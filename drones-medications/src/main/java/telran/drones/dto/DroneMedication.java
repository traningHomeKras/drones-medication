package telran.drones.dto;
import static telran.drones.api.DroneValidationErrorMessages.*;
import static telran.drones.api.ConstraintConstants.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DroneMedication(
		@NotEmpty(message = MISSING_DRONE_SN)
		@Size(max = MAX_DRONE_SN_LENGHT, message = TO_LONG_DRONE_SN)
		String droneNumber,
		@NotEmpty(message = MISSING_MEDICATION_CODE)
		@Pattern(regexp = MEDICATION_CODE_VALIDATION_REG_EX, message = WRONG_MEDICATION_CODE)
		String medicationCode) {

}
