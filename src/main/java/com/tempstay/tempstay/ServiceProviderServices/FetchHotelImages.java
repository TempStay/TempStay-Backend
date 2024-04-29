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

    // @Autowired
    // private GetSPDetailsMW getSPDetailsMW;

    // @Autowired
    // private HotelsDB hotelsDB;

    // @Autowired
    // private HotelDBRepo  

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

    public List<HotelsDB> fetchSportsService(String token, String role) {
        String email=authService.verifyToken(token);
        UUID hotelownId=serviceProviderRepository.findByEmail(email).getId();
        // ResponseEntity<ResponseMessage> spId = getSPDetailsMW.getSPDetailsByToken(token, role);
        List<HotelsDB> sports = hotelDBRepo.findByHotelownId(hotelownId);
        return sports;
    }

    public List<ImagesDB> fetchImagesService(String token, String role) {
        // ResponseEntity<ResponseMessage> spId = getSPDetailsMW.getSPDetailsByToken(token, role);
        String email=authService.verifyToken(token);
        UUID hotelownId=serviceProviderRepository.findByEmail(email).getId();

        List<ImagesDB> images = imagesDBRepo.findByhotelownId(hotelownId);
        return images;
    }

    public ResponseEntity<Object> fetchSportBySportIdAndSpIdService(UUID hotelownId, UUID roomId) {
        try {
            HotelsDB sortedBySportIdAndSpId = hotelDBRepo.findByHotelownIdAndRoomId(hotelownId, roomId);
                    

            if (sortedBySportIdAndSpId != null) {
                return ResponseEntity.ok().body(sortedBySportIdAndSpId);
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
