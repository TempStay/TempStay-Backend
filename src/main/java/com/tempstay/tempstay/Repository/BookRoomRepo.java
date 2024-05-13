package com.tempstay.tempstay.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tempstay.tempstay.Models.BookRoomHOModel;


@Repository
public interface BookRoomRepo extends JpaRepository<BookRoomHOModel, UUID> {

    List<BookRoomHOModel> findByUserId(UUID userId);

    List<BookRoomHOModel> findByHotelownId(UUID hotelownId);

    BookRoomHOModel findByRoomBookingId(UUID roomBookingId);

    

}