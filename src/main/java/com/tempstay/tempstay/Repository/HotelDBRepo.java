package com.tempstay.tempstay.Repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tempstay.tempstay.Models.HotelsDB;

import jakarta.transaction.Transactional;

public interface HotelDBRepo extends JpaRepository<HotelsDB, UUID> {
    List<HotelsDB> findByHotelownId(UUID hotelownId);
    HotelsDB findByRoomType(String roomType);
    List<HotelsDB> findByEmail(String email);
    HotelsDB findByRoomId(UUID roomId);

    @Transactional
    @Query(value = "SELECT * FROM hotels_db WHERE hotelown_id=:hotelownId AND room_id=:roomId",nativeQuery = true)
    HotelsDB findByHotelownIdAndRoomId(@Param("hotelownId") UUID hotelownId, @Param("roomId") UUID roomId);
    
    // @Transactional
    // @Query(value="SELECT * FROM hotels_db WHERE room_type like %?1% ",nativeQuery = true)
    // List<HotelsDB> findBySearch(String searchItem);

    @Transactional
    @Query(value = "SELECT * FROM hotels_db WHERE hotelown_id=:hotelownId AND room_type =:roomType",nativeQuery = true)
    HotelsDB findByHotelownIdAndRoomType(@Param("hotelownId") UUID hotelownId , @Param("roomType") String roomType);
}