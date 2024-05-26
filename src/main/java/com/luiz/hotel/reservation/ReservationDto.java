package com.luiz.hotel.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationDto(LocalDate start_date, LocalDate end_date, LocalDateTime check_in, LocalDateTime check_out,
                             boolean vehicle, Integer price, Long guest_id) {
    public ReservationDto(ReservationEntity reservation) {
        this(reservation.getStart_date(), reservation.getEnd_date(), reservation.getCheck_in(),
                reservation.getCheck_out(), reservation.isVehicle(), reservation.getPrice(), reservation.getGuest().getId());
    }
}
