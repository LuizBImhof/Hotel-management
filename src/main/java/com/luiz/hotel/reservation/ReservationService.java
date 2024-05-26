package com.luiz.hotel.reservation;

import com.luiz.hotel.guest.GuestDto;
import com.luiz.hotel.guest.GuestEntity;
import com.luiz.hotel.guest.GuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    @Autowired
    private final ReservationRepository reservationRepository;

    @Autowired
    private final GuestService guestService;

    public List<ReservationDto> getAllReservations() {
        log.info("Get all reservations");
        final List<ReservationEntity> result = reservationRepository.findAll();
        return result.stream().map(ReservationDto::new).toList();
    }

    public ReservationDto saveReservation(ReservationDto reservationData) {
        final Optional<GuestEntity> guest = guestService.getGuestById(reservationData.guest_id());

        final ReservationEntity reservation = new ReservationEntity(reservationData);
        if(guest.isPresent()){
        reservation.setGuest(guest.get());
        } else {
            log.info("User not found");
            return null;//todo send exception
        }


        reservationRepository.save(reservation);
        log.info("Reservation saved");
        return reservationData;
        //return Optional.of(reservation).map(ReservationDto::new).get();
    }

    public void deleteReservationById(Long id) {
        reservationRepository.deleteById(id);
    }

}
