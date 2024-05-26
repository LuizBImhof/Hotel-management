package com.luiz.hotel.guest;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "guest")
public class GuestEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String document;

    private String phone;

    public GuestEntity (GuestDto data){
        this.name = data.name();
        this.document = data.document();
        this.phone = data.phone();
    }
}
