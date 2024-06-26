package com.luiz.hotel.services;

import ch.qos.logback.core.util.StringUtil;
import com.luiz.hotel.dtos.*;
import com.luiz.hotel.entities.*;
import com.luiz.hotel.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestService {
    private final GuestRepository guestRepository;

    public List<GuestDto> getAllGuests() {
        log.info("Get all guests");
        final List<GuestEntity> result = guestRepository.findAll();
        return result.stream().map(GuestDto::new).toList();
    }

    public GuestDto saveGuest(GuestDto guestData) {

        final GuestEntity guest = new GuestEntity(guestData);

        guestRepository.save(guest);
        log.info("Guest successfully inserted id: {}", guest.getId());
        return guestData;
    }

    public void deleteGuestById(Long id) {
        guestRepository.deleteById(id);
    }

    public Optional<GuestDto> getGuestByDocument(String document) {
        return guestRepository.findGuestByDocument(document).map(GuestDto::new);
    }

    public Optional<GuestDto> getGuestByPhone(String phone) {
        return guestRepository.findGuestByPhone(phone).map(GuestDto::new);
    }

    public Optional<GuestDto> getGuestByName(String name) {
        return guestRepository.findGuestByName(name).map(GuestDto::new);
    }

    public Optional<GuestDto> getGuestByParameter(String document, String phone, String name) {
        if (!StringUtil.isNullOrEmpty(document)) {
            return this.getGuestByDocument(document);
        }
        if (!StringUtil.isNullOrEmpty(phone)) {
            return this.getGuestByPhone(phone);
        }
        if (!StringUtil.isNullOrEmpty(name)) {
            return this.getGuestByName(name);
        }
        return Optional.empty();
    }

    public List<GuestDto> getGuestsCurrentlyInHotel() {
        return guestRepository.findGuestsInHotel().stream().map(GuestDto::new).toList();
    }

    public List<GuestDto> getGuestsWithReservationAndNotCheckedIn() {
        return guestRepository.findGuestsWithReservationAndNotCheckedIn().stream().map(GuestDto::new).toList();
    }
}
