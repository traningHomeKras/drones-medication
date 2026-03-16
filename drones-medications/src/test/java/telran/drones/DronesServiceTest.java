package telran.drones;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneItemsAmount;
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
import static telran.drones.TestNames.*;
import java.util.List;


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
	DroneMedication DRONE_MEDICATION2 = new DroneMedication("Drone-1", "MED_1");

	
	private static final String SERVICE_TEST = "Service: ";
	@Test
	@DisplayName(SERVICE_TEST + REGISTER_DRONE_NORMAL)
	void testRegisterDrone() {
		DroneDto res = dronesService.registerDrone(DRONE_DTO1);
		assertEquals(DRONE_DTO1, res);
		assertTrue(dronesRepo.existsById("Drone-4"));
	}
	

	@Test
	@DisplayName(SERVICE_TEST + REGISTER_DRONE_ALREADY_EXISTS)
	void testRegisterDroneAlreadyExists() {	
		assertThrowsExactly(DroneAlreaadyExistssException.class, ()-> dronesService.registerDrone(new DroneDto("Drone-1", ModelType.Middleweight)));
		
	}
	@Test
	@DisplayName(SERVICE_TEST + LOAD_DRONE_NORMAL)
	void testLoadDrone() {	
	DroneMedication res = dronesService.loadDrone(DRONE_MEDICATION1);
		assertEquals(DRONE_MEDICATION1, res);
		assertTrue(eventLogRepo.existsByDroneNumber("Drone-1"));
		Drone drone = dronesRepo.findById("Drone-1").orElseThrow(DroneNotFoundException::new);
		assertEquals(drone.getState(), State.LOADING);
	}
	
	@Test
	@DisplayName(SERVICE_TEST + LOAD_DRONE_MISSING_SN)
	void testLoadDroneWithWrongDroneNumber() {
		assertThrowsExactly(DroneNotFoundException.class, ()-> dronesService.loadDrone(new DroneMedication("Wrong-Drone", "MED_1")));
	}
	@Test
	@DisplayName(SERVICE_TEST + LOAD_DRONE_MISSING_MEDICATION_CODE)
	void testLoadDroneWithWrongMedicationCode() {
		assertThrowsExactly(MedicationNotFoundException.class, ()-> dronesService.loadDrone(new DroneMedication("Drone-1", "Wrong-Medication")));
	}
	@Test
	@DisplayName(SERVICE_TEST + LOAD_WRONG_STATE_DRONE)
	void loadWrongStateDerone() {
		assertThrowsExactly(IllegalDroneStateException.class, ()-> dronesService.loadDrone(new DroneMedication("Drone-3", "MED_1")));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + LOAD_DRONE_BATTERY_CAPACITY_LOW)
	void lowBarretyCapacity() {
		assertThrowsExactly(LowBatteryCapacityException.class, ()-> dronesService.loadDrone(new DroneMedication("Drone-2", "MED_1")));
	}
	@Test
	@DisplayName(SERVICE_TEST + LOAD_DRONE_WEIGHT_LIMIT_VIOLATIONString)
	void loadDroneWeigthProbleemTest(){
		assertThrowsExactly(IllegalMedicationWeightException.class, ()-> dronesService.loadDrone(new DroneMedication("Drone-1", "MED_2")));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + CHECK_MEDICATIONS_NORMAL)	
		void checkMedicationItems() {
		dronesService.loadDrone(DRONE_MEDICATION1);
		Drone drone = dronesRepo.findById("Drone-1").orElseThrow(DroneNotFoundException::new);
		drone.setState(State.IDLE);
		dronesRepo.save(drone);
		dronesService.loadDrone(DRONE_MEDICATION2);
		List<String> exp = List.of("MED_1", "MED_1");
		List<String> res = dronesService.checkMedicationItems("Drone-1");
		//assertThat(exp).containsExactlyInAnyOrderElementsOf(res);
	
	}	
	
	@Test
	@DisplayName(SERVICE_TEST + CHECK_UNEXISTING_DRRONE )
	void checkMedicationItemsWithWrongDroneNumber() {
		assertThrowsExactly(DroneNotFoundException.class, ()-> dronesService.checkMedicationItems("Wrong-Drone"));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + DRONES_AVAIBLE_NORMAL)
	void checkAvailableDrones() {
		List<String> exp = List.of("Drone-1", "Drone-2");
		List<String> res = dronesService.checkAvailableDrones();
		assertThat(exp).containsExactlyInAnyOrderElementsOf(res);
		List<Drone> drones = dronesRepo.findAllById(res);
		drones.stream().forEach(d-> d.setState(State.LOADED));
		dronesRepo.saveAll(drones);
		res = dronesService.checkAvailableDrones();
		assertTrue(res.isEmpty());
	}
	
	@Test	
	@DisplayName(SERVICE_TEST + CHECK_DRONES_BATTERY_NORMAL)
	void checkBatteryCapacity() {
		int res = dronesService.checkBatteryCapacity("Drone-1");
		assertEquals(100, res);		
	}
	
	@Test
	@DisplayName(SERVICE_TEST + CHECK_DRONES_BATTERY_UNEXISTING_DRONE)
	void checkBatteryCapacityWithWrongDroneNumber() {
		assertThrowsExactly(DroneNotFoundException.class, ()-> dronesService.checkBatteryCapacity("Wrong-Drone"));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + CHECK_DRONES_LOADED_ITEM_AMOUNTS_NORMAL)
	void checkDroneLoadedItemAmounts() {
		dronesService.loadDrone(DRONE_MEDICATION1);
		Drone drone = dronesRepo.findById("Drone-1").orElseThrow(DroneNotFoundException::new);
		drone.setState(State.IDLE);
		dronesRepo.save(drone);
		dronesService.loadDrone(DRONE_MEDICATION2);
		List<DroneItemsAmountImpl> exp = List.of(new DroneItemsAmountImpl("Drone-1", 2), 
				new DroneItemsAmountImpl("Drone-2", 0), new DroneItemsAmountImpl("Drone-3", 0));
		List<DroneItemsAmount> res = dronesService.checkDroneLoadedItemAmounts();
		List<DroneItemsAmountImpl> res2 = res.stream().map(r -> new DroneItemsAmountImpl(r.getNumber(), r.getAmount())).toList();
		
		assertThat(exp).containsExactlyInAnyOrderElementsOf(res2);
	}
}

