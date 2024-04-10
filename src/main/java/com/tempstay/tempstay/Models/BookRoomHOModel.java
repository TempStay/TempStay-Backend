package com.tempstay.tempstay.Models;
import java.sql.Date;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "book_room_details")
public class BookRoomHOModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID roomBookingId;

    private UUID hotelownId;

    private UUID userId;

    private UUID roomId;

    private Date checkinDate;

    private Date checkoutDate;

   private int numberOfDaysToStay;

    private int numberOfRooms;

    private int priceToBePaid;

    private UUID roomNo;
}