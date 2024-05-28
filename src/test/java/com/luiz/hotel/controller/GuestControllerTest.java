package com.luiz.hotel.controller;

import com.luiz.hotel.dtos.GuestDto;
import com.luiz.hotel.entities.GuestEntity;
import com.luiz.hotel.services.GuestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GuestControllerTest{
    @Mock
    private GuestService guestService;

    @InjectMocks
    private GuestController guestController;

    @Test
    @DisplayName("Should save guest correctly")
    public void testSaveGuest() {
        GuestDto guestDto = new GuestDto(1L, "a", "2", "2", Collections.emptyList());

        when(guestService.saveGuest(any(GuestDto.class))).thenReturn(guestDto);

        ResponseEntity<GuestDto> result = guestController.saveGuest(guestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("a", Objects.requireNonNull(result.getBody()).name());
        verify(guestService, times(1)).saveGuest(any(GuestDto.class));
    }
    @Test
    @DisplayName("Should return all guests when guests exist")
    public void shouldReturnAllGuestsWhenGuestsExist() {
        GuestEntity guestEntity1 = new GuestEntity(new GuestDto(1L,"a","1","1", Collections.emptyList()));
        GuestEntity guestEntity2 = new GuestEntity(new GuestDto(2L,"b","2","2", Collections.emptyList()));
        List<GuestEntity> guestEntities = Arrays.asList(guestEntity1, guestEntity2);
        when(guestService.getAllGuests()).thenReturn(guestEntities.stream().map(GuestDto::new).collect(Collectors.toList()));

        ResponseEntity<List<GuestDto>> result = guestController.getAllGuests();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, Objects.requireNonNull(result.getBody()).size());
        Assertions.assertEquals("a", result.getBody().get(0).name());
        Assertions.assertEquals("b", result.getBody().get(1).name());
    }

    @Test
    @DisplayName("Should return empty list when there are no guests")
    public void shouldReturnEmptyWhenThereAreNoGuests() {
        when(guestService.getAllGuests()).thenReturn(Collections.emptyList());

        ResponseEntity<List<GuestDto>> result = guestController.getAllGuests();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(Objects.requireNonNull(result.getBody()).isEmpty());
    }

    @Test
    @DisplayName("Should return guest when guest exists by given parameters")
    public void shouldReturnGuestWhenGuestExistsByGivenParameters() {
        String document = "123456";
        String phone = "1234567890";
        String name = "John Doe";
        GuestDto guestDto = new GuestDto(1L, name, document, phone, Collections.emptyList());
        when(guestService.getGuestByParameter(document, phone, name)).thenReturn(Optional.of(guestDto));

        ResponseEntity<GuestDto> result = guestController.getGuestByParameter(document, phone, name);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(name, Objects.requireNonNull(result.getBody()).name());
        Assertions.assertEquals(document, result.getBody().document());
        Assertions.assertEquals(phone, result.getBody().phone());
    }

    @Test
    @DisplayName("Should return no content when guest does not exist by given parameters")
    public void shouldReturnNoContentWhenGuestDoesNotExistByGivenParameters() {
        String document = "123456";
        String phone = "1234567890";
        String name = "John Doe";
        when(guestService.getGuestByParameter(document, phone, name)).thenReturn(Optional.empty());

        ResponseEntity<GuestDto> result = guestController.getGuestByParameter(document, phone, name);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    @DisplayName("Should delete guest by id successfully")
    public void shouldDeleteGuestByIdSuccessfully() {
        Long id = 1L;
        doNothing().when(guestService).deleteGuestById(id);

        ResponseEntity<String> result = guestController.deleteGuestById(id);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals("Deleted guest with id : " + id + " successfully", result.getBody());
    }

    @Test
    @DisplayName("Should return all guests currently in hotel")
    public void shouldReturnAllGuestsCurrentlyInHotel() {
        GuestDto guestDto = new GuestDto(1L, "a", "1", "1", Collections.emptyList());
        List<GuestDto> guestDtos = Collections.singletonList(guestDto);
        when(guestService.getGuestsCurrentlyInHotel()).thenReturn(guestDtos);

        ResponseEntity<List<GuestDto>> result = guestController.getGuestsCurrentlyInHotel();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, Objects.requireNonNull(result.getBody()).size());
        Assertions.assertEquals("a", result.getBody().get(0).name());
    }

    @Test
    @DisplayName("Should return all guests with reservation and not checked in")
    public void shouldReturnAllGuestsWithReservationAndNotCheckedIn() {
        GuestDto guestDto = new GuestDto(1L, "a", "1", "1", Collections.emptyList());
        List<GuestDto> guestDtos = Collections.singletonList(guestDto);
        when(guestService.getGuestsWithReservationAndNotCheckedIn()).thenReturn(guestDtos);

        ResponseEntity<List<GuestDto>> result = guestController.getGuestsWithReservationAndNotCheckedIn();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, Objects.requireNonNull(result.getBody()).size());
        Assertions.assertEquals("a", result.getBody().get(0).name());
    }
}