package com.luiz.hotel.repositories;

import com.luiz.hotel.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<GuestEntity, Long> {

    Optional<GuestEntity> findGuestByDocument(String document);

    Optional<GuestEntity> findGuestByPhone(String phone);

    Optional<GuestEntity> findGuestByName(String name);

    Optional<GuestEntity> findGuestById(Long id);

    @Query("SELECT guest FROM ReservationEntity r WHERE r.guest is not null " +
            " AND r.check_in is not null AND r.check_out is null")
    List<GuestEntity> findGuestsInHotel();

    @Query("SELECT guest FROM ReservationEntity r WHERE r.guest is not null " +
            " AND r.check_in is null AND r.check_out is null")
    List<GuestEntity> findGuestsWithReservationAndNotCheckedIn();
}
