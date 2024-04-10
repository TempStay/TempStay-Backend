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
import com.tempstay.tempstay.Repository.ServiceProviderRepository;
import com.tempstay.tempstay.UserServices.AuthService;

@Service
public class UploadHotelService {

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private HotelDBRepo hotelDBRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    public ResponseEntity<ResponseMessage> uploadHotelsInfoService(List<PriceHotelType> pricePerType, String token,
            String role) {
        // return ResponseEntity.ok().body(responseMessage);
        try {
            String email = authService.verifyToken(token);
            String hotelownID = serviceProviderRepository.findByEmail(email).getId().toString();
            String duplicatesMessage = "";

            for (int i = 0; i < pricePerType.size(); i++) {

                HotelsDB roomExistence = hotelDBRepo.findByHotelownIdAndRoomType(UUID.fromString(hotelownID),
                        pricePerType.get(i).getRoom());
                if (roomExistence == null) {
                    HotelsDB hotelsdb = new HotelsDB();
                    hotelsdb.setHotelownId(UUID.fromString(hotelownID));
                    hotelsdb.setRoomType(pricePerType.get(i).getRoom());
                    hotelsdb.setPricePerDay(pricePerType.get(i).getPrice());
                    hotelsdb.setNumberOfRooms(pricePerType.get(i).getNumberOfRoom());
                    hotelsdb.setEmail(email);
                    hotelDBRepo.save(hotelsdb);

                } else {
                    duplicatesMessage += hotelDBRepo.findByRoomType(pricePerType.get(i).getRoom());
                    duplicatesMessage += ", ";
                }
            }

            if (duplicatesMessage.length() == 0) {
                responseMessage.setSuccess(true);
                responseMessage.setMessage("Hotels registered successfully");
                return ResponseEntity.ok().body(responseMessage);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Hotels registered successfully but these " + duplicatesMessage
                        + " already exists, hence not been added.");
                return ResponseEntity.ok().body(responseMessage);
            }

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }

    }
}
