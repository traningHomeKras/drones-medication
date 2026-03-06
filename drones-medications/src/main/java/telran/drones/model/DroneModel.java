package telran.drones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import telran.drones.dto.ModelType;

@Entity
@Table(name = "drone_models")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DroneModel {
@Id
@Enumerated(EnumType.STRING)
	ModelType modelType;
@Column(nullable = false)
int weight;
}
