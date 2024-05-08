package com.tempstay.tempstay.UserServices;

import java.time.LocalDate;
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
                // Get the hotel details
                HotelsDB hotel_ob = hotelDBRepo.findByRoomId(bookRoomHOModelReq.getRoomId());

                // Calculate the available rooms
                int availableRooms = hotel_ob.getNumberOfRooms();

                // Check if the requested number of rooms is available
                if (bookRoomHOModelReq.getNumberOfRooms() <= availableRooms) {
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

                    int total_price = (int) (hotel_ob.getPricePerDay() * daysDifference
                            * bookRoomHOModelReq.getNumberOfRooms());

                    bookRoomHOModel.setPriceToBePaid(total_price);

                    bookRoomRepo.save(bookRoomHOModel);

                    // Update the number of available rooms
                    hotel_ob.setNumberOfRooms(availableRooms - bookRoomHOModelReq.getNumberOfRooms());
                    hotelDBRepo.save(hotel_ob);

                    responseMessage.setSuccess(true);
                    responseMessage.setMessage("Room booked.");

                    return ResponseEntity.ok().body(responseMessage);
                } else {
                    // Not enough rooms available
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Not enough rooms available.");
                    return ResponseEntity.badRequest().body(responseMessage);
                }
            } else {
                return messageFromCheckRoom;
            }
        } catch (Exception e) {
            // Handle exceptions
            responseMessage.setSuccess(false);
            responseMessage.setMessage("An error occurred while processing your request.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
}

}