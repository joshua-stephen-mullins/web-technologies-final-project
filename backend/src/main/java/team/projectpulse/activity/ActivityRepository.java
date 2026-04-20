package team.projectpulse.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Owner: Oscar (Person 2)
public interface ActivityRepository extends JpaRepository<Activity, Integer>, JpaSpecificationExecutor<Activity> {

    List<Activity> findByStudentIdAndWeekId(Integer studentId, Integer weekId);

    @Query("SELECT a FROM Activity a WHERE a.student.teamId = :teamId AND a.week.id = :weekId")
    List<Activity> findByTeamIdAndWeekId(@Param("teamId") Integer teamId, @Param("weekId") Integer weekId);

    @Query("SELECT a FROM Activity a WHERE a.student.id = :studentId AND a.week.id IN :weekIds ORDER BY a.week.id ASC")
    List<Activity> findByStudentIdAndWeekIdIn(@Param("studentId") Integer studentId, @Param("weekIds") List<Integer> weekIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM Activity a WHERE a.student.id IN :studentIds")
    void deleteByStudentIdIn(@Param("studentIds") List<Integer> studentIds);
}
