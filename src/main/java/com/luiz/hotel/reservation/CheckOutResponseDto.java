package com.luiz.hotel.reservation;

import com.luiz.hotel.guest.GuestDto;

import java.time.LocalDateTime;

public record CheckOutResponseDto(LocalDateTime checkIn, LocalDateTime checkOut, Integer finalPrice, Integer weekDays, Integer weekendDays, Integer vehiclePrice, Integer lateCheckOutPrice, GuestDto guest) {
}
