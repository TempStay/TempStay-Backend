package com.tempstay.tempstay.Models;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class TwoFAServiceProvider {
    private String email;
    private int otp;
}
