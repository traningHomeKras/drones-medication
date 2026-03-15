package telran.drones;

public interface TestNames {
	String REGISTER_DRONE_NORMAL = "Registering drone normal flow";
	String REGISTER_DRONE_MISSING_FIELDS = "Drone JSON with missing fields";
	String REGISTER_DRONE_VALIDATION_VIOLATION = "Drone JSON with  wrong fields";
	String REGISTER_DRONE_ALREADY_EXISTS = "Registering Drone with existing number";
	String REGISTER_DRONE_WRONG_MODEL = "Registering Drone with wrong model";
	
	String LOAD_DRONE_NORMAL = "Loading drone normal flow";
	String LOAD_DRONE_MISSING_FIELDS = "Drone JSON with missing fields";
	String LOAD_DRONE_MISSING_SN = "Load not existing drone";
	String LOAD_DRONE_MISSING_MEDICATION_CODE = "Load drone with wrong medication code";
	String LOAD_WRONG_STATE_DRONE = "Load drone with wrong state";
	String LOAD_DRONE_BATTERY_CAPACITY_LOW = "Load drone with low battery capacity";
	String LOAD_DRONE_WEIGHT_LIMIT_VIOLATIONString = "Load drone with medication weight limit violation";
	
	String CHECK_MEDICATIONS_NORMAL = "Check medications normal flow";
	String CHECK_MEDICATIONS_WRONG_FIELDS = "Check medications with empty SN field";
	String CHECK_UNEXISTING_DRRONE = "Check medications for unexisting drone";
	
	String DRONES_AVAIBLE_NORMAL = "Check available drones normal flow";
	String CHECK_DRONES_BATTERY_NORMAL = "Check battery capacity normal flow";
	String CHECK_DRONES_BATTERY_WRONG_FIELDS = "Check battery capacity with wrong SN";
	String CHECK_DRONES_BATTERY_UNEXISTING_DRONE = "Check battery capacity for unexisting drone";
	String CHECK_DRONES_LOADED_ITEM_AMOUNTS_NORMAL = "Check drone loaded item amounts normal flow";
}
