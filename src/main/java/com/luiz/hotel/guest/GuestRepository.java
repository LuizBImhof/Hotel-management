package com.luiz.hotel.guest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<GuestEntity, Long> {

    Optional<GuestEntity> findGuestByDocument(String document);

    Optional<GuestEntity> findGuestByPhone(String phone);

    Optional<GuestEntity> findGuestByName(String name);

}
