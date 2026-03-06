package telran.drones.exception;

import telran.drones.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class IllegalDroneStateException extends IllegalStateException {

	public IllegalDroneStateException() {
		super(ServiceExceptionMessages.ILLEGAL_DRRONE_STATE_MESSAGE);
		
	}

}
