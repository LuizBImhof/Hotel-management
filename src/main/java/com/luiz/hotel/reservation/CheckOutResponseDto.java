package com.luiz.hotel.reservation;

import com.luiz.hotel.guest.GuestDto;

public record CheckOutResponseDto(Integer finalPrice, Integer weekDays, Integer weekendDays, Integer vehiclePrice, Integer lateCheckOutPrice, GuestDto guest) {
}
