package com.tempstay.tempstay.ServiceProviderServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tempstay.tempstay.Models.HotelsDB;
import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.Repository.HotelDBRepo;

@Service
public class UpdateHotelDetails {

    @Autowired
    private HotelDBRepo hotelDBRepo;

    @Autowired
    private ResponseMessage responseMessage;

    public ResponseEntity<ResponseMessage> updateHotelDetailsService(HotelsDB latesthotelDetails) {
        try {
            HotelsDB hotelToBeUpdated = hotelDBRepo.findByHotelownIdAndRoomId(latesthotelDetails.getHotelownId(),
                    latesthotelDetails.getRoomId());
            if (hotelToBeUpdated == null) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Room with roomId: " + latesthotelDetails.getRoomId() + " and with roomId: "
                        + latesthotelDetails.getRoomId() + " doesn't exist.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            } else {
                hotelToBeUpdated.setRoomType(latesthotelDetails.getRoomType());
                hotelToBeUpdated.setPricePerDay(latesthotelDetails.getPricePerDay());
                hotelToBeUpdated.setNumberOfRooms(latesthotelDetails.getNumberOfRooms());
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