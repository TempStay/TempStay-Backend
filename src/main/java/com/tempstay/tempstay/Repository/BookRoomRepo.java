package com.tempstay.tempstay.Repository;

import java.sql.Date;
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

    @Transactional
    @Query(value = "SELECT * FROM book_room_details WHERE hotelown_id = :hotelownId AND room_no = :roomNo AND room_id= :roomId AND  (checkin_date = :checkinDate OR checkout_date = :checkoutDate) ", nativeQuery = true)
    BookRoomHOModel findRoomExists(@Param("hotelownId") UUID hotelownId, @Param("roomId") UUID roomId,
            @Param("checkinDate") Date checkinDate, @Param("checkoutDate") Date checkoutDate,@Param("roomNo") UUID roomNo);

    List<BookRoomHOModel> findByUserId(UUID userId);

    List<BookRoomHOModel> findByHotelownId(UUID hotelownId);

    BookRoomHOModel findByRoomBookingId(UUID roomBookingId);

}