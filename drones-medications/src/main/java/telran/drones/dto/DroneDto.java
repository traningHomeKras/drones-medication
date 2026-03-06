package telran.drones.dto;
import static telran.drones.api.DroneValidationErrorMessages.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DroneDto(
		@NotEmpty(message = MISSING_DRONE_SN)
		@Size(max = MAX_DRONE_SN_LENGHT, message = TO_LONG_DRONE_SN)
		String serialNumber, 
		@NotNull(message = MISSING_MODEL_DRONE)
		ModelType modelType) {


}
