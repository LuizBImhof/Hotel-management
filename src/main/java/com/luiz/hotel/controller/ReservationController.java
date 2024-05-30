package com.luiz.hotel.controller;

import com.luiz.hotel.dtos.CheckOutResponseDto;
import com.luiz.hotel.dtos.ReservationDto;
import com.luiz.hotel.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("reservation")
@RequiredArgsConstructor
@CrossOrigin
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping(value = "/",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReservationDto>> getAllReservations(){
        return ResponseEntity.ok().body(reservationService.getAllReservations());
    }

    @PostMapping(value = "/")
    public ResponseEntity<ReservationDto> saveReservation(@RequestBody ReservationDto reservation) throws Exception {
        return ResponseEntity.ok().body(reservationService.saveReservation(reservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservationById(@PathVariable Long id){
        this.reservationService.deleteReservationById(id);
        return ResponseEntity.ok().body("Deleted reservation with id : " + id + " successfully");
    }

    @PostMapping(value = "/check-in/{id}")
    public ResponseEntity<ReservationDto> doCHeckIn(@PathVariable Long id) throws Exception {
        final Optional<ReservationDto> response;
        response = reservationService.doCheckIn(id);
        return ResponseEntity.ok().body(response.get());
    }
    @PostMapping(value = "/check-out/{id}")
    public ResponseEntity<CheckOutResponseDto> doCHeckOut(@PathVariable Long id) throws Exception {
        final Optional<CheckOutResponseDto> response;
        response = reservationService.doCheckOut(id);
        return ResponseEntity.ok().body(response.get());
    }
}
