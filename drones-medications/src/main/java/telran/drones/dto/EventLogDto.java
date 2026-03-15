package telran.drones.dto;

import java.time.LocalDateTime;

public record EventLogDto(LocalDateTime timeStamp, String droneNumber, State state, int battaryCapacity,
		String medicattionCode) {


}
