package telran.drones;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;
import telran.drones.dto.ModelType;
import telran.drones.dto.State;
import telran.drones.exceptions.DroneAlreaadyExistssException;
import telran.drones.exceptions.DroneNotFoundException;
import telran.drones.exceptions.IllegalDroneStateException;
import telran.drones.exceptions.IllegalMedicationWeightException;
import telran.drones.exceptions.LowBatteryCapacityException;
import telran.drones.exceptions.MedicationNotFoundException;
import telran.drones.model.Drone;
import telran.drones.repo.DroneModelRepo;
import telran.drones.repo.DronesRepo;
import telran.drones.repo.EventLogRepo;
import telran.drones.repo.MedicationsRepo;
import telran.drones.service.DronesService;


@SpringBootTest
@Sql(scripts = "classpath:test_drones_sql.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

class DronesServiceTest {

	@Autowired
	DronesService dronesService;
	@Autowired
	DronesRepo dronesRepo;
	@Autowired
	MedicationsRepo medicationsRepo;
	@Autowired
	DroneModelRepo droneModelRepo;
	@Autowired
	EventLogRepo eventLogRepo;
	
	DroneDto DRONE_DTO1 = new DroneDto("Drone-4", ModelType.Cruiserweight);
	DroneMedication DRONE_MEDICATION1 = new DroneMedication("Drone-1", "MED_1");
	@Test
	void testRegisterDrone() {
		DroneDto res = dronesService.registerDrone(DRONE_DTO1);
		assertEquals(DRONE_DTO1, res);
		assertTrue(dronesRepo.existsById("Drone-4"));
	}
	

	@Test
	void testRegisterDroneAlreadyExists() {	
		assertThrowsExactly(DroneAlreaadyExistssException.class, ()-> dronesService.registerDrone(new DroneDto("Drone-1", ModelType.Middleweight)));
		
	}
	@Test
	void testLoadDrone() {	
	DroneMedication res = dronesService.loadDrone(DRONE_MEDICATION1);
		assertEquals(DRONE_MEDICATION1, res);
		assertTrue(eventLogRepo.existsByDroneNumber("Drone-1"));
		Drone drone = dronesRepo.findById("Drone-1").orElseThrow(DroneNotFoundException::new);
		assertEquals(drone.getState(), State.LOADING);
	}
	@Test
	void testLoadDroneWithWrongDroneNumber() {
		assertThrowsExactly(DroneNotFoundException.class, ()-> dronesService.loadDrone(new DroneMedication("Wrong-Drone", "MED_1")));
	}
	@Test
	void testLoadDroneWithWrongMedicationCode() {
		assertThrowsExactly(MedicationNotFoundException.class, ()-> dronesService.loadDrone(new DroneMedication("Drone-1", "Wrong-Medication")));
	}
	@Test
	void loadWrongStateDerone() {
		assertThrowsExactly(IllegalDroneStateException.class, ()-> dronesService.loadDrone(new DroneMedication("Drone-3", "MED_1")));
	}
	
	@Test
	void lowBarretyCapacity() {
		assertThrowsExactly(LowBatteryCapacityException.class, ()-> dronesService.loadDrone(new DroneMedication("Drone-2", "MED_1")));
	}
	@Test
	void loadDroneWeigthProbleemTest(){
		assertThrowsExactly(IllegalMedicationWeightException.class, ()-> dronesService.loadDrone(new DroneMedication("Drone-1", "MED_2")));
	}
}
