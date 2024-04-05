package com.tempstay.tempstay.ServiceProviderServices;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tempstay.tempstay.Models.HotelsDB;
import com.tempstay.tempstay.Models.PriceHotelType;
import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.Repository.HotelDBRepo;

@Service
public class  UpdateHotelDetails {

    @Autowired
    private HotelDBRepo hotelDBRepo;

    @Autowired
    private ResponseMessage responseMessage;

    public List<HotelsDB> getAllHotels() {
        return hotelDBRepo.findAll();
    }

    public ResponseEntity<ResponseMessage> updateHotelDetailsService(PriceHotelType latesthotelDetails, UUID roomId) {
        try {
            HotelsDB hotelToBeUpdated = hotelDBRepo.findByRoomId(roomId);
            System.out.println("Latest hotel details"+latesthotelDetails);

            if (hotelToBeUpdated == null) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Room with roomId: " + latesthotelDetails.getRoom() + " and with roomId: "
                        + latesthotelDetails.getRoom() + " doesn't exist.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            } else {
                hotelToBeUpdated.setRoomType(latesthotelDetails.getRoom());
                hotelToBeUpdated.setPricePerDay(latesthotelDetails.getPrice());
                hotelToBeUpdated.setNumberOfRooms(latesthotelDetails.getNumberOfRoom());
                hotelDBRepo.save(hotelToBeUpdated);

                responseMessage.setSuccess(true);
                responseMessage.setMessage("Hotel details updated successfully");
                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
            }

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage
                    .setMessage("Internal Server Error in method updateHotelDetailsService. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
}