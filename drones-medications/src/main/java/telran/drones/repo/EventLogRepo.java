package telran.drones.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.drones.dto.DroneItemsAmount;
import telran.drones.dto.State;
import telran.drones.model.EventLog;
import telran.drones.projection.MedicationCode;

public interface EventLogRepo extends JpaRepository<EventLog, Long> {

	boolean existsByDroneNumber(String string);

	List<MedicationCode> findByDroneNumberAndState(String droneNumber, State state);

	@Query(value = """
		select d.drone_number as number, count(el.drone_number) as amount from (select
		* from event_logs where state='LOADING') el right join drones d on el.drone_number=d.drone_number
		 group by d.drone_number order by count(el.drone_number) desc
		""", nativeQuery = true)
	List<DroneItemsAmount> findDroneItemsAmount();

}
