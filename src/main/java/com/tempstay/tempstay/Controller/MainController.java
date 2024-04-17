package com.tempstay.tempstay.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tempstay.tempstay.Models.BookRoomHOModel;
import com.tempstay.tempstay.Models.HotelsDB;
import com.tempstay.tempstay.Models.LoginModel;
import com.tempstay.tempstay.Models.PriceHotelType;
import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.Models.ServiceProviderModel;
import com.tempstay.tempstay.RatingService.HotelRating;
import com.tempstay.tempstay.Repository.BookRoomRepo;
import com.tempstay.tempstay.Repository.HotelDBRepo;
import com.tempstay.tempstay.Repository.ServiceProviderRepository;
import com.tempstay.tempstay.SearchFunction.SearchByAddressAndHotelName;
import com.tempstay.tempstay.ServiceProviderServices.ServiceProviderUpdate;
import com.tempstay.tempstay.ServiceProviderServices.UpdateHotelDetails;
import com.tempstay.tempstay.ServiceProviderServices.UploadHotelService;
import com.tempstay.tempstay.UserServices.AuthService;
import com.tempstay.tempstay.UserServices.BookRoomService;
import com.tempstay.tempstay.UserServices.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "http://localhost:5173")
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceProviderUpdate serviceProviderUpdate;

    @Autowired
    private SearchByAddressAndHotelName searchByAddressAndHotelName;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private HotelRating hotelRating;

    @Autowired
    private UploadHotelService uploadHotelService;

    @Autowired
    private UpdateHotelDetails updateHotelDetials;

    @Autowired
    private AuthService authService;

    @Autowired
    private HotelDBRepo hotelDBRepo;

    @Autowired
    private BookRoomService bookRoomService;

    @Autowired
    private BookRoomRepo bookRoomRepo;

    @PostMapping("adduser")
    public ResponseEntity<Object> addUser(@Valid @RequestBody Object userOrService, BindingResult bindingResult,
            @RequestHeader String role) {
        return userService.userRegisterService(userOrService, bindingResult, role);
    }

    @GetMapping("getuserdetailsbytoken")
    public ResponseEntity<Object> getUserDetailsByToken(@RequestHeader String token, @RequestHeader String role) {
        return userService.getUserDetailsByEmailService(token, role);
    }

    @GetMapping("login")
    public ResponseEntity<Object> verifyUser(@RequestBody LoginModel loginModel, @RequestHeader String role) {
        return userService.userLoginService(loginModel, role);
    }

    @PostMapping("2fa")
    public ResponseEntity<Object> twofa(@RequestHeader int otpforTwoFAFromUser, @RequestHeader String email,
            @RequestHeader String role) {
        return userService.TwoFAService(otpforTwoFAFromUser, email, role);
    }

    @PostMapping("forgotpassword")
    public ResponseEntity<Object> forgotPassword(@RequestHeader String email, @RequestHeader String role) {
        return userService.forgotPasswordService(email, role);
    }

    @PostMapping("verifyOtpforforgotpassword")
    public ResponseEntity<Object> verifyTheUserOtp(@RequestHeader int otp, @RequestHeader String email) {
        return userService.verifyTheOtpEnteredByUser(otp, email);
    }

    @PostMapping("resetpassword")
    public ResponseEntity<Object> resetThePassword(@RequestHeader String passwordFromUser, @RequestHeader String role,
            @RequestHeader String email) {
        return userService.resetThePasswordService(passwordFromUser, role, email);
    }

    @PutMapping("updatehotelownerdetails")
    public ResponseEntity<ResponseMessage> updateArenaDetails(@RequestHeader String token,
            @RequestBody ServiceProviderModel latestDetails) {
        return serviceProviderUpdate.UpdateService(token, latestDetails);
    }

    @GetMapping("searchbyaddressandhotelname")
    public List<ServiceProviderModel> searchFunction(@RequestHeader String searchItem) {
        return searchByAddressAndHotelName.searchByAddressAndHotelName(searchItem);
    }

    @GetMapping("getdetailsby")
    public Optional<ServiceProviderModel> getDetailsByHoId(@RequestHeader String hotelownId) {
        return serviceProviderRepository.findById(UUID.fromString(hotelownId));
    }

    @PostMapping("addrating")
    public ResponseEntity<ResponseMessage> rating(@RequestHeader String hotelownId, @RequestHeader float rating) {
        return hotelRating.AvgHotelRating(hotelownId, rating);

    }

    @PostMapping("uploadhotels")
    public ResponseEntity<ResponseMessage> uploadHotels(@RequestHeader String token, @RequestHeader String role,
            @RequestBody List<PriceHotelType> pricePerType) {
        return uploadHotelService.uploadHotelsInfoService(pricePerType, token, role);
    }

    @PutMapping("updatehoteldetails")
    public ResponseEntity<ResponseMessage> updateSportDetails(@RequestBody PriceHotelType latesthotelDetails,
            @RequestHeader UUID roomId) {
        return updateHotelDetials.updateHotelDetailsService(latesthotelDetails, roomId);
    }

    @GetMapping("getdetails")
    public List<HotelsDB> getSPDetails(@RequestHeader String token, @RequestHeader String role) {
        String email = authService.verifyToken(token);
        List<HotelsDB> hotels = hotelDBRepo.findByEmail(email);
        return hotels;

    }

    @PostMapping("bookroom")
    public ResponseEntity<ResponseMessage> bookRoomtByUser(@RequestBody BookRoomHOModel bookRoomHOModel,
            @RequestHeader String token, @RequestHeader String role) {
        return bookRoomService.userRoomBookService(bookRoomHOModel, token, role);
    }

    @PostMapping("checkroom")
    public ResponseEntity<ResponseMessage> checkingfunc(@RequestBody BookRoomHOModel bookRoomHOModelReq) {

        return bookRoomService.checkRoom( bookRoomHOModelReq.getRoomId(), bookRoomHOModelReq.getHotelownId());

               
    }

    @GetMapping("userbookingdetails")
    public BookRoomHOModel userbookingdetails(@RequestHeader String token, @RequestHeader String role,
            @RequestHeader UUID roomBookingId) {
        BookRoomHOModel ob = bookRoomRepo.findByRoomBookingId(roomBookingId);
        return ob;
    }

}
