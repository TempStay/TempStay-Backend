package com.tempstay.tempstay.UserServices;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tempstay.tempstay.Models.BookRoomHOModel;
import com.tempstay.tempstay.Models.HotelsDB;
import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.Repository.BookRoomRepo;
import com.tempstay.tempstay.Repository.HotelDBRepo;

@Service
public class DeleteBooking {

@Autowired
private AuthService authService;

@Autowired
private BookRoomRepo bookRoomRepo;

@Autowired
private ResponseMessage responseMessage;

@Autowired
private HotelDBRepo hotelDBRepo;

    public ResponseEntity<ResponseMessage> deletebooking(String token,UUID bookingId,String role){
        
        String email=authService.verifyToken(token);

        if(email!=null){

            BookRoomHOModel usserbookingOb = bookRoomRepo.findByRoomBookingId(bookingId);
            if (usserbookingOb == null) {

                responseMessage.setSuccess(false);
                responseMessage.setMessage("booking not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            } else {
                int noOfRooms = bookRoomRepo.findById(bookingId).get().getNumberOfRooms();

                HotelsDB hotelFromDB = hotelDBRepo.findByRoomId(bookRoomRepo.findById(bookingId).get().getRoomId());

                int updated_no_of_rooms = noOfRooms + hotelFromDB.getNumberOfRooms();

                hotelFromDB.setNumberOfRooms(updated_no_of_rooms);

                hotelDBRepo.save(hotelFromDB);
                bookRoomRepo.deleteById(bookingId);

                responseMessage.setSuccess(true);
                responseMessage.setMessage("Booking deleted succesfull.");

                return ResponseEntity.ok().body(responseMessage);

            }
        } else {

            responseMessage.setSuccess(false);
            responseMessage.setMessage("User  not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);

        }
    }

        }


