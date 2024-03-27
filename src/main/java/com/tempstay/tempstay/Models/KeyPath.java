package com.tempstay.tempstay.Models;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class KeyPath {
    private String key;
    private String path;
}