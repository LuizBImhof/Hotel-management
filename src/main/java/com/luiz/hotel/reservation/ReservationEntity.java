package com.luiz.hotel.reservation;

import com.luiz.hotel.guest.GuestEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "reservation")
public class ReservationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate start_date;

    private LocalDate end_date;

    private LocalDateTime check_in;

    private LocalDateTime check_out;

    private boolean vehicle;

    @Column(columnDefinition = "Integer default 0")
    private Integer price = 0;

    @ManyToOne(optional = false)
    @JoinColumn (name = "guest_id")
    private GuestEntity guest;

    public ReservationEntity (ReservationDto data){
        this.check_in = data.check_in();
        this.check_out = data.check_out();
        this.start_date = data.start_date();
        this.end_date = data.end_date();
        this.vehicle = data.vehicle();
        this.price = data.price();

    }

}
