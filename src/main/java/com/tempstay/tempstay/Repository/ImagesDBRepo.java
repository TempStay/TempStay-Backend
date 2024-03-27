package com.tempstay.tempstay.Repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tempstay.tempstay.Models.ImagesDB;
import java.util.List;


public interface ImagesDBRepo extends JpaRepository<ImagesDB, UUID> {
    List<ImagesDB> findByhotelownId(UUID hotelownId);
    
}

