package com.luiz.hotel.guest;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
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

    public GuestEntity (GuestDto data){
        this.name = data.name();
        this.document = data.document();
        this.phone = data.phone();
    }
}
