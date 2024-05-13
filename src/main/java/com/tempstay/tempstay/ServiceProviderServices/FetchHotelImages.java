package com.tempstay.tempstay.ServiceProviderServices;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tempstay.tempstay.Models.HotelsDB;
import com.tempstay.tempstay.Models.ImagesDB;
import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.Repository.HotelDBRepo;
import com.tempstay.tempstay.Repository.ImagesDBRepo;
import com.tempstay.tempstay.Repository.ServiceProviderRepository;
import com.tempstay.tempstay.UserServices.AuthService;

@Service
public class FetchHotelImages {

   

    @Autowired
    private HotelDBRepo hotelDBRepo;

    @Autowired
    private ImagesDBRepo imagesDBRepo;

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private AuthService authService;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    public List<HotelsDB> fetchHotelsService(String token, String role) {
        String email=authService.verifyToken(token);
        UUID hotelownId=serviceProviderRepository.findByEmail(email).getId();
       
        List<HotelsDB> hotels = hotelDBRepo.findByHotelownId(hotelownId);
        return hotels;
    }

    public List<ImagesDB> fetchImagesService(String token, String role) {
       
        String email=authService.verifyToken(token);
        UUID hotelownId=serviceProviderRepository.findByEmail(email).getId();

        List<ImagesDB> images = imagesDBRepo.findByhotelownId(hotelownId);
        return images;
    }

    public ResponseEntity<Object> findByRoomIdAndHotelownIdService(UUID hotelownId, UUID roomId) {
        try {
            HotelsDB sortedByRoomIdAndhotelownId = hotelDBRepo.findByHotelownIdAndRoomId(hotelownId, roomId);
                    

            if (sortedByRoomIdAndhotelownId != null) {
                return ResponseEntity.ok().body(sortedByRoomIdAndhotelownId);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Room with this id doesn't exists.");
                return ResponseEntity.badRequest().body(responseMessage);
            }

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }

    }

}
