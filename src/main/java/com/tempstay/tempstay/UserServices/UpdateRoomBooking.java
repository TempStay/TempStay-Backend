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
import com.tempstay.tempstay.Models.ResponseBooking;
import com.tempstay.tempstay.Repository.BookRoomRepo;
import com.tempstay.tempstay.Repository.HotelDBRepo;

@Service
public class UpdateRoomBooking {

    @Autowired
    private BookRoomRepo bookRoomRepo;

    @Autowired
    private HotelDBRepo hotelDBRepo;

    @Autowired
    private BookRoomService bookRoomService;

    @Autowired
    private ResponseBooking responseBooking;

    public ResponseEntity<ResponseBooking> reScheduleRoomService(ChangeBookingData changeBookingData, String token,
            String role) {
        try {

            BookRoomHOModel bookRoomOb = bookRoomRepo.findByRoomBookingId(changeBookingData.getRoomBookingId());

            if (bookRoomOb == null) {
                responseBooking.setSuccess(false);
                responseBooking.setMessage("Booking not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBooking);
            }

            ResponseEntity<ResponseBooking> messageFromCheckRoom = bookRoomService.checkRoom(
                    bookRoomOb.getRoomId(),
                    bookRoomOb.getHotelownId(), changeBookingData.getCheckinDate(),
                    changeBookingData.getCheckoutDate());

            
            int availableRooms = messageFromCheckRoom.getBody().getAvailableRooms();
            
            if (!messageFromCheckRoom.getBody().getSuccess()) {
                if(changeBookingData.getNumberOfRooms()<bookRoomOb.getNumberOfRooms()){
                    bookRoomOb.setNumberOfRooms(changeBookingData.getNumberOfRooms());
                    return reScheduleRoomService(changeBookingData, token, role);
                }
                else {
                    return messageFromCheckRoom;
                }
            }
            if (Math.abs(changeBookingData.getNumberOfRooms()-bookRoomOb.getNumberOfRooms()) > availableRooms) {
                responseBooking.setSuccess(false);
                responseBooking.setMessage("Insufficient rooms available.");
                responseBooking.setAvailableRooms(availableRooms);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBooking);

            } else {
                bookRoomOb.setCheckinDate(changeBookingData.getCheckinDate());
                bookRoomOb.setCheckoutDate(changeBookingData.getCheckoutDate());
                bookRoomOb.setNumberOfRooms(changeBookingData.getNumberOfRooms());
                LocalDate checkOut = changeBookingData.getCheckoutDate().toLocalDate();
                LocalDate checkIn = changeBookingData.getCheckinDate().toLocalDate();
                long daysDifference = ChronoUnit.DAYS.between(checkIn, checkOut);
                bookRoomOb.setNumberOfDaysToStay((int) daysDifference);
                HotelsDB hotelOb = hotelDBRepo.findByHotelownIdAndRoomId(bookRoomOb.getHotelownId(),
                        bookRoomOb.getRoomId());
                int totalPrice = (int) (hotelOb.getPricePerDay() * daysDifference
                        * changeBookingData.getNumberOfRooms());
                bookRoomOb.setPriceToBePaid(totalPrice);
                bookRoomRepo.save(bookRoomOb);
                responseBooking.setSuccess(true);
                responseBooking.setMessage("Updated Room Successfully booked.");
                responseBooking.setPriceToBePaid(totalPrice);
                ResponseEntity<ResponseBooking> messageupdated = bookRoomService.checkRoom(
                    bookRoomOb.getRoomId(),
                    bookRoomOb.getHotelownId(), changeBookingData.getCheckinDate(),
                    changeBookingData.getCheckoutDate());
                responseBooking.setAvailableRooms(messageupdated.getBody().getAvailableRooms());
                return ResponseEntity.ok().body(responseBooking);

            }

        } catch (Exception e) {

            responseBooking.setSuccess(false);
            responseBooking.setMessage(
                    "Internal Server Error inside BookSlotServce.java Method: userBookSLotService " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBooking);
        }
    }

}
