package telran.drones.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;
import telran.drones.dto.State;
import telran.drones.exception.DroneAlreaadyExistssException;
import telran.drones.exception.DroneNotFoundException;
import telran.drones.exception.IllegalDroneStateException;
import telran.drones.exception.IllegalMedicationWeightException;
import telran.drones.exception.LowBatteryCapacityException;
import telran.drones.exception.MedicationNotFoundException;
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

	@Override
	@Transactional(readOnly = true)
	public DroneDto registerDrone(DroneDto droneDto) {
		if(dronesRepo.existsById(droneDto.serialNumber())) {
			throw new DroneAlreaadyExistssException();
		}
		Drone drone = Drone.of(droneDto);
		
		DroneModel droneModel = droneModelRepo.findById(droneDto.modelType()).orElseThrow(()-> new ModelNotFoundException());
		drone.setModel(droneModel);
		
		dronesRepo.save(drone);
		log.debug("Drone with SN {} has been registrated", droneDto.serialNumber());
		return droneDto;
	}

	@Override
	@Transactional(readOnly = false)
	public DroneMedication loadDrrone(DroneMedication droneMedication) {
		Drone drone = dronesRepo.findById(droneMedication.droneNumber()).orElseThrow(()-> new DroneNotFoundException());
		Medication medication = medicationsRepo.findById(droneMedication.medicationCode()).orElseThrow(()-> new MedicationNotFoundException());
		if(drone.getState()!= State.IDLE) {
			throw new IllegalDroneStateException();
		}
		if(drone.getBatteryCapacity() <= 25) {
			throw new LowBatteryCapacityException();
		}
		if(drone.getModel().getWeight() < medication.getWeight()) {
			throw new IllegalMedicationWeightException();
		}
		drone.setState(State.LOADING);
		EventLog eventLog = new EventLog(LocalDateTime.now(), drone.getNumber(), drone.getState(), drone.getBatteryCapacity());
		eventLogRepo.save(eventLog);
		
		return droneMedication;
	}

}
