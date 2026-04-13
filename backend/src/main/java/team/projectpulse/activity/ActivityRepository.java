package team.projectpulse.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// Owner: Oscar (Person 2)
public interface ActivityRepository extends JpaRepository<Activity, Integer>, JpaSpecificationExecutor<Activity> {
    // TODO: findByStudentIdAndWeekId(Integer studentId, Integer weekId): List<Activity>
    // TODO: findByWeekIdAndStudentTeamId(Integer weekId, Integer teamId): List<Activity>
}
