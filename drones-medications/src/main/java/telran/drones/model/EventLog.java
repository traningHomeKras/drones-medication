package telran.drones.model;

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
	@Column(name = "battary_capacity")
	int battaryCapacity;
	public EventLog(LocalDateTime timeStamp, String droneNumber, State state, int battaryCapacity) {
		
		this.timeStamp = timeStamp;
		this.droneNumber = droneNumber;
		this.state = state;
		this.battaryCapacity = battaryCapacity;
	}

}
