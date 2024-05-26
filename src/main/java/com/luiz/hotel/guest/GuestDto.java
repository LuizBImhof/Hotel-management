package com.luiz.hotel.guest;

public record GuestDto(String name, String document, String phone) {
    public GuestDto(GuestEntity guest){
        this(guest.getName(), guest.getDocument(), guest.getPhone());
    }
}
