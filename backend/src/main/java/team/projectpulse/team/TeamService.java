package team.projectpulse.team;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.List;

// Owner: Oscar (Person 2)
// Related UCs: UC-7,8,9,10,12,13,14
@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    // UC-7: Find teams (minimal; Oscar will add search/filter params)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<Team> findAllTeams() {
        return teamRepository.findAll();
    }

    // UC-8: View team details
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public Team findTeamById(Integer id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("team", id));
    }

    // TODO: createTeam(request): Team                            UC-9
    // TODO: updateTeam(id, request): Team                        UC-10
    // TODO: assignStudents(teamId, studentIds): void             UC-12
    // TODO: removeStudent(teamId, studentId): void               UC-13
    // TODO: deleteTeam(id): void                                 UC-14
}
