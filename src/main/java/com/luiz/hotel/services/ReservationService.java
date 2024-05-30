package com.luiz.hotel.services;

import com.luiz.hotel.dtos.*;
import com.luiz.hotel.entities.*;
import com.luiz.hotel.repositories.*;
import com.luiz.hotel.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    @Autowired
    private final ReservationRepository reservationRepository;

    @Autowired
    private final GuestService guestService;

    @Autowired
    private final Clock clock;


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
        LocalDateTime checkIn = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES);
        checkIfAvailableForCheckIn(checkIn);
        final Optional<ReservationEntity> reservation = this.reservationRepository.findById(id);
        if (reservation.isPresent()) {
            if (reservation.get().getCheck_in() == null) {
                reservation.get().setCheck_in(checkIn);
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

    private static void checkIfAvailableForCheckIn(LocalDateTime checkIn) throws Exception {
        if (checkIn.getHour() < 14) {
            log.info("check in não permitido (antes das 14h)");
            throw new Exception("Não é permitido check-in antes das 14h");
        }
    }

    public Optional<CheckOutResponseDto> doCheckOut(Long id) throws Exception {
        final Optional<ReservationEntity> reservation = this.reservationRepository.findById(id);

        if (reservation.isPresent()) {
            LocalDateTime checkIn = reservation.get().getCheck_in();
            if (checkIn == null) {
                throw new Exception("Reservation is not checked in");
            }
            LocalDateTime checkOut = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES);
            reservation.get().setCheck_out(checkOut);

            int[] dayCounts = countDays(checkIn, checkOut);
            int price = calculatePrice(reservation.get(), dayCounts);

            GuestDto guest = new GuestDto(reservation.get().getGuest());
            CheckOutResponseDto responseDto = new CheckOutResponseDto(checkIn, checkOut, price, dayCounts[0], dayCounts[1], dayCounts[2], dayCounts[3], guest);

            return Optional.of(responseDto);
        }

        return Optional.empty();
    }

    private int[] countDays(LocalDateTime checkIn, LocalDateTime checkOut) {
        int numberOfWeekDays = 0;
        int numberOfWeekEndDays = 0;
        int vehiclePrice = 0;
        int lateCheckoutPrice = 0;

        for (LocalDate date = LocalDate.from(checkIn); date.isBefore(LocalDate.from(checkOut)); date = date.plusDays(1)) {
            if (date.getDayOfWeek().ordinal() == 5 || date.getDayOfWeek().ordinal() == 6) {
                numberOfWeekEndDays++;
            } else {
                numberOfWeekDays++;
            }
        }

        return new int[]{numberOfWeekDays, numberOfWeekEndDays, vehiclePrice, lateCheckoutPrice};
    }

    private int calculatePrice(ReservationEntity reservation, int[] dayCounts) {
        int price = (dayCounts[0] * Constants.WEEKDAYPRICE) + (dayCounts[1] * Constants.WEEKENDPRICE);
        if (reservation.isVehicle()) {
            int vehiclePrice = (dayCounts[0] * Constants.VEHICLEWEEKPRICE) + (dayCounts[1] * Constants.VEHICLEWEEKENDPRICE);
            price += vehiclePrice;
        }
        if (LocalDateTime.now(clock).getHour() >= 12) {
            int lateCheckoutPrice = (LocalDateTime.now(clock).getDayOfWeek().ordinal() == 0 || LocalDateTime.now(clock).getDayOfWeek().ordinal() == 6) ? Constants.WEEKENDPRICE / 2 : Constants.WEEKDAYPRICE / 2;
            price += lateCheckoutPrice;
        }

        return price;
    }
}
