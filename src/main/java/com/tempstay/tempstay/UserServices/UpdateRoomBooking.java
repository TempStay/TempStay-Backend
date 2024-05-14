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
                    changeBookingData.getRoomId(),
                    bookRoomOb.getHotelownId());

            if (!messageFromCheckRoom.getBody().getSuccess()) {
                return messageFromCheckRoom;
            }

            bookRoomOb.setCheckinDate(changeBookingData.getCheckinDate());

            

            bookRoomOb.setCheckoutDate(changeBookingData.getCheckoutDate());

           
            LocalDate checkOut = changeBookingData.getCheckoutDate().toLocalDate();

            LocalDate checkIn = changeBookingData.getCheckinDate().toLocalDate();

            long daysDifference = ChronoUnit.DAYS.between(checkIn, checkOut);

            bookRoomOb.setNumberOfDaysToStay((int) daysDifference);

            bookRoomOb.setNumberOfRooms(changeBookingData.getNumberOfRooms());

            HotelsDB hotelOb = hotelDBRepo.findByRoomId(changeBookingData.getRoomId());

            int totalPrice = (int) (hotelOb.getPricePerDay() * daysDifference * changeBookingData.getNumberOfRooms());

            bookRoomOb.setPriceToBePaid(totalPrice);

            bookRoomRepo.save(bookRoomOb);

            int numberOfRooms = hotelOb.getNumberOfRooms();

            int updatedNumberOfRooms = numberOfRooms - changeBookingData.getNumberOfRooms();

            hotelOb.setNumberOfRooms(updatedNumberOfRooms);

            hotelDBRepo.save(hotelOb);
            // int noOfRooms = bookRoomRepo.findById(bookRoomOb.getRoomBookingId()).get().getNumberOfRooms();

            HotelsDB hotelFromDB = hotelDBRepo
                    .findByRoomId(bookRoomRepo.findById(bookRoomOb.getRoomBookingId()).get().getRoomId());

            int updated_no_of_rooms = bookRoomOb.getNumberOfRooms() + hotelFromDB.getNumberOfRooms();

            
            hotelFromDB.setNumberOfRooms(updated_no_of_rooms);

            hotelDBRepo.save(hotelFromDB);

            bookRoomOb.setRoomId(changeBookingData.getRoomId());

            bookRoomRepo.save(bookRoomOb);

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
