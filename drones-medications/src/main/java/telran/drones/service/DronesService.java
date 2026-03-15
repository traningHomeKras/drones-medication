package telran.drones.service;


import java.util.List;

import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneItemsAmount;
import telran.drones.dto.DroneMedication;

public interface DronesService {

DroneDto registerDrone(DroneDto droneDto);
DroneMedication loadDrone(DroneMedication droneMedication);
//- check how many medication items have been loaded for each drone, ordered by the amount in the
/**
* checking loaded medication items for a given drone; 
* @param droneNumber
* @return list of medication codes that have been loaded on a given drone (for all time)
*/
List<String> checkMedicationItems(String droneNumber);
/*************************************************************/
/**
* checking available drones for loading;
* @return list of drone numbers that are available for loading
*/
List<String> checkAvailableDrones();
/******************************************************/
/**
* checking drone battery level for a given drone
* @param droneNumber
* @return the battery capacity of a given drone
*/
int checkBatteryCapacity(String droneNumber);
/****************************************************************/
/**
* check how many medication items have been loaded for each drone,
*  ordered by the amount in the descending order
* @return distribution projection
*/
List<DroneItemsAmount >checkDroneLoadedItemAmounts();

}
