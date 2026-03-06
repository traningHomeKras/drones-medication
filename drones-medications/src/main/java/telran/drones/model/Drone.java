package telran.drones.model;
import telran.drones.dto.State;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import telran.drones.dto.DroneDto;
import static telran.drones.api.DroneValidationErrorMessages.*;
@Entity
@Table(name = "drones")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class Drone {
	@Id
	@Column(length = MAX_DRONE_SN_LENGHT, name = "drone_number")
	String number;
	@ManyToOne
	@JoinColumn(name = "model_name")
	@Setter
	DroneModel model;
	@Column(name = "battery_capaity", nullable = false)
	@Setter
	int batteryCapecity;
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	State state;
	
	static public Drone of(DroneDto droneDto) {
	return new Drone(droneDto.serialNumber(), null, 100, State.IDLE);
	}
	
	public DroneDto build() {
		return new DroneDto(number, model.getModelType());
	}
}
