package com.tempstay.tempstay.Models;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class PriceHotelType {
    private String room;
    private int price;
    private int numberOfRoom;
}
