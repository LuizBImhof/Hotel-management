package com.luiz.hotel.reservation;

import com.luiz.hotel.guest.GuestDto;
import com.luiz.hotel.guest.GuestEntity;
import com.luiz.hotel.guest.GuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public ReservationDto saveReservation(ReservationDto reservationData) throws Exception {
        final Optional<GuestDto> guest = guestService.getGuestByDocument(reservationData.guest_document());

        final ReservationEntity reservation = new ReservationEntity(reservationData);
        if (guest.isPresent()) {
            reservation.setGuest(guest.map(GuestEntity::new).get());
        } else {
            log.info("User not found");
            throw new Exception("Usuário não encontrado");
        }

        reservationRepository.save(reservation);
        log.info("Reservation saved");
        return reservationData;

    }

    public void deleteReservationById(Long id) {
        reservationRepository.deleteById(id);
    }

    public Optional<ReservationDto> doCheckIn(Long id) throws Exception {
        checkIfAvailableForCheckIn();
        final Optional<ReservationEntity> reservation = this.reservationRepository.findById(id);
        if (reservation.isPresent()) {
            if (reservation.get().getCheck_in() == null) {
                reservation.get().setCheck_in(LocalDateTime.now());
                this.reservationRepository.save(reservation.get());
            } else {
                log.info("check in já efetuado");
                throw new Exception("Reserva já possui check-in");
            }
        } else {
            log.info("reserva não encontrada");
            throw new Exception("Reserva não encontrada");
        }
        return reservation.map(ReservationDto::new);
    }

    private static void checkIfAvailableForCheckIn() throws Exception {
        if (LocalDateTime.now().getHour() <= 14) {
            log.info("check in não permitido (antes das 14h)");
            throw new Exception("Não é permitido check-in antes das 14h");
        }
    }

    public Optional<CheckOutResponseDto> doCheckOut(Long id) {
        int numberOfWeekDays = 0;
        int numberOfWeekEndDays = 0;
        int price = 0;
        int vehiclePrice = 0;
        int lateCheckoutPrice = 0;

        final Optional<ReservationEntity> reservation = this.reservationRepository.findById(id);

        if (reservation.isPresent()) {
            LocalDateTime checkIn = reservation.get().getCheck_in();
            LocalDateTime checkOut = LocalDateTime.now();
            for (LocalDate date = LocalDate.from(checkIn); date.isBefore(LocalDate.from(checkOut)) || date.isEqual(LocalDate.from(checkOut)); date = date.plusDays(1)) {
                if (date.getDayOfWeek().ordinal() == 6 || date.getDayOfWeek().ordinal() == 7) {
                    numberOfWeekEndDays++;
                } else {
                    numberOfWeekDays++;
                }
            }

            price = (numberOfWeekDays * 120) + (numberOfWeekEndDays * 180);
            if (reservation.get().isVehicle()) {
                vehiclePrice = (numberOfWeekDays * 15) + (numberOfWeekEndDays * 20);
                price = price + vehiclePrice;
            }
            if (checkOut.getHour() >= 12) {
                if (checkOut.getDayOfWeek().ordinal() == 6 || checkOut.getDayOfWeek().ordinal() == 7) {
                    lateCheckoutPrice = 120 / 2;
                } else {
                    lateCheckoutPrice = 180 / 2;
                }
                price = price + lateCheckoutPrice;
            }

            reservation.get().setCheck_out(checkOut);

            GuestDto guest = new GuestDto(reservation.get().getGuest());
            CheckOutResponseDto responseDto = new CheckOutResponseDto(price, numberOfWeekDays, numberOfWeekEndDays, vehiclePrice, lateCheckoutPrice, guest);

            return Optional.of(responseDto);
        }

        return Optional.empty();
    }
}
