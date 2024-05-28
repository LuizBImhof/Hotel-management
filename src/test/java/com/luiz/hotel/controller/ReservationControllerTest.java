package com.luiz.hotel.controller;


import com.luiz.hotel.reservation.CheckOutResponseDto;
import com.luiz.hotel.reservation.ReservationDto;
import com.luiz.hotel.reservation.ReservationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @Test
    @DisplayName("Should return all reservations when getAllReservations is called")
    public void shouldReturnAllReservationsWhenGetAllReservationsIsCalled() {
        List<ReservationDto> expectedReservations = Arrays.asList(
                new ReservationDto(1L, null, null, null, null, false, 0, "123456789"),
                new ReservationDto(2L, null, null, null, null, false, 0, "123456789"));
        when(reservationService.getAllReservations()).thenReturn(expectedReservations);

        ResponseEntity<List<ReservationDto>> response = reservationController.getAllReservations();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedReservations, response.getBody());
    }

    @Test
    @DisplayName("Should save reservation when saveReservation is called")
    public void shouldSaveReservationWhenSaveReservationIsCalled() throws Exception {
        ReservationDto expectedReservation = new ReservationDto(1L, null, null, null, null, false, 0, "123456789");
        when(reservationService.saveReservation(expectedReservation)).thenReturn(expectedReservation);

        ResponseEntity<ReservationDto> response = reservationController.saveReservation(new ReservationDto(1L, null, null, null, null, false, 0, "123456789"));

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedReservation, response.getBody());
    }

    @Test
    @DisplayName("Should delete reservation when deleteReservationById is called")
    public void shouldDeleteReservationWhenDeleteReservationByIdIsCalled() {
        Long id = 1L;
        doNothing().when(reservationService).deleteReservationById(id);

        ResponseEntity<String> response = reservationController.deleteReservationById(id);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Deleted reservation with id : " + id + " successfully", response.getBody());
    }

    @Test
    @DisplayName("Should perform check-in when doCheckIn is called and reservation exists")
    public void shouldPerformCheckInWhenDoCheckInIsCalledAndReservationExists() throws Exception {
        ReservationDto expectedReservation = new ReservationDto(1L, null, null, null, null, false, 0, "123456789");
        when(reservationService.doCheckIn(anyLong())).thenReturn(Optional.of(expectedReservation));

        ResponseEntity<ReservationDto> response = reservationController.doCHeckIn(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedReservation, response.getBody());
    }

    @Test
    @DisplayName("Should perform check-out when doCheckOut is called and reservation exists")
    public void shouldPerformCheckOutWhenDoCheckOutIsCalledAndReservationExists() throws Exception {
        CheckOutResponseDto expectedResponse = new CheckOutResponseDto(
                LocalDateTime.now(),
                LocalDateTime.now(),
                0,
                0,
                0,
                0,
                0,
                null
        );
        when(reservationService.doCheckOut(anyLong())).thenReturn(Optional.of(expectedResponse));

        ResponseEntity<CheckOutResponseDto> response = reservationController.doCHeckOut(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedResponse, response.getBody());
    }
}