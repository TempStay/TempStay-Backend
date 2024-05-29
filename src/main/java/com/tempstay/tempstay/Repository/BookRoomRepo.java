package com.tempstay.tempstay.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tempstay.tempstay.Models.BookRoomHOModel;

import jakarta.transaction.Transactional;

@Repository
public interface BookRoomRepo extends JpaRepository<BookRoomHOModel, UUID> {

    List<BookRoomHOModel> findByUserId(UUID userId);

    List<BookRoomHOModel> findByHotelownId(UUID hotelownId);

    BookRoomHOModel findByRoomBookingId(UUID roomBookingId);

    @Transactional
    @Query(value = "SELECT * FROM book_room_details WHERE hotelown_id = :hotelownId AND room_id = :roomId AND ((checkin_date BETWEEN :checkinDate AND :checkoutDate) OR (checkout_date BETWEEN :checkinDate AND :checkoutDate) OR (:checkinDate BETWEEN checkin_date AND checkout_date) OR (:checkoutDate BETWEEN checkin_date AND checkout_date))", nativeQuery = true)

    ArrayList<BookRoomHOModel> findRoomBookingExists(
            @Param("hotelownId") UUID hotelownId,
            @Param("roomId") UUID roomId,
            @Param("checkinDate") Date checkinDate,
            @Param("checkoutDate") Date checkoutDate);

    // @Transactional
    // @Query(value = "SELECT SUM(number_of_rooms) AS total_rooms_booked FROM book_room_details WHERE hotelown_id = :hotelownId AND room_id =:roomId AND ((checkin_date BETWEEN :checkinDate AND :checkoutDate) OR (checkout_date BETWEEN :checkinDate AND :checkoutDate) OR (:checkinDate BETWEEN checkin_date AND checkout_date) OR (:checkoutDate BETWEEN checkin_date AND checkout_date))", nativeQuery = true)
    // long countNumberOfRooms(@Param("hotelownId") UUID hotelownId,
    //         @Param("roomId") UUID roomId,
    //         @Param("checkinDate") Date checkinDate,
    //         @Param("checkoutDate") Date checkoutDate);
}