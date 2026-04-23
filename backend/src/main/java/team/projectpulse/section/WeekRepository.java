package team.projectpulse.section;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeekRepository extends JpaRepository<Week, Integer> {
    List<Week> findBySectionIdOrderByWeekNumber(Integer sectionId);
    Optional<Week> findFirstBySectionIdAndEndDateBeforeOrderByEndDateDesc(Integer sectionId, LocalDate today);

    @Modifying
    @Query("DELETE FROM Week w WHERE w.section.id = :sectionId")
    void deleteBySectionId(Integer sectionId);
}
