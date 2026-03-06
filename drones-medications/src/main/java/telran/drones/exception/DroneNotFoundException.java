package telran.drones.exception;


import telran.drones.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class DroneNotFoundException extends NotFoundException {
 public DroneNotFoundException (){
	super(ServiceExceptionMessages.DRONE_NOD_FOUND_MESSAGE);
}
}
