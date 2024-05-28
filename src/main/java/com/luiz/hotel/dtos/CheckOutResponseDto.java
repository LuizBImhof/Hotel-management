package com.luiz.hotel.dtos;

import java.time.LocalDateTime;

public record CheckOutResponseDto(LocalDateTime checkIn, LocalDateTime checkOut, Integer finalPrice, Integer weekDays, Integer weekendDays, Integer vehiclePrice, Integer lateCheckOutPrice, GuestDto guest) {
}
