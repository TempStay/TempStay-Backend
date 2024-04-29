package com.tempstay.tempstay.ServiceProviderServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.Models.ServiceProviderModel;
import com.tempstay.tempstay.Repository.ServiceProviderRepository;
import com.tempstay.tempstay.UserServices.AuthService;


@Service
public class ServiceProviderUpdate {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ResponseMessage responseMessage;

    public ResponseEntity<ResponseMessage> UpdateService(String token, ServiceProviderModel latestDetails) {
        try {
            String email = authService.verifyToken(token);
            ServiceProviderModel updateToBeDone = serviceProviderRepository.findByEmail(email);
            if (updateToBeDone == null) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Service provider with this email: " + email + " doesn't exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            } else {
                updateToBeDone.setHotelName(latestDetails.getHotelName());
                updateToBeDone.setAddress(latestDetails.getAddress());
                updateToBeDone.setPhoneNumber(latestDetails.getPhoneNumber());
               
                serviceProviderRepository.save(updateToBeDone);

                responseMessage.setSuccess(true);
                responseMessage.setMessage("Details updated successfully");
                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
            }
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage(
                    "Internal server error in ServiceProviderUpdateService.java. Method: ServiceProviderUpdateService. Reason: "
                            + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
}
