package telran.drones.exceptions;

import telran.drones.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class DroneAlreaadyExistssException extends IllegalStateException {

	public DroneAlreaadyExistssException() {
		super(ServiceExceptionMessages.DRONE_ALREADY_EXISTS);
		
	}

}
