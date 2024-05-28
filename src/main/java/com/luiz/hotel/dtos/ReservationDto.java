package com.luiz.hotel.dtos;

import com.luiz.hotel.entities.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationDto(Long id, LocalDate start_date, LocalDate end_date, LocalDateTime check_in, LocalDateTime check_out,
                             boolean vehicle, Integer price, String guest_document) {
    public ReservationDto(ReservationEntity reservation) {
        this(reservation.getId(),
                reservation.getStart_date(),
                reservation.getEnd_date(),
                reservation.getCheck_in(),
                reservation.getCheck_out(),
                reservation.isVehicle(),
                reservation.getPrice(),
                reservation.getGuest().getDocument());
    }
}
