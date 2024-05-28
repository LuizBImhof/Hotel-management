package com.luiz.hotel.reservation;

import com.luiz.hotel.dtos.*;
import com.luiz.hotel.entities.*;
import com.luiz.hotel.repositories.*;
import com.luiz.hotel.services.*;
import com.luiz.hotel.utils.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.dao.*;

import java.time.*;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private GuestService guestService;

    @Mock
    @MockBean
    private Clock clock;


    @Test
    @DisplayName("Should save reservation when guest exists")
    public void shouldSaveReservationWhenGuestExists() throws Exception {
        ReservationDto reservationDto = new ReservationDto(1L, null, null, null, null, false, 0, "123456789");
        ReservationEntity reservationEntity = new ReservationEntity(reservationDto);
        GuestDto guestDto = new GuestDto(1L, "Luiz", "123456789", "123456789", null);

        when(guestService.getGuestByDocument(anyString())).thenReturn(Optional.of(guestDto));
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservationEntity);

        ReservationDto result = reservationService.saveReservation(reservationDto);

        verify(reservationRepository, times(1)).save(any(ReservationEntity.class));
        verify(guestService, times(1)).getGuestByDocument(anyString());


        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.guest_document(), reservationDto.guest_document());
    }

    @Test
    @DisplayName("Should throw exception when guest does not exist")
    public void shouldThrowExceptionWhenGuestDoesNotExist() {
        ReservationDto reservationDto = new ReservationDto(1L, null, null, null, null, false, 0, "123456789");

        when(guestService.getGuestByDocument(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(Exception.class, () -> reservationService.saveReservation(reservationDto));
        verify(guestService, times(1)).getGuestByDocument(anyString());
    }

    @Test
    @DisplayName("Should return all reservations when reservations exist")
    public void shouldReturnAllReservationsWhenReservationsExist() {
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setGuest(new GuestEntity());
        List<ReservationEntity> reservationEntities = Collections.singletonList(reservationEntity);

        when(reservationRepository.findAll()).thenReturn(reservationEntities);

        List<ReservationDto> result = reservationService.getAllReservations();

        verify(reservationRepository, times(1)).findAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(reservationEntity.getId(), result.get(0).id());
    }

    @Test
    @DisplayName("Should return empty list when no reservations exist")
    public void shouldReturnEmptyListWhenNoReservationsExist() {
        when(reservationRepository.findAll()).thenReturn(Collections.emptyList());

        List<ReservationDto> result = reservationService.getAllReservations();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
    @Test
    @DisplayName("Should delete reservation by id successfully")
    public void shouldDeleteReservationByIdSuccessfully() {
        Long id = 1L;
        doNothing().when(reservationRepository).deleteById(id);

        reservationService.deleteReservationById(id);

        verify(reservationRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing reservation")
    public void shouldThrowExceptionWhenDeletingNonExistingReservation() {
        Long id = 1L;
        doThrow(EmptyResultDataAccessException.class).when(reservationRepository).deleteById(id);

        verify(reservationRepository, times(0)).deleteById(id);

        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> reservationService.deleteReservationById(id));
    }

    @Test
    @DisplayName("Should perform check-in when reservation exists and check-in is not done and time is after 14h")
    public void shouldPerformCheckInWhenReservationExistsAndCheckInIsNotDoneAndTimeIsAfter14h() throws Exception {
        Long id = 1L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setCheck_in(null);
        reservationEntity.setGuest(new GuestEntity());
        Clock fixedClock = prepareClock(LocalDateTime.of(2024, 5, 27, 14, 0, 0));
        setupMocksForClock(fixedClock);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationEntity));


        Optional<ReservationDto> result = reservationService.doCheckIn(id);

        verify(reservationRepository, times(1)).save(reservationEntity);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertNotNull(result.get().check_in());
    }

    @Test
    @DisplayName("Should throw exception when check-in is before 14h")
    public void shouldThrowExceptionWhenCheckInIsBefore14h() throws Exception {
        Long id = 1L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setCheck_in(null);
        Clock fixedClock = prepareClock(LocalDateTime.of(2024, 5, 27, 13, 59, 0));
        setupMocksForClock(fixedClock);

        Assertions.assertThrows(Exception.class, () -> reservationService.doCheckIn(id));
    }

    @Test
    @DisplayName("Should throw exception when reservation does not exist")
    public void shouldThrowExceptionWhenReservationDoesNotExist() {
        Long id = 1L;
        Clock fixedClock = prepareClock(LocalDateTime.of(2024, 5, 27, 14, 0, 0));
        setupMocksForClock(fixedClock);

        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(Exception.class, () -> reservationService.doCheckIn(id));
    }

    @Test
    @DisplayName("Should throw exception when check-in is already done")
    public void shouldThrowExceptionWhenCheckInIsAlreadyDone() {
        Long id = 1L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setCheck_in(LocalDateTime.now());
        Clock fixedClock = prepareClock(LocalDateTime.of(2024, 5, 27, 14, 0, 0));
        setupMocksForClock(fixedClock);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationEntity));

        Assertions.assertThrows(Exception.class, () -> reservationService.doCheckIn(id));
    }

    @Test
    @DisplayName("Should return empty when reservation does not exist")
    public void shouldReturnEmptyWhenReservationDoesNotExist() throws Exception {
        Long id = 1L;
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        Optional<CheckOutResponseDto> result = reservationService.doCheckOut(id);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when reservation is not yet checked in")
    public void shouldThrowExceptionWhenReservationIsNotYetCheckedIn() {
        Long id = 1L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setGuest(new GuestEntity());
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationEntity));

        Assertions.assertThrows(Exception.class, () -> reservationService.doCheckOut(id));
    }

    @Test
    @DisplayName("Should calculate checkout price correctly reservation is one weekday")
    public void shouldCalculateCheckOutPriceCorrectlyWhenReservationIsOneWeekday() throws Exception {
        Integer expectedValue = Constants.WEEKDAYPRICE;
        Long id = 1L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setGuest(new GuestEntity());
        reservationEntity.setCheck_in(LocalDateTime.of(2024, 5, 20, 14, 0, 0));
        reservationEntity.setVehicle(false);
        Clock fixedClock = prepareClock(LocalDateTime.of(2024, 5, 21, 10, 0, 0));
        setupMocksForClock(fixedClock);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationEntity));

        Optional<CheckOutResponseDto> result = reservationService.doCheckOut(id);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedValue, result.get().finalPrice());
    }
    @Test
    @DisplayName("Should calculate checkout price correctly reservation is one weekend day")
    public void shouldCalculateCheckOutPriceCorrectlyWhenReservationIsOneWeekendDay() throws Exception {
        Integer expectedValue = Constants.WEEKENDPRICE;
        Long id = 1L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setGuest(new GuestEntity());
        reservationEntity.setCheck_in(LocalDateTime.of(2024, 5, 25, 14, 0, 0));
        reservationEntity.setVehicle(false);
        Clock fixedClock = prepareClock(LocalDateTime.of(2024, 5, 26, 10, 0, 0));
        setupMocksForClock(fixedClock);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationEntity));

        Optional<CheckOutResponseDto> result = reservationService.doCheckOut(id);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedValue, result.get().finalPrice());
    }

    @Test
    @DisplayName("Should calculate checkout price correctly reservation is one weekday and has vehicle")
    public void shouldCalculateCheckOutPriceCorrectlyWhenReservationIsOneWeekdayAndHasVehicle() throws Exception {
        Integer expectedValue = Constants.WEEKDAYPRICE + Constants.VEHICLEWEEKPRICE;
        Long id = 1L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setGuest(new GuestEntity());
        reservationEntity.setCheck_in(LocalDateTime.of(2024, 5, 20, 14, 0, 0));
        reservationEntity.setVehicle(true);
        Clock fixedClock = prepareClock(LocalDateTime.of(2024, 5, 21, 10, 0, 0));
        setupMocksForClock(fixedClock);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationEntity));

        Optional<CheckOutResponseDto> result = reservationService.doCheckOut(id);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedValue, result.get().finalPrice());
    }

    @Test
    @DisplayName("Should calculate checkout price correctly reservation is two weekend day and a late checkout")
    public void shouldCalculateCheckOutPriceCorrectlyWhenReservationIsTwoWeekendDayAndLateCheckout() throws Exception {
        Integer expectedValue = Constants.WEEKENDPRICE*2 + (Constants.WEEKENDPRICE / 2);
        Long id = 1L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setGuest(new GuestEntity());
        reservationEntity.setCheck_in(LocalDateTime.of(2024, 5, 25, 14, 0, 0));
        reservationEntity.setVehicle(false);
        Clock fixedClock = prepareClock(LocalDateTime.of(2024, 5, 27, 13, 0, 0));
        setupMocksForClock(fixedClock);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationEntity));

        Optional<CheckOutResponseDto> result = reservationService.doCheckOut(id);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedValue, result.get().finalPrice());
    }

    @Test
    @DisplayName("Should calculate checkout price correctly reservation is a weekday and a late checkout")
    public void shouldCalculateCheckOutPriceCorrectlyWhenReservationIsAWeekdayAndLateCheckout() throws Exception {
        Integer expectedValue = Constants.WEEKDAYPRICE + (Constants.WEEKDAYPRICE / 2);
        Long id = 1L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setGuest(new GuestEntity());
        reservationEntity.setCheck_in(LocalDateTime.of(2024, 5, 20, 14, 0, 0));
        reservationEntity.setVehicle(false);
        Clock fixedClock = prepareClock(LocalDateTime.of(2024, 5, 21, 13, 0, 0));
        setupMocksForClock(fixedClock);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationEntity));

        Optional<CheckOutResponseDto> result = reservationService.doCheckOut(id);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedValue, result.get().finalPrice());
    }

    private Clock prepareClock (LocalDateTime time) {
        return Clock
                .fixed(time.toInstant(ZoneOffset.ofHours(-3)), ZoneId.systemDefault());
    }

    private void setupMocksForClock(Clock mockClock) {
        when(clock.instant()).thenReturn(mockClock.instant());
        when(clock.getZone()).thenReturn(mockClock.getZone());
    }

}