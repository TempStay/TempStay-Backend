package com.tempstay.tempstay.Models;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class UpdateUserDetails {
    
private String userName;
private long phoneNumber;


}
