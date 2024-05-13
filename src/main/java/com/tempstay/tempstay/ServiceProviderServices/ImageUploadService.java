package com.tempstay.tempstay.ServiceProviderServices;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tempstay.tempstay.Models.ImagesDB;
import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.Repository.ImagesDBRepo;
import com.tempstay.tempstay.Repository.ServiceProviderRepository;
import com.tempstay.tempstay.UserServices.AuthService;

@Service
public class ImageUploadService {

    // @Autowired
    // private GetSPDetailsMW getSPDetailsMW;

    @Autowired
    private ImagesDBRepo imagesDBRepo;

    @Autowired
    private S3PutObjectService s3PutObjectService;

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private AuthService authService;

    public ResponseEntity<ResponseMessage> uploadImageService(String token, String role, List<MultipartFile> images) {
        try {

            String failedImages = "";

            for (int i = 0; i < images.size(); i++) {

                String email = authService.verifyToken(token);

                String hotelownId = serviceProviderRepository.findByEmail(email).getId().toString();

                ImagesDB imagesDB = new ImagesDB();

                UUID imageUUID = UUID.randomUUID();
                UUID key = imageUUID;
                imagesDB.setImageId(imageUUID);

                imagesDB.setHotelownId(UUID.fromString(hotelownId));

                ResponseMessage messageFromPutObjectService = s3PutObjectService
                        .putObjectService(hotelownId, key.toString(), images.get(i)).getBody();

                if (messageFromPutObjectService.getSuccess()) {
                    imagesDB.setImageURL(messageFromPutObjectService.getMessage());
                    imagesDB.setDateOfGenration(LocalDate.now());
                    imagesDBRepo.save(imagesDB);
                } else {
                    failedImages += images.get(i).getOriginalFilename();
                    failedImages += ", ";
                    failedImages += "Reason: " + messageFromPutObjectService.getMessage();
                }
            }

            if (failedImages.length() == 0) {
                responseMessage.setSuccess(true);
                responseMessage.setMessage("Upload successfully!");
                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Upload failed for: " + failedImages);
                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
            }

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Internal Server Error inside ImageUploadService.java " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }

    }
}
