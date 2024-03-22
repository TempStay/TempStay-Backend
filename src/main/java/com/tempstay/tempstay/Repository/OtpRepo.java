package com.tempstay.tempstay.Repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tempstay.tempstay.Models.OTPModel;

import java.util.List;
import java.time.LocalDateTime;


public interface OtpRepo extends JpaRepository<OTPModel,UUID>{

    OTPModel findByEmail(String email);

    List<OTPModel> findByCreatedAt(LocalDateTime createdAt);
    
}
