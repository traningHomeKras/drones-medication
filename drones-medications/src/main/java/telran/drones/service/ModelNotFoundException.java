package telran.drones.service;

import telran.drones.api.ServiceExceptionMessages;
import telran.drones.exception.NotFoundException;

@SuppressWarnings("serial")
public class ModelNotFoundException extends NotFoundException {

	public ModelNotFoundException() {
		super(ServiceExceptionMessages.MODEL_NOT_FOUND_MESSAGE);
		
	}

}
