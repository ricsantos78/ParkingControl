package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.impl.ParkingSpotServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping("/api/parking-spot")
public class ParkingSpotController {

    final ParkingSpotServiceImpl parkingSpotServiceImpl;

    public ParkingSpotController(ParkingSpotServiceImpl parkingSpotServiceImpl) {
        this.parkingSpotServiceImpl = parkingSpotServiceImpl;
    }

    @PostMapping("/add")
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){
        if(parkingSpotServiceImpl.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate car Is already in use!");
        }
        if(parkingSpotServiceImpl.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
        }
        if(parkingSpotServiceImpl.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already for this apartment/block!");
        }
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto,parkingSpotModel);//Converte DTO para Model
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotServiceImpl.save(parkingSpotModel));
    }

    @GetMapping()
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(@PageableDefault(sort = "id", direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotServiceImpl.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModel = parkingSpotServiceImpl.findById(id);
        return parkingSpotModel.<ResponseEntity<Object>>
                map(spotModel -> ResponseEntity.status(HttpStatus.OK).body(spotModel))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(httpStatusNotFound()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModel = parkingSpotServiceImpl.findById(id);
        if(parkingSpotModel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(httpStatusNotFound());
        }
        parkingSpotServiceImpl.delete(parkingSpotModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,
                                                    @RequestBody @Valid ParkingSpotDto parkingSpotDto){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotServiceImpl.findById(id);
        if(parkingSpotModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(httpStatusNotFound());
        }
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto,parkingSpotModel);
        parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotServiceImpl.save(parkingSpotModel));
    }

    private String httpStatusNotFound() {
        return "Parking Spot Not Found.";
    }
}
