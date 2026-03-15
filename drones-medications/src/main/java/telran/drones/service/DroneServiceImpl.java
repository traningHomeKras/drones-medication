package telran.drones.service;
 import static telran.drones.api.PropertiesNames.*;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneItemsAmount;
import telran.drones.dto.DroneMedication;
import telran.drones.dto.State;
import telran.drones.exceptions.DroneAlreaadyExistssException;
import telran.drones.exceptions.DroneNotFoundException;
import telran.drones.exceptions.IllegalDroneStateException;
import telran.drones.exceptions.IllegalMedicationWeightException;
import telran.drones.exceptions.LowBatteryCapacityException;
import telran.drones.exceptions.MedicationNotFoundException;
import telran.drones.exceptions.ModelNotFoundException;
import telran.drones.model.Drone;
import telran.drones.model.DroneModel;
import telran.drones.model.EventLog;
import telran.drones.model.Medication;
import telran.drones.repo.DroneModelRepo;
import telran.drones.repo.DronesRepo;
import telran.drones.repo.EventLogRepo;
import telran.drones.repo.MedicationsRepo;

@Service
@Slf4j
@RequiredArgsConstructor
public class DroneServiceImpl implements DronesService {
final DronesRepo dronesRepo;
final EventLogRepo eventLogRepo;
final MedicationsRepo medicationsRepo;
final DroneModelRepo droneModelRepo;
@Value("${" + CAPACITY_THRESHOLD + ":25}")
private int thresholdBatteryCapacity;

	@Override
	@Transactional
	public DroneDto registerDrone(DroneDto droneDto) {
		if(dronesRepo.existsById(droneDto.serialNumber())) {
			throw new DroneAlreaadyExistssException();
		}
		Drone drone = Drone.of(droneDto);
		
		DroneModel droneModel = droneModelRepo.findById(droneDto.modelType()).orElseThrow(ModelNotFoundException::new);
		drone.setModel(droneModel);
		
		dronesRepo.save(drone);
		log.debug("Drone with SN {} has been registrated", droneDto.serialNumber());
		return droneDto;
	}

	@Override
	@Transactional(readOnly = false)
	public DroneMedication loadDrone(DroneMedication droneMedication) {
		Drone drone = dronesRepo.findById(droneMedication.droneNumber()).orElseThrow(DroneNotFoundException::new);
		Medication medication = medicationsRepo.findById(droneMedication.medicationCode()).orElseThrow(MedicationNotFoundException::new);
		log.debug("Loading drone with SN {} with medic@Testation {}", droneMedication.droneNumber(), droneMedication.medicationCode());
		if(drone.getState()!= State.IDLE) {
		
			throw new IllegalDroneStateException();
		}
		if(drone.getBatteryCapacity() <= thresholdBatteryCapacity) {
			throw new LowBatteryCapacityException();
		}
		if(drone.getModel().getWeight() < medication.getWeight()) {
			throw new IllegalMedicationWeightException();
		}
		drone.setState(State.LOADING);
		EventLog eventLog = new EventLog(LocalDateTime.now(), drone.getNumber(), drone.getState(), drone.getBatteryCapacity(), medication.getCode());
		eventLogRepo.save(eventLog);
		log.debug("new event has been created for drone with SN {}", drone.getNumber());
		return droneMedication;
	}

	@Override
	public List<String> checkMedicationItems(String droneNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> checkAvailableDrones() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int checkBatteryCapacity(String droneNumber) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DroneItemsAmount> checkDroneLoadedItemAmounts() {
		// TODO Auto-generated method stub
		return null;
	}

}
