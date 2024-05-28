package com.luiz.hotel.entities;

import com.luiz.hotel.dtos.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "reservation")
public class ReservationEntity {
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ToString.Include
    private LocalDate start_date;

    @ToString.Include
    private LocalDate end_date;

    @ToString.Include
    private LocalDateTime check_in;

    @ToString.Include
    private LocalDateTime check_out;

    @ToString.Include
    @Column(nullable = false)
    private boolean vehicle;

    @ToString.Include
    @Column(columnDefinition = "Integer default 0")
    private Integer price = 0;

    @ManyToOne(optional = false)
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
