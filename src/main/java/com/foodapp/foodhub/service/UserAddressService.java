package com.foodapp.foodhub.service;

import com.foodapp.foodhub.dto.AddressRequestDto;
import com.foodapp.foodhub.dto.AddressResponseDto;
import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.entity.UserAddress;
import com.foodapp.foodhub.mappers.AddressMapper;
import com.foodapp.foodhub.repository.UserAddressRepository;
import com.foodapp.foodhub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@Service
public class UserAddressService {


    private final UserAddressRepository addressRepo;
    private final UserRepository userRepo;


    @Transactional
    public AddressResponseDto addAddress(Long userId, AddressRequestDto dto) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        validateLatLng(dto.getLatitude(), dto.getLongitude());

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        address.setArea(dto.getArea());
        address.setStreet(dto.getStreet());
        address.setPhone(dto.getPhone());
        address.setNotes(dto.getNotes());
        address.setBuilding(dto.getBuilding());
        address.setFloor(dto.getFloor());
        address.setApartment(dto.getApartment());

        UserAddress saved = addressRepo.save(address);
        return AddressMapper.toResponseDto(saved);
    }


    @Transactional
    public AddressResponseDto updateAddress(Long userId, Long addressId, AddressRequestDto dto) {

        UserAddress addr = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        if (!addr.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Address not yours");
        }

        validateLatLng(dto.getLatitude(), dto.getLongitude());

        addr.setLatitude(dto.getLatitude());
        addr.setLongitude(dto.getLongitude());
        addr.setPhone(dto.getPhone());
        addr.setNotes(dto.getNotes());
        addr.setBuilding(dto.getBuilding());
        addr.setFloor(dto.getFloor());
        addr.setApartment(dto.getApartment());

        UserAddress saved = addressRepo.save(addr);
        return AddressMapper.toResponseDto(saved);
    }


    @Transactional
    public void deleteAddress(Long userId, Long addressId) {

        UserAddress addr = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        if (!addr.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Address not yours");
        }

        addressRepo.delete(addr);
    }


    public List<AddressResponseDto> listAddresses(Long userId) {
        return addressRepo.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(AddressMapper::toResponseDto)
                .toList();
    }


    public AddressResponseDto getLatestAddress(Long userId) {
        return addressRepo.findTopByUserIdOrderByIdDesc(userId)
                .map(AddressMapper::toResponseDto)
                .orElse(null);
    }


    private void validateLatLng(Double lat, Double lng) {
        if (lat == null || lng == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Latitude & longitude required");
        }
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid coordinates");
        }
    }
}
