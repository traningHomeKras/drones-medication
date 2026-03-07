package telran.drones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import telran.drones.api.DroneValidationErrorMessages;
import telran.drones.api.ServiceExceptionMessages;
import telran.drones.controller.DronesController;
import telran.drones.dto.DroneDto;
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
@WebMvcTest(DronesController.class)
class DronesControllerTest {
	final static String url = "http://localhost:8080/drones";
	private static final String LOAD_URL = url + "/load";
	private  final String SERIAL_NUMBER = "55555-55";
	private  final DroneDto dronDtoNorm = new DroneDto(SERIAL_NUMBER, ModelType.Cruiserweight);
	private final DroneMedication droneMedication = new DroneMedication(SERIAL_NUMBER, "MED123");

	@MockitoBean
	DronesService dronesService;
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;

	@Test
	void droneRegistrationNormalFlow() throws  Exception {
		when(dronesService.registerDrone(dronDtoNorm)).thenReturn(dronDtoNorm);
		String json = objectMapper.writeValueAsString(dronDtoNorm);
		String actJson = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(json, actJson);
	}
		
@Test
	void dronregistrationWithEmptyFields() throws Exception {
		DroneDto dronDtoEmpty = new DroneDto(null, null);
		String json = objectMapper.writeValueAsString(dronDtoEmpty);
		String actJson = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertTrue(actJson.contains(DroneValidationErrorMessages.MISSING_DRONE_SN));
		assertTrue(actJson.contains(DroneValidationErrorMessages.MISSING_MODEL_DRONE));

}
@Test
	void dronregistrationWithTooLongSerialNumber() throws Exception {
		DroneDto dronDtoTooLongSN = new DroneDto("5".repeat(101), ModelType.Cruiserweight);
		String json = objectMapper.writeValueAsString(dronDtoTooLongSN);
		String actJson = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertTrue(actJson.contains(DroneValidationErrorMessages.TO_LONG_DRONE_SN));
}
@Test
	void dronregistrationWithAlreadyExistSerialNumber() throws Exception {
		when(dronesService.registerDrone(dronDtoNorm)).thenThrow(new DroneAlreaadyExistssException());
		String json = objectMapper.writeValueAsString(dronDtoNorm);
		String actJson = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertTrue(actJson.contains("Drone with this SN already exists"));	
}

@Test
void testRegisterDroneWithWrongModelType() throws Exception {
	wronngModelTypeDto dronDtoWrongModel = new wronngModelTypeDto(SERIAL_NUMBER, "WrongModel)");
	String json = objectMapper.writeValueAsString(dronDtoWrongModel);
	String actJson = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
	assertEquals("Wrong JSON format", actJson);
	
	
}

void testLoadDroneNormalFlow() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenReturn(droneMedication);
		String json = objectMapper.writeValueAsString(droneMedication);
		String actJson = mockMvc.perform(post(LOAD_URL).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(json, actJson);
}

@Test
void testLoadDroneWithEmptyFields() throws Exception {
		DroneMedication	 droneMedicationEmpty = new DroneMedication(null, null);
		String json = objectMapper.writeValueAsString(droneMedicationEmpty);
		String actJson = mockMvc.perform(post(LOAD_URL).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertTrue(actJson.contains(DroneValidationErrorMessages.MISSING_DRONE_SN));
		assertTrue(actJson.contains(DroneValidationErrorMessages.MISSING_MEDICATION_CODE));
}

@Test
void testloadMisssingDrone() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(new DroneNotFoundException());
		String json = objectMapper.writeValueAsString(droneMedication);
		String actJson = mockMvc.perform(post(LOAD_URL).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(ServiceExceptionMessages.DRONE_NOD_FOUND_MESSAGE, actJson);
}

@Test
void testLoadDromeWithMissingMedication() throws Exception {
		
		serviceExceptionRequest(new MedicationNotFoundException(), 404, ServiceExceptionMessages.MEDICATION_NOT_EXISTS_MESSSAGE);
}

@Test
void testLoadWrongStateDrone() throws Exception {
	
		serviceExceptionRequest(new IllegalDroneStateException(), 400, ServiceExceptionMessages.ILLEGAL_DRRONE_STATE_MESSAGE);
}
@Test
void testLoadDrronLowBatteryCapecity() throws Exception {
		
		serviceExceptionRequest(new LowBatteryCapacityException(), 400, ServiceExceptionMessages.BATTERY_LOW_MESSAGE);
}

@Test
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
}