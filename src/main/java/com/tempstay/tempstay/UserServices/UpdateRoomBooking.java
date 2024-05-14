package com.tempstay.tempstay.UserServices;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tempstay.tempstay.Models.BookRoomHOModel;
import com.tempstay.tempstay.Models.ChangeBookingData;
import com.tempstay.tempstay.Models.HotelsDB;
import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.Repository.BookRoomRepo;
import com.tempstay.tempstay.Repository.HotelDBRepo;

@Service
public class UpdateRoomBooking {

    @Autowired
    private BookRoomRepo bookRoomRepo;

    @Autowired
    private HotelDBRepo hotelDBRepo;

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private BookRoomService bookRoomService;


    public ResponseEntity<ResponseMessage> reScheduleRoomService(ChangeBookingData changeBookingData, String token,
    String role) {
try {
    
    BookRoomHOModel bookRoomOb = bookRoomRepo.findByRoomBookingId(changeBookingData.getRoomBookingId());

    

    if (bookRoomOb == null) {
        responseMessage.setSuccess(false);
        responseMessage.setMessage("Booking not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
    }

    ResponseEntity<ResponseMessage> messageFromCheckRoom = bookRoomService.checkRoom(
        bookRoomOb.getRoomId(),
            bookRoomOb.getHotelownId());

    if (!messageFromCheckRoom.getBody().getSuccess()) {
        return messageFromCheckRoom;
    }

    // Calculate the difference in the number of rooms requested
    int numberOfRoomsDifference = changeBookingData.getNumberOfRooms() - bookRoomOb.getNumberOfRooms();

    // Fetch hotel details
    HotelsDB hotelOb = hotelDBRepo.findByRoomId(bookRoomOb.getRoomId());

    // If the difference is positive, it means the user wants to book more rooms
    if (numberOfRoomsDifference > 0) {
        int updatedNumberOfRooms = hotelOb.getNumberOfRooms() - numberOfRoomsDifference;
        if (updatedNumberOfRooms < 0) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Insufficient rooms available.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
        }
        hotelOb.setNumberOfRooms(updatedNumberOfRooms);
    }
    // If the difference is negative, it means the user wants to reduce the booked rooms
    else if (numberOfRoomsDifference < 0) {
        hotelOb.setNumberOfRooms(hotelOb.getNumberOfRooms() - numberOfRoomsDifference);
    }
    // If the difference is zero, no change is needed

    // Update booking details
    bookRoomOb.setCheckinDate(changeBookingData.getCheckinDate());
    bookRoomOb.setCheckoutDate(changeBookingData.getCheckoutDate());
    bookRoomOb.setNumberOfRooms(changeBookingData.getNumberOfRooms());
    LocalDate checkOut = changeBookingData.getCheckoutDate().toLocalDate();
    LocalDate checkIn = changeBookingData.getCheckinDate().toLocalDate();
    long daysDifference = ChronoUnit.DAYS.between(checkIn, checkOut);
    bookRoomOb.setNumberOfDaysToStay((int) daysDifference);
    int totalPrice = (int) (hotelOb.getPricePerDay() * daysDifference * changeBookingData.getNumberOfRooms());
    bookRoomOb.setPriceToBePaid(totalPrice);
    bookRoomRepo.save(bookRoomOb);
    hotelDBRepo.save(hotelOb);

    responseMessage.setSuccess(true);
    responseMessage.setMessage("Updated Room Successfully booked.");
    return ResponseEntity.ok().body(responseMessage);
} catch (Exception e) {
    
    responseMessage.setSuccess(false);
    responseMessage.setMessage(
            "Internal Server Error inside BookSlotServce.java Method: userBookSLotService " + e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
}
}

}
