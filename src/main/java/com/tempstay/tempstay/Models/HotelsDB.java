package com.tempstay.tempstay.Models;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Component
@Table(name = "hotels_db")
public class HotelsDB {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID roomId;

    private String roomType;
    
    private UUID hotelownId;

    @NotNull
    private int pricePerDay;

    @NotNull
    private int numberOfRooms;

    @NotNull
    private String email;

}
    

    
