package com.luiz.hotel.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.luiz.hotel.dtos.*;
import com.luiz.hotel.entities.*;
import com.luiz.hotel.repositories.*;
import com.luiz.hotel.services.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private GuestService guestService;

    @Test
    @DisplayName("Should save guest correctly")
    public void testSaveGuest() {
        // Arrange
        GuestDto guestDto = new GuestDto(1L, "a", "2", "2", Collections.emptyList());
        GuestEntity guestEntity = new GuestEntity(guestDto);

        when(guestRepository.save(any(GuestEntity.class))).thenReturn(guestEntity);

        // Act
        GuestDto result = guestService.saveGuest(guestDto);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.document(), guestDto.document());
        verify(guestRepository, times(1)).save(any(GuestEntity.class));

    }

    @Test
    @DisplayName("Should get all guests correctly even with a null reservation list")
    public void testGetAllGuestsWithNullList() {
        // Arrange
        GuestEntity guestEntity1 = new GuestEntity(new GuestDto(1L, "a", "1", "1", Collections.emptyList()));

        GuestEntity guestEntity2 = new GuestEntity(new GuestDto(2L, "b", "2", "2", null));

        List<GuestEntity> guestEntities = Arrays.asList(guestEntity1, guestEntity2);
        when(guestRepository.findAll()).thenReturn(guestEntities);

        // Act
        List<GuestDto> result = guestService.getAllGuests();

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("a", result.get(0).name());
        Assertions.assertEquals("b", result.get(1).name());
        verify(guestRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should delete guest by id when guest exists")
    public void shouldDeleteGuestByIdWhenGuestExists() {
        Long id = 1L;
        doNothing().when(guestRepository).deleteById(id);

        guestService.deleteGuestById(id);

        verify(guestRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should handle exception when deleting non-existing guest")
    public void shouldHandleExceptionWhenDeletingNonExistingGuest() {
        Long id = 1L;
        doThrow(new EmptyResultDataAccessException(0)).when(guestRepository).deleteById(id);

        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> guestService.deleteGuestById(id));
        verify(guestRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should return guest by document when document is provided")
    public void shouldReturnGuestByDocumentWhenDocumentIsProvided() {
        // Arrange
        String document = "123456";
        GuestEntity guestEntity = new GuestEntity(new GuestDto(1L, "a", document, "a", Collections.emptyList()));
        when(guestRepository.findGuestByDocument(document)).thenReturn(Optional.of(guestEntity));

        // Act
        Optional<GuestDto> result = guestService.getGuestByParameter(document, null, null);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(document, result.get().document());
    }

    @Test
    @DisplayName("Should return guest by phone when phone is provided")
    public void shouldReturnGuestByPhoneWhenPhoneIsProvided() {
        // Arrange
        String phone = "123456";
        GuestEntity guestEntity = new GuestEntity(new GuestDto(1L, "a", "a", phone, Collections.emptyList()));
        when(guestRepository.findGuestByPhone(phone)).thenReturn(Optional.of(guestEntity));

        // Act
        Optional<GuestDto> result = guestService.getGuestByParameter(null, phone, null);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(phone, result.get().phone());
    }

    @Test
    @DisplayName("Should return guest by name when name is provided")
    public void shouldReturnGuestByNameWhenNameIsProvided() {
        // Arrange
        String name = "John Doe";
        GuestEntity guestEntity = new GuestEntity(new GuestDto(1L, name, "1", "1", Collections.emptyList()));
        when(guestRepository.findGuestByName(name)).thenReturn(Optional.of(guestEntity));

        // Act
        Optional<GuestDto> result = guestService.getGuestByParameter(null, null, name);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(name, result.get().name());
    }

    @Test
    @DisplayName("Should return empty when no parameters are provided")
    public void shouldReturnEmptyWhenNoParametersAreProvided() {
        // Act
        Optional<GuestDto> result = guestService.getGuestByParameter(null, null, null);

        // Assert
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return guests currently in hotel when there are guests")
    public void shouldReturnGuestsCurrentlyInHotelWhenThereAreGuests() {
        GuestEntity guestEntity1 = new GuestEntity(new GuestDto(1L, "a", "1", "1", Collections.emptyList()));
        GuestEntity guestEntity2 = new GuestEntity(new GuestDto(2L, "b", "2", "2", Collections.emptyList()));
        List<GuestEntity> guestEntities = Arrays.asList(guestEntity1, guestEntity2);
        when(guestRepository.findGuestsInHotel()).thenReturn(guestEntities);

        List<GuestDto> result = guestService.getGuestsCurrentlyInHotel();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("a", result.get(0).name());
        Assertions.assertEquals("b", result.get(1).name());
    }

    @Test
    @DisplayName("Should return empty list when there are no guests currently in hotel")
    public void shouldReturnEmptyWhenThereAreNoGuestsCurrentlyInHotel() {
        when(guestRepository.findGuestsInHotel()).thenReturn(Collections.emptyList());

        List<GuestDto> result = guestService.getGuestsCurrentlyInHotel();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return guests with reservation and not checked in when there are such guests")
    public void shouldReturnGuestsWithReservationAndNotCheckedInWhenThereAreSuchGuests() {
        GuestEntity guestEntity1 = new GuestEntity(new GuestDto(1L, "a", "1", "1", Collections.emptyList()));
        GuestEntity guestEntity2 = new GuestEntity(new GuestDto(2L, "b", "2", "2", Collections.emptyList()));
        List<GuestEntity> guestEntities = Arrays.asList(guestEntity1, guestEntity2);
        when(guestRepository.findGuestsWithReservationAndNotCheckedIn()).thenReturn(guestEntities);

        List<GuestDto> result = guestService.getGuestsWithReservationAndNotCheckedIn();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("a", result.get(0).name());
        Assertions.assertEquals("b", result.get(1).name());
    }

    @Test
    @DisplayName("Should return empty list when there are no guests with reservation and not checked in")
    public void shouldReturnEmptyWhenThereAreNoGuestsWithReservationAndNotCheckedIn() {
        when(guestRepository.findGuestsWithReservationAndNotCheckedIn()).thenReturn(Collections.emptyList());

        List<GuestDto> result = guestService.getGuestsWithReservationAndNotCheckedIn();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}
