package com.luiz.hotel.guest;

import com.luiz.hotel.reservation.ReservationEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@ToString
@Table(name = "guest")
public class GuestEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique=true)
    private String document;

    @Column(unique=true)
    private String phone;

    @OneToMany(mappedBy = "guest")
    @ToString.Exclude
    private List<ReservationEntity> reservations;

    public GuestEntity (GuestDto data){
        this.id = data.id();
        this.name = data.name();
        this.document = data.document();
        this.phone = data.phone();
    }

}
