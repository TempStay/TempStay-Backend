package com.tempstay.tempstay.ServiceProviderServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tempstay.tempstay.Models.ImagesDB;
import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.StaticInfo.S3Data;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class DeleteImagesService {

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private S3PutObjectService s3PutObjectService;

    public ResponseEntity<ResponseMessage> deleteImageService(ImagesDB imageInfo) {
        S3Client client = S3Data.s3Client;
        try {
            String bucketName = System.getenv("BUCKET_NAME");
            String key = imageInfo.getHotelownId().toString() + '/' + imageInfo.getImageId();
            if (s3PutObjectService.checkObjectInBucket(bucketName, key)) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();

                client.deleteObject(deleteObjectRequest);

                responseMessage.setSuccess(true);
                responseMessage.setMessage("Object '" + key + "' deletion success!");

                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);

            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Object '" + key + "' not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage
                    .setMessage("Internal Server Error in DeleteImageService.java. Method: deleteImageService. Reason: "
                            + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);

        }
    }
}
