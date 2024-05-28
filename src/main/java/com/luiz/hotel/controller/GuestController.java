package com.luiz.hotel.controller;

import com.luiz.hotel.dtos.GuestDto;
import com.luiz.hotel.services.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("guest")
@RequiredArgsConstructor
public class GuestController {
    private final GuestService guestService;

    @GetMapping(value = "/",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        return ResponseEntity.ok().body(guestService.getAllGuests());
    }

    @GetMapping(value = "/filter",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestDto> getGuestByParameter(
            @RequestParam(
                    value = "document",
                    required = false) final String document,
            @RequestParam(
                    value = "phone",
                    required = false) final String phone,
            @RequestParam(
                    value = "name",
                    required = false) final String name
    ) {
        final Optional<GuestDto> guest = this.guestService.getGuestByParameter(document, phone, name);
        return guest.map(guestDto -> ResponseEntity.ok().body(guestDto)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PostMapping("/")
    public ResponseEntity<GuestDto> saveGuest(@RequestBody GuestDto guest) {

        return ResponseEntity.ok().body(guestService.saveGuest(guest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGuestById(@PathVariable Long id) {
        this.guestService.deleteGuestById(id);
        return ResponseEntity.ok().body("Deleted guest with id : " + id + " successfully");
    }

    @GetMapping("/guestsInHotel")
    public ResponseEntity<List<GuestDto>> getGuestsCurrentlyInHotel(){
        return ResponseEntity.ok().body(this.guestService.getGuestsCurrentlyInHotel());
    }

    @GetMapping("/guestsWithReservationAndNotCheckedIn")
    public ResponseEntity<List<GuestDto>> getGuestsWithReservationAndNotCheckedIn(){
        return ResponseEntity.ok().body(this.guestService.getGuestsWithReservationAndNotCheckedIn());
    }

}
