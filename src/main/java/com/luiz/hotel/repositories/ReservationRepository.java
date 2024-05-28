package com.luiz.hotel.repositories;

import com.luiz.hotel.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
}
