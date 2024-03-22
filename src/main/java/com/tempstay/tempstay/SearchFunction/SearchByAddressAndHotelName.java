package com.tempstay.tempstay.SearchFunction;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tempstay.tempstay.Models.ServiceProviderModel;
import com.tempstay.tempstay.Repository.ServiceProviderRepository;



@Service
public class SearchByAddressAndHotelName {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    public List<ServiceProviderModel> searchByAddressAndHotelName(String searchItem) {
        List<ServiceProviderModel> searchResults = serviceProviderRepository.findByAddressAndHotelName(searchItem);
        return searchResults;
    }
}
