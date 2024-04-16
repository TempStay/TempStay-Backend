package com.tempstay.tempstay.UserServices;

import java.sql.Date;
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

    public ResponseEntity<ResponseMessage> checkRoom(UUID hotelownId, Date checkinDate, Date checkoutDate,
            UUID roomId, UUID roomNo) {
        try {
            BookRoomHOModel userBooking = bookRoomRepo.findRoomExists(hotelownId, roomId, checkinDate, checkoutDate,
                    roomNo);
            if (userBooking == null) {
                responseMessage.setSuccess(true);
                responseMessage.setMessage("Room Empty");

                return ResponseEntity.ok().body(responseMessage);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Room full.");
                return ResponseEntity.ok().body(responseMessage);
            }
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
            ResponseEntity<ResponseMessage> messageFromCheckRoom = checkRoom(bookRoomHOModelReq.getHotelownId(),
                    bookRoomHOModelReq.getCheckinDate(), bookRoomHOModelReq.getCheckoutDate(),
                    bookRoomHOModelReq.getRoomId(),
                    bookRoomHOModelReq.getRoomNo());

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

                bookRoomHOModel.setNumberOfDaysToStay(bookRoomHOModelReq.getNumberOfDaysToStay());

                bookRoomHOModel.setNumberOfRooms(bookRoomHOModelReq.getNumberOfRooms());

                UUID roomId = bookRoomHOModelReq.getRoomId();

                HotelsDB hotel_ob = hotelDBRepo.findByRoomId((roomId));

                int total_price = hotel_ob.getPricePerDay() * bookRoomHOModelReq.getNumberOfDaysToStay()
                        * bookRoomHOModelReq.getNumberOfRooms();

                bookRoomHOModel.setPriceToBePaid(total_price);

                if(bookRoomHOModelReq.getNumberOfRooms()==1){
                    

                }
                else if(bookRoomHOModelReq.getNumberOfRooms()>1){



                }

                     
                




                bookRoomHOModel.setRoomNo(bookRoomHOModelReq.getRoomNo());

                bookRoomRepo.save(bookRoomHOModel);

                responseMessage.setSuccess(true);
                responseMessage.setMessage("Room booked.");

                return ResponseEntity.ok().body(responseMessage);
            } else {
                return messageFromCheckRoom;
            }

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage(
                    "Internal Server Error inside BookSlotServce.java Method: userBookSLotService " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
}