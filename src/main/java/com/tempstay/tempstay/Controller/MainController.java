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
import com.tempstay.tempstay.Models.LoginModel;
import com.tempstay.tempstay.Models.ResponseMessage;
import com.tempstay.tempstay.Models.ServiceProviderModel;
import com.tempstay.tempstay.RatingService.HotelRating;
import com.tempstay.tempstay.Repository.ServiceProviderRepository;
import com.tempstay.tempstay.SearchFunction.SearchByAddressAndHotelName;
import com.tempstay.tempstay.ServiceProviderServices.ServiceProviderUpdate;
import com.tempstay.tempstay.UserServices.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api")
@CrossOrigin(origins="http://localhost:5173")
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceProviderUpdate serviceProviderUpdate ;

    @Autowired
    private SearchByAddressAndHotelName searchByAddressAndHotelName;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private HotelRating playgroundRating;

    
   
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
    public Optional<ServiceProviderModel> getDetailsByHoId(@RequestHeader String HotelownId) {
        return serviceProviderRepository.findById(UUID.fromString(HotelownId));
    }

    @PostMapping("addrating")
    public ResponseEntity<ResponseMessage> rating(@RequestHeader String HotelownId, @RequestHeader float rating) {
        return playgroundRating.AvgHotelRating(HotelownId, rating);
    }

  
   
}
