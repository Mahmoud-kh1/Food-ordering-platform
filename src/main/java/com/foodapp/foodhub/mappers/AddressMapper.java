package com.foodapp.foodhub.mappers;

import com.foodapp.foodhub.dto.AddressRequestDto;
import com.foodapp.foodhub.dto.AddressResponseDto;
import com.foodapp.foodhub.entity.UserAddress;

public class AddressMapper {
    // Request -> Entity
    public static UserAddress toEntity(AddressRequestDto dto) {
        UserAddress address = new UserAddress();
        address.setStreet(dto.getStreet());
        address.setBuilding(dto.getBuilding());
        address.setFloor(dto.getFloor());
        address.setApartment(dto.getApartment());
        address.setArea(dto.getArea());
        address.setNotes(dto.getNotes());
        address.setPhone(dto.getPhone());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        return address;
    }

    // Entity -> Response
    public static AddressResponseDto toResponseDto(UserAddress address) {
        AddressResponseDto dto = new AddressResponseDto();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setBuilding(address.getBuilding());
        dto.setFloor(address.getFloor());
        dto.setApartment(address.getApartment());
        dto.setArea(address.getArea());
        dto.setNotes(address.getNotes());
        dto.setPhone(address.getPhone());
        dto.setLatitude(address.getLatitude());
        dto.setLongitude(address.getLongitude());
        return dto;
    }
}
