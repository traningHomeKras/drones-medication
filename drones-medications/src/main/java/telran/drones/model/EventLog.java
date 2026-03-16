package telran.drones.model;

import telran.drones.dto.EventLogDto;
import telran.drones.dto.State;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_logs")
@NoArgsConstructor
public class EventLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	long  id;
	LocalDateTime timeStamp;
	@Column(name = "drone_number")
	String droneNumber;
	@Enumerated(EnumType.STRING)
	State state;
	@Column(name = "battery_capacity")
	int batteryCapacity;
	@Column(name = "medication_code")	
	String medicationCode;
	public EventLog(LocalDateTime timeStamp, String droneNumber, State state, int batteryCapacity, String medicattionCode) {
		
		this.timeStamp = timeStamp;
		this.droneNumber = droneNumber;
		this.state = state;
		this.batteryCapacity = batteryCapacity;
		this.medicationCode = medicattionCode;
	}

	public EventLogDto build() {
		return new EventLogDto(timeStamp, droneNumber, state, batteryCapacity, medicationCode);
	}
}
