package telran.drones.service;

import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;

public interface DronesService {
	/*
	 * adds new drone into data base
	 * return drone if ok or exception other else IllegalSttateException(drone with sn already exists)
	 */
DroneDto registerDrone(DroneDto droneDto);
/**
 * checks whether a given drone available for loading(state idle, battery >=25%, weight match, checks whether given mediation exists )
 * @param droneMedication
 * crreates event log
 * chekes model type drone model exists
 * changes status loading and  current bataery capacity
 * @return all kindes exceptions
 */
DroneMedication loadDrrone(DroneMedication droneMedication);
}
