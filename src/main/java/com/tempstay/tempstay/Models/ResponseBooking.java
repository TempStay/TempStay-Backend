package com.tempstay.tempstay.Models;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ResponseBooking {
    private Boolean success;
    private String message;
    private String token;
    private int priceToBePaid;
    private int availableRooms;
}
