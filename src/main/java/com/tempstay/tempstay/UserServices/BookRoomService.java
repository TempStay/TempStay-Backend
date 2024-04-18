package com.tempstay.tempstay.UserServices;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tempstay.tempstay.Models.BookRoomHOModel;
import com.tempstay.tempstay.Models.HotelsDB;
import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.Models.UserModel;
import com.tempstay.tempstay.Repository.BookRoomRepo;
import com.tempstay.tempstay.Repository.HotelDBRepo;
import com.tempstay.tempstay.Repository.UserRepository;

@Service
public class BookRoomService {
    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private BookRoomRepo bookRoomRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

   

    @Autowired
    private HotelDBRepo hotelDBRepo;

    public ResponseEntity<ResponseMessage> checkRoom(UUID roomId, UUID hotelownId) {
        try {
            HotelsDB hotel_ob = hotelDBRepo.findByHotelownIdAndRoomId(hotelownId, roomId);

            if (hotel_ob != null) {
                int no_of_rooms = hotel_ob.getNumberOfRooms();

                if (no_of_rooms > 0) {
                    responseMessage.setSuccess(true);
                    responseMessage.setMessage("Room Empty");
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("No Rooms Available");
                }
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Enter valid roomId");
            }

            return ResponseEntity.ok().body(responseMessage);
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage
                    .setMessage("Internal Server Error inside BookRoomServce.java Method:checkRoom " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    public ResponseEntity<ResponseMessage> userRoomBookService(BookRoomHOModel bookRoomHOModelReq, String token,
            String role) {
        try {
            ResponseEntity<ResponseMessage> messageFromCheckRoom = checkRoom(bookRoomHOModelReq.getRoomId(),
                    bookRoomHOModelReq.getHotelownId());

            if (messageFromCheckRoom.getBody().getSuccess()) {

                BookRoomHOModel bookRoomHOModel = new BookRoomHOModel();

                bookRoomHOModel.setHotelownId(bookRoomHOModelReq.getHotelownId());

                String email = authService.verifyToken(token);

                UserModel user = userRepository.findByEmail(email);

                bookRoomHOModel.setUserId(user.getId());

                bookRoomHOModel.setHotelownId((bookRoomHOModelReq.getHotelownId()));

                bookRoomHOModel.setCheckinDate(bookRoomHOModelReq.getCheckinDate());

                bookRoomHOModel.setRoomId(bookRoomHOModelReq.getRoomId());

                bookRoomHOModel.setCheckoutDate(bookRoomHOModelReq.getCheckoutDate());

               

                LocalDate checkOut = bookRoomHOModelReq.getCheckoutDate().toLocalDate();
                LocalDate checkIn = bookRoomHOModelReq.getCheckinDate().toLocalDate();

                long daysDifference = ChronoUnit.DAYS.between(checkIn, checkOut);
                

                bookRoomHOModel.setNumberOfDaysToStay((int) daysDifference);

                bookRoomHOModel.setNumberOfRooms(bookRoomHOModelReq.getNumberOfRooms());

                UUID roomId = bookRoomHOModelReq.getRoomId();

                HotelsDB hotel_ob = hotelDBRepo.findByRoomId((roomId));

                int total_price = (int) (hotel_ob.getPricePerDay() * daysDifference
                        * bookRoomHOModelReq.getNumberOfRooms());

                bookRoomHOModel.setPriceToBePaid(total_price);

                bookRoomRepo.save(bookRoomHOModel);

                int no_of_rooms = hotel_ob.getNumberOfRooms();

                int updated_no_of_rooms = no_of_rooms - bookRoomHOModelReq.getNumberOfRooms();

                HotelsDB hotelFromDB = hotelDBRepo.findByRoomId(bookRoomHOModelReq.getRoomId());

                hotelFromDB.setNumberOfRooms(updated_no_of_rooms);

                hotelDBRepo.save(hotelFromDB);

                responseMessage.setSuccess(true);
                responseMessage.setMessage("Room booked.");

                return ResponseEntity.ok().body(responseMessage);
            } else {
                return messageFromCheckRoom;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseMessage.setSuccess(false);
            responseMessage.setMessage(
                    "Internal Server Error inside BookSlotServce.java Method: userBookSLotService " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

}