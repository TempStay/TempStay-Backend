package com.tempstay.tempstay.Models;

import java.sql.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.Data;


@Data
@Component
public class ChangeBookingData {
    

    // private UUID roomId;

    private Date checkinDate;

    private Date checkoutDate;

    private int numberOfRooms;

    private UUID roomBookingId;


    
}
