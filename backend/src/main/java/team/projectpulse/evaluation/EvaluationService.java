package team.projectpulse.evaluation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.evaluation.dto.BatchEvaluationRequest;
import team.projectpulse.evaluation.dto.SubmitFormDto;
import team.projectpulse.rubric.Criterion;
import team.projectpulse.rubric.CriterionRepository;
import team.projectpulse.section.Week;
import team.projectpulse.section.WeekRepository;
import team.projectpulse.system.exception.ObjectNotFoundException;
import team.projectpulse.team.Team;
import team.projectpulse.team.TeamRepository;
import team.projectpulse.user.PeerEvaluationUser;
import team.projectpulse.user.PeerEvaluationUserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Owner: Whitney (Person 3)
@Service
public class EvaluationService {

    private final PeerEvaluationUserRepository userRepository;
    private final TeamRepository teamRepository;
    private final WeekRepository weekRepository;
    private final CriterionRepository criterionRepository;
    private final PeerEvaluationRepository evaluationRepository;
    private final RatingRepository ratingRepository;

    public EvaluationService(PeerEvaluationUserRepository userRepository,
                             TeamRepository teamRepository,
                             WeekRepository weekRepository,
                             CriterionRepository criterionRepository,
                             PeerEvaluationRepository evaluationRepository,
                             RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.weekRepository = weekRepository;
        this.criterionRepository = criterionRepository;
        this.evaluationRepository = evaluationRepository;
        this.ratingRepository = ratingRepository;
    }

    @Transactional(readOnly = true)
    public SubmitFormDto getSubmitForm(String username) {
        PeerEvaluationUser student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("user", username));

        if (student.getTeamId() == null) {
            throw new IllegalStateException("You are not assigned to a team.");
        }

        Team team = teamRepository.findById(student.getTeamId())
                .orElseThrow(() -> new ObjectNotFoundException("team", student.getTeamId()));

        Integer sectionId = team.getSection().getId();

        Week week = weekRepository.findFirstBySectionIdAndEndDateBeforeOrderByEndDateDesc(sectionId, LocalDate.now())
                .orElseThrow(() -> new IllegalStateException("No completed week found for your section."));

        List<PeerEvaluationUser> teammates = team.getStudents().stream()
                .filter(s -> !s.getId().equals(student.getId()))
                .toList();

        List<Criterion> criteria = team.getSection().getRubric() == null
                ? List.of()
                : team.getSection().getRubric().getCriteria();

        List<PeerEvaluation> existing = evaluationRepository.findByEvaluator_IdAndWeek_Id(student.getId(), week.getId());

        Map<Integer, SubmitFormDto.ExistingEvaluation> existingMap = new LinkedHashMap<>();
        for (PeerEvaluation eval : existing) {
            Map<Integer, BigDecimal> scores = new HashMap<>();
            for (Rating r : eval.getRatings()) {
                scores.put(r.getCriterion().getId(), r.getScore());
            }
            existingMap.put(eval.getEvaluatee().getId(),
                    new SubmitFormDto.ExistingEvaluation(eval.getPublicComments(), eval.getPrivateComments(), scores));
        }

        return new SubmitFormDto(
                new SubmitFormDto.WeekInfo(week.getId(), week.getWeekNumber(), week.getStartDate(), week.getEndDate()),
                teammates.stream().map(t -> new SubmitFormDto.TeammateInfo(t.getId(), t.getFirstName(), t.getLastName())).toList(),
                criteria.stream().map(c -> new SubmitFormDto.CriterionInfo(c.getId(), c.getName(), c.getDescription(), c.getMaxScore())).toList(),
                existingMap
        );
    }

    @Transactional
    public void submitEvaluations(String username, BatchEvaluationRequest request) {
        PeerEvaluationUser evaluator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("user", username));

        Week week = weekRepository.findById(request.weekId())
                .orElseThrow(() -> new ObjectNotFoundException("week", request.weekId()));

        for (BatchEvaluationRequest.EvaluationEntry entry : request.evaluations()) {
            PeerEvaluationUser evaluatee = userRepository.findById(entry.evaluateeId())
                    .orElseThrow(() -> new ObjectNotFoundException("user", entry.evaluateeId()));

            Optional<PeerEvaluation> existing = evaluationRepository
                    .findByEvaluator_IdAndEvaluatee_IdAndWeek_Id(evaluator.getId(), evaluatee.getId(), week.getId());

            PeerEvaluation eval = existing.orElseGet(PeerEvaluation::new);
            eval.setEvaluator(evaluator);
            eval.setEvaluatee(evaluatee);
            eval.setWeek(week);
            eval.setPublicComments(entry.publicComments());
            eval.setPrivateComments(entry.privateComments());
            eval.getRatings().clear();

            for (Map.Entry<Integer, BigDecimal> scoreEntry : entry.scores().entrySet()) {
                Criterion criterion = criterionRepository.findById(scoreEntry.getKey())
                        .orElseThrow(() -> new ObjectNotFoundException("criterion", scoreEntry.getKey()));
                BigDecimal max = criterion.getMaxScore();
                BigDecimal score = scoreEntry.getValue();
                if (score.compareTo(BigDecimal.ONE) < 0 || score.compareTo(max) > 0) {
                    throw new IllegalArgumentException(
                            "Score for criterion '" + criterion.getName() + "' must be between 1 and " + max + ".");
                }
                Rating rating = new Rating();
                rating.setPeerEvaluation(eval);
                rating.setCriterion(criterion);
                rating.setScore(score);
                eval.getRatings().add(rating);
            }

            evaluationRepository.save(eval);
        }
    }
}
