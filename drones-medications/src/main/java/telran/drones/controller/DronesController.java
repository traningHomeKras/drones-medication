package telran.drones.controller;


import static telran.drones.api.DroneValidationErrorMessages.MAX_DRONE_SN_LENGHT;
import static telran.drones.api.DroneValidationErrorMessages.MISSING_DRONE_SN;
import static telran.drones.api.DroneValidationErrorMessages.TO_LONG_DRONE_SN;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneItemsAmount;
import telran.drones.dto.DroneMedication;
import telran.drones.service.DronesService;
import static telran.drones.api.UrlConstants.*;
@RestController
@RequiredArgsConstructor
@Slf4j
public class DronesController {
final DronesService dronesService;

@PostMapping((DRONES))
public DroneDto registerDrone(
		@RequestBody
		@Valid
		DroneDto droneDto) {
	
	log.debug("redister drone: drone {}", droneDto);
			return dronesService.registerDrone(droneDto);
	
}

@PostMapping(LOAD_DRONE)
public DroneMedication loadDrone(
		@RequestBody
		@Valid
		DroneMedication droneMedication) {
	log.debug("load drone : loading drone {}", droneMedication);
return dronesService.loadDrone(droneMedication);
}

@GetMapping(MEDICATIONS +"/{droneNumber}")
public List<String> checkMedicationItems(
		@PathVariable
		@NotEmpty(message = MISSING_DRONE_SN)
		@Size(max = MAX_DRONE_SN_LENGHT, message = TO_LONG_DRONE_SN)
		String droneNumber) {
	log.debug("check medication items for: drone number {}", droneNumber);
	return  dronesService.checkMedicationItems(droneNumber);
}


@GetMapping(DRONES_AVAILABLE)
public List<String> checkAvailableDrones() {
	log.debug("check available drones for loading");
	return dronesService.checkAvailableDrones();
}

@GetMapping(DRONES_BATTERY + "/{droneNumber}")
public int checkBatteryCapacity(
		@PathVariable
		@NotEmpty(message = MISSING_DRONE_SN)
		@Size(max = MAX_DRONE_SN_LENGHT, message = TO_LONG_DRONE_SN)String droneNumber) {
log.debug("check battery capacity for: drone number {}", droneNumber);
	return dronesService.checkBatteryCapacity(droneNumber);
}

@GetMapping(DRONES_AMOUNT)
List<DroneItemsAmount> checkDroneLoadedItemAmounts() {
	log.debug("check drone loaded item amounts");
	return dronesService.checkDroneLoadedItemAmounts();
}
}
