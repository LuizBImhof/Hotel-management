package com.luiz.hotel.dtos;

import com.luiz.hotel.entities.*;

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
