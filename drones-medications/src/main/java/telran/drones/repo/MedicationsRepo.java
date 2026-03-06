package telran.drones.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.drones.model.Medication;

public interface MedicationsRepo extends JpaRepository<Medication, String> {

}
