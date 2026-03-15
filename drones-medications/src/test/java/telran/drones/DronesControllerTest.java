package telran.drones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static telran.drones.TestNames.*;
import static telran.drones.api.UrlConstants.*;
import static telran.drones.api.DroneValidationErrorMessages.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import telran.drones.api.DroneValidationErrorMessages;
import telran.drones.api.ServiceExceptionMessages;
import telran.drones.controller.DronesController;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneItemsAmount;
import telran.drones.dto.DroneMedication;
import telran.drones.dto.ModelType;
import telran.drones.exceptions.DroneAlreaadyExistssException;
import telran.drones.exceptions.DroneNotFoundException;
import telran.drones.exceptions.IllegalDroneStateException;
import telran.drones.exceptions.IllegalMedicationWeightException;
import telran.drones.exceptions.LowBatteryCapacityException;
import telran.drones.exceptions.MedicationNotFoundException;
import telran.drones.service.DronesService;
import tools.jackson.databind.ObjectMapper;
record wronngModelTypeDto(String serialNumber, String modelType){}

@AllArgsConstructor

class DroneItemsAmountImpl implements DroneItemsAmount {
	@Getter
	String number;
	@Getter
	long amount;
	@Override
	public long getAmmount() {
		return amount;
	}
	
	
	
}
@WebMvcTest(DronesController.class)
class DronesControllerTest {
	final static String HOST = "http://localhost:8080/";
	final static String url = HOST + DRONES;
	private static final String LOAD_URL = HOST + LOAD_DRONE;
	private static final String MEDICATIONS_URL = HOST + MEDICATIONS;
	private static final String AVAIBLE_URL = HOST + DRONES_AVAILABLE;
	private static final String BATTERRY_URL = HOST + DRONES_BATTERY;
	private static final String DRONES_ANOUNT_URL = HOST + DRONES_AMOUNT;

	
	
	private  final String SERIAL_NUMBER = "55555-55";
	private  final DroneDto dronDtoNorm = new DroneDto(SERIAL_NUMBER, ModelType.Cruiserweight);
	private final DroneMedication droneMedication = new DroneMedication(SERIAL_NUMBER, "MED123");
	private final String CONTROLLER = "Controller test: ";
	@MockitoBean
	DronesService dronesService;
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName(CONTROLLER + REGISTER_DRONE_NORMAL)
	void droneRegistrationNormalFlow() throws  Exception {
		when(dronesService.registerDrone(dronDtoNorm)).thenReturn(dronDtoNorm);
		String json = objectMapper.writeValueAsString(dronDtoNorm);
		String actJson = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(json, actJson);
	}
		
@Test
@DisplayName(CONTROLLER + REGISTER_DRONE_MISSING_FIELDS)
	void dronRegistrationWithEmptyFields() throws Exception {
		DroneDto dronDtoEmpty = new DroneDto(null, null);
		String json = objectMapper.writeValueAsString(dronDtoEmpty);
		String actJson = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertTrue(actJson.contains(MISSING_DRONE_SN));
		assertTrue(actJson.contains(MISSING_MODEL_DRONE));

}
@Test
@DisplayName(CONTROLLER + REGISTER_DRONE_VALIDATION_VIOLATION)
	void dronRegistrationWithStTooLongSerialNumber() throws Exception {
		DroneDto dronDtoTooLongSN = new DroneDto("5".repeat(101), ModelType.Cruiserweight);
		String json = objectMapper.writeValueAsString(dronDtoTooLongSN);
		String actJson = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertTrue(actJson.contains(TO_LONG_DRONE_SN));
}
@Test
@DisplayName(CONTROLLER + REGISTER_DRONE_ALREADY_EXISTS)
	void dronRegistrationWithAlreadyExistSerialNumber() throws Exception {
		when(dronesService.registerDrone(dronDtoNorm)).thenThrow(new DroneAlreaadyExistssException());
		String json = objectMapper.writeValueAsString(dronDtoNorm);
		String actJson = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertTrue(actJson.contains("Drone with this SN already exists"));	
}

@Test
@DisplayName(CONTROLLER + REGISTER_DRONE_WRONG_MODEL)
void testRegisterDroneWithWrongModelType() throws Exception {
	wronngModelTypeDto dronDtoWrongModel = new wronngModelTypeDto(SERIAL_NUMBER, "WrongModel)");
	String json = objectMapper.writeValueAsString(dronDtoWrongModel);
	String actJson = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
	assertEquals("Wrong JSON format", actJson);
	
	
}
@Test
@DisplayName(CONTROLLER + LOAD_DRONE_NORMAL)
void testLoadDroneNormalFlow() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenReturn(droneMedication);
		String json = objectMapper.writeValueAsString(droneMedication);
		String actJson = mockMvc.perform(post(LOAD_URL).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(json, actJson);
}

@Test
@DisplayName(CONTROLLER + LOAD_DRONE_MISSING_FIELDS)
void testLoadDroneWithEmptyFields() throws Exception {
		DroneMedication	 droneMedicationEmpty = new DroneMedication(null, null);
		String json = objectMapper.writeValueAsString(droneMedicationEmpty);
		String actJson = mockMvc.perform(post(LOAD_URL).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertTrue(actJson.contains(DroneValidationErrorMessages.MISSING_DRONE_SN));
		assertTrue(actJson.contains(DroneValidationErrorMessages.MISSING_MEDICATION_CODE));
}

@Test
@DisplayName(CONTROLLER + LOAD_DRONE_MISSING_SN)
void testloadMisssingDrone() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(new DroneNotFoundException());
		String json = objectMapper.writeValueAsString(droneMedication);
		String actJson = mockMvc.perform(post(LOAD_URL).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(ServiceExceptionMessages.DRONE_NOD_FOUND_MESSAGE, actJson);
}

@Test
@DisplayName(CONTROLLER + LOAD_DRONE_MISSING_MEDICATION_CODE)
void testLoadDromeWithMissingMedication() throws Exception {
		
		serviceExceptionRequest(new MedicationNotFoundException(), 404, ServiceExceptionMessages.MEDICATION_NOT_EXISTS_MESSSAGE);
}

@Test
@DisplayName(CONTROLLER + LOAD_WRONG_STATE_DRONE)
void testLoadWrongStateDrone() throws Exception {
	
		serviceExceptionRequest(new IllegalDroneStateException(), 400, ServiceExceptionMessages.ILLEGAL_DRRONE_STATE_MESSAGE);
}
@Test
@DisplayName(CONTROLLER + LOAD_DRONE_BATTERY_CAPACITY_LOW)
void testLoadDrronLowBatteryCapecity() throws Exception {
		
		serviceExceptionRequest(new LowBatteryCapacityException(), 400, ServiceExceptionMessages.BATTERY_LOW_MESSAGE);
}

@Test
@DisplayName(CONTROLLER + LOAD_DRONE_WEIGHT_LIMIT_VIOLATIONString)
void testLoadDrroneWithTooHeavyMedication() throws Exception {

		serviceExceptionRequest(new IllegalMedicationWeightException(), 400, ServiceExceptionMessages.WEIGHT_LIMIT_VIOLATION);
		
}

private void serviceExceptionRequest(RuntimeException serviceException, int statusCode, String errorMessage)
		throws  Exception {
	when(dronesService.loadDrone(droneMedication)).thenThrow(serviceException);
	String droneMedicationJSON = objectMapper.writeValueAsString(droneMedication);
	String response = mockMvc.perform(post(LOAD_URL ).contentType(MediaType.APPLICATION_JSON)
			.content(droneMedicationJSON)).andExpect(status().is(statusCode)).andReturn().getResponse().getContentAsString();
	assertEquals(errorMessage, response);
}

@Test
@DisplayName(CONTROLLER + CHECK_MEDICATIONS_NORMAL)
void checkMedicationsItemsNormalTest() throws Exception {
	String[] medications = {"MED_1", "MED_2"};
	String exp = objectMapper.writeValueAsString(medications);
	when(dronesService.checkMedicationItems(SERIAL_NUMBER)).thenReturn(List.of(medications));
	String act = mockMvc.perform(get(MEDICATIONS_URL + "/" + SERIAL_NUMBER))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			assertEquals(exp, act);
	
}

@Test
@DisplayName(CONTROLLER+ CHECK_MEDICATIONS_WRONG_FIELDS)
void checkMedicationsItemsWitWrrongSN() throws Exception {
	String response = mockMvc.perform(get(MEDICATIONS_URL + "/" +"k".repeat(101)))
			.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
	assertEquals(TO_LONG_DRONE_SN, response);
}

@Test
@DisplayName(CONTROLLER + CHECK_UNEXISTING_DRRONE)
void checkMedicationsItemsForUnexistingDrone() throws Exception {
		when(dronesService.checkMedicationItems(SERIAL_NUMBER)).thenThrow(new DroneNotFoundException());
	String response = mockMvc.perform(get(MEDICATIONS_URL + "/" + SERIAL_NUMBER))
			.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
	assertEquals(ServiceExceptionMessages.DRONE_NOD_FOUND_MESSAGE, response);
	
}

@Test
@DisplayName(CONTROLLER + DRONES_AVAIBLE_NORMAL)
void checkAvailableDronesNormalTest() throws Exception {
		String[] drones = {"Drone-1", "Drone-2"};
	String exp = objectMapper.writeValueAsString(drones);
	when(dronesService.checkAvailableDrones()).thenReturn(List.of(drones));
	String act = mockMvc.perform(get(AVAIBLE_URL))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			assertEquals(exp, act);
	when(dronesService.checkAvailableDrones()).thenReturn(List.of());
	act = mockMvc.perform(get(AVAIBLE_URL)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
	assertEquals(objectMapper.writeValueAsString(new String[0]), act);
}


@Test
@DisplayName(CONTROLLER + CHECK_DRONES_BATTERY_NORMAL)
void checkBatteryCapacityNormalTest() throws Exception {
		int batteryCapacity = 75;
	String exp = String.valueOf(batteryCapacity);
	when(dronesService.checkBatteryCapacity(SERIAL_NUMBER)).thenReturn(batteryCapacity);
	String act = mockMvc.perform(get(BATTERRY_URL + "/" + SERIAL_NUMBER))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			assertEquals(exp, act);	
}

@Test
@DisplayName(CONTROLLER + CHECK_DRONES_BATTERY_WRONG_FIELDS)
void checkBatteryCapacityWithWrongSN() throws Exception {
		String response = mockMvc.perform(get(BATTERRY_URL + "/" +"k".repeat(101)))
			.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
	assertEquals(TO_LONG_DRONE_SN, response);
	
}

@Test
@DisplayName(CONTROLLER + CHECK_DRONES_BATTERY_UNEXISTING_DRONE)
void checkBatteryCapacityForUnexistingDrone() throws Exception {
		when(dronesService.checkBatteryCapacity(SERIAL_NUMBER)).thenThrow(new DroneNotFoundException());
	String response = mockMvc.perform(get(BATTERRY_URL + "/" + SERIAL_NUMBER))
			.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
	assertEquals(ServiceExceptionMessages.DRONE_NOD_FOUND_MESSAGE, response);
	
	
}
@Test
@DisplayName(CONTROLLER + CHECK_DRONES_LOADED_ITEM_AMOUNTS_NORMAL)
void chckDroneLoadedItemAmounts() throws Exception {
	DroneItemsAmount droneItemsAmount1 = new DroneItemsAmountImpl("Drone-1", 5);
	DroneItemsAmount droneItemsAmount2 = new DroneItemsAmountImpl("Drone-2", 3);		
	when(dronesService.checkDroneLoadedItemAmounts()).thenReturn(List.of(droneItemsAmount1, droneItemsAmount2));
	String act = mockMvc.perform(get(DRONES_ANOUNT_URL))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			assertEquals(objectMapper.writeValueAsString(List.of(droneItemsAmount1, droneItemsAmount2)), act);
			
}


}