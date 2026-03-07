package telran.drones.api;

public interface DroneValidationErrorMessages {
int MAX_DRONE_SN_LENGHT = 100;
String TO_LONG_DRONE_SN = "Serial number lenght must be max " + MAX_DRONE_SN_LENGHT;
String MISSING_DRONE_SN = "Missing drone serial number";
String MISSING_MODEL_DRONE = "Missing model drone";

String MISSING_MEDICATION_CODE = "Missig medication code";
String WRONG_MEDICATION_CODE = "Wrong medication code";
}
