package com.luiz.hotel.guest;

import com.luiz.hotel.reservation.ReservationDto;

import java.util.Collections;
import java.util.List;


public record GuestDto(Long id, String name, String document, String phone, List<ReservationDto> reservations) {
    public GuestDto(GuestEntity guest){
        this(guest.getId(),
                guest.getName(),
                guest.getDocument(),
                guest.getPhone(),
                guest.getReservations() == null ? Collections.emptyList() : guest.getReservations().stream().map(ReservationDto::new).toList());
    }

}
