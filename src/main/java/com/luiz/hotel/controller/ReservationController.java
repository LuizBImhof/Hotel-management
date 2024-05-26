package com.luiz.hotel.controller;

import com.luiz.hotel.reservation.ReservationDto;
import com.luiz.hotel.reservation.ReservationEntity;
import com.luiz.hotel.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping(value = "/",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReservationDto>> getAllReservations(){
        return ResponseEntity.ok().body(reservationService.getAllReservations());
    }

    @PostMapping(value = "/")
    public ResponseEntity<ReservationDto> saveReservation(@RequestBody ReservationDto reservation){
        return ResponseEntity.ok().body(reservationService.saveReservation(reservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservationById(@PathVariable Long id){
        this.reservationService.deleteReservationById(id);
        return ResponseEntity.ok().body("Deleted reservation with id : " + id + " successfully");
    }




    @PostMapping(value = "/check-in")
    public ResponseEntity<ReservationEntity> doCHeckIn(){
        //todo fazer metodo no service
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
