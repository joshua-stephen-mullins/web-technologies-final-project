package team.projectpulse.evaluation;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.evaluation.dto.BatchEvaluationRequest;
import team.projectpulse.evaluation.dto.MyEvaluationReportDto;
import team.projectpulse.evaluation.dto.SectionEvaluationReportDto;
import team.projectpulse.evaluation.dto.SectionSummaryDto;
import team.projectpulse.evaluation.dto.SubmitFormDto;
import team.projectpulse.instructor.Instructor;
import team.projectpulse.instructor.InstructorRepository;
import team.projectpulse.rubric.Criterion;
import team.projectpulse.rubric.CriterionRepository;
import team.projectpulse.section.Section;
import team.projectpulse.section.Week;
import team.projectpulse.section.WeekRepository;
import team.projectpulse.system.exception.ObjectNotFoundException;
import team.projectpulse.team.Team;
import team.projectpulse.team.TeamRepository;
import team.projectpulse.user.PeerEvaluationUser;
import team.projectpulse.user.PeerEvaluationUserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final InstructorRepository instructorRepository;

    public EvaluationService(PeerEvaluationUserRepository userRepository,
                             TeamRepository teamRepository,
                             WeekRepository weekRepository,
                             CriterionRepository criterionRepository,
                             PeerEvaluationRepository evaluationRepository,
                             RatingRepository ratingRepository,
                             InstructorRepository instructorRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.weekRepository = weekRepository;
        this.criterionRepository = criterionRepository;
        this.evaluationRepository = evaluationRepository;
        this.ratingRepository = ratingRepository;
        this.instructorRepository = instructorRepository;
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

    // UC-29: available completed weeks for the student's section
    @Transactional(readOnly = true)
    public List<SubmitFormDto.WeekInfo> getMyReportWeeks(String username) {
        PeerEvaluationUser student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("user", username));

        if (student.getTeamId() == null) {
            throw new IllegalStateException("You are not assigned to a team.");
        }

        Team team = teamRepository.findById(student.getTeamId())
                .orElseThrow(() -> new ObjectNotFoundException("team", student.getTeamId()));

        Integer sectionId = team.getSection().getId();
        LocalDate today = LocalDate.now();

        return weekRepository.findBySectionIdOrderByWeekNumber(sectionId).stream()
                .filter(w -> w.getEndDate().isBefore(today))
                .sorted((a, b) -> b.getEndDate().compareTo(a.getEndDate()))
                .map(w -> new SubmitFormDto.WeekInfo(w.getId(), w.getWeekNumber(), w.getStartDate(), w.getEndDate()))
                .toList();
    }

    // UC-29: generate a student's own peer evaluation report for a given week
    @Transactional(readOnly = true)
    public MyEvaluationReportDto getMyReport(String username, Integer weekId) {
        PeerEvaluationUser student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("user", username));

        if (student.getTeamId() == null) {
            throw new IllegalStateException("You are not assigned to a team.");
        }

        Team team = teamRepository.findById(student.getTeamId())
                .orElseThrow(() -> new ObjectNotFoundException("team", student.getTeamId()));

        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new ObjectNotFoundException("week", weekId));

        List<PeerEvaluation> evaluations = evaluationRepository.findByEvaluatee_IdAndWeek_Id(student.getId(), week.getId());

        List<Criterion> criteria = team.getSection().getRubric() == null
                ? List.of()
                : team.getSection().getRubric().getCriteria();

        List<MyEvaluationReportDto.CriterionAverageDto> criterionAverages = new ArrayList<>();
        double totalAverageScore = 0.0;
        double totalMaxScore = 0.0;

        for (Criterion criterion : criteria) {
            List<BigDecimal> scores = evaluations.stream()
                    .flatMap(e -> e.getRatings().stream())
                    .filter(r -> r.getCriterion().getId().equals(criterion.getId()))
                    .map(Rating::getScore)
                    .toList();

            double avg = scores.isEmpty() ? 0.0 : scores.stream()
                    .mapToDouble(BigDecimal::doubleValue)
                    .average()
                    .orElse(0.0);

            criterionAverages.add(new MyEvaluationReportDto.CriterionAverageDto(
                    criterion.getName(), criterion.getDescription(), avg, criterion.getMaxScore().doubleValue()
            ));

            totalAverageScore += avg;
            totalMaxScore += criterion.getMaxScore().doubleValue();
        }

        List<String> publicComments = evaluations.stream()
                .map(PeerEvaluation::getPublicComments)
                .filter(c -> c != null && !c.isBlank())
                .toList();

        String studentName = student.getFirstName() + " " + student.getLastName();
        String weekLabel = "Week " + week.getWeekNumber() + ": " + week.getStartDate() + " – " + week.getEndDate();

        return new MyEvaluationReportDto(weekLabel, studentName, evaluations.size(),
                criterionAverages, publicComments, totalAverageScore, totalMaxScore);
    }

    // UC-31: sections the instructor has access to (unique sections across their teams)
    @Transactional(readOnly = true)
    public List<SectionSummaryDto> getInstructorSections(String username) {
        Instructor instructor = instructorRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("instructor", username));

        Map<Integer, SectionSummaryDto> seen = new LinkedHashMap<>();
        for (Team team : instructor.getTeams()) {
            Section s = team.getSection();
            seen.putIfAbsent(s.getId(), new SectionSummaryDto(s.getId(), s.getName()));
        }
        return new ArrayList<>(seen.values());
    }

    // UC-31: completed weeks for a section (instructor must have access)
    @Transactional(readOnly = true)
    public List<SubmitFormDto.WeekInfo> getSectionReportWeeks(String username, Integer sectionId) {
        Instructor instructor = instructorRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("instructor", username));

        boolean hasAccess = instructor.getTeams().stream()
                .anyMatch(t -> t.getSection().getId().equals(sectionId));
        if (!hasAccess) {
            throw new AccessDeniedException("You do not have access to this section.");
        }

        LocalDate today = LocalDate.now();
        return weekRepository.findBySectionIdOrderByWeekNumber(sectionId).stream()
                .filter(w -> w.getEndDate().isBefore(today))
                .sorted((a, b) -> b.getEndDate().compareTo(a.getEndDate()))
                .map(w -> new SubmitFormDto.WeekInfo(w.getId(), w.getWeekNumber(), w.getStartDate(), w.getEndDate()))
                .toList();
    }

    // UC-31: generate peer evaluation report for all students in a section for a given week
    @Transactional(readOnly = true)
    public SectionEvaluationReportDto getSectionReport(String username, Integer sectionId, Integer weekId) {
        Instructor instructor = instructorRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("instructor", username));

        boolean hasAccess = instructor.getTeams().stream()
                .anyMatch(t -> t.getSection().getId().equals(sectionId));
        if (!hasAccess) {
            throw new AccessDeniedException("You do not have access to this section.");
        }

        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new ObjectNotFoundException("week", weekId));

        // All students across all teams in the section, sorted by last name then first name
        List<PeerEvaluationUser> allStudents = teamRepository.findBySectionId(sectionId).stream()
                .flatMap(t -> t.getStudents().stream())
                .collect(java.util.stream.Collectors.toMap(
                        PeerEvaluationUser::getId, s -> s, (a, b) -> a, LinkedHashMap::new))
                .values().stream()
                .sorted(Comparator.comparing(PeerEvaluationUser::getLastName)
                        .thenComparing(PeerEvaluationUser::getFirstName))
                .toList();

        // Max grade = sum of all criterion max scores
        Section section = teamRepository.findBySectionId(sectionId).stream()
                .findFirst()
                .map(Team::getSection)
                .orElse(null);
        List<Criterion> criteria = (section != null && section.getRubric() != null)
                ? section.getRubric().getCriteria()
                : List.of();
        double maxGrade = criteria.stream().mapToDouble(c -> c.getMaxScore().doubleValue()).sum();

        List<SectionEvaluationReportDto.StudentReportDto> studentReports = new ArrayList<>();
        for (PeerEvaluationUser student : allStudents) {
            List<PeerEvaluation> evals = evaluationRepository.findByEvaluatee_IdAndWeek_Id(student.getId(), weekId);

            // Algorithm: avg(sum of criterion scores per evaluator)
            double grade = 0.0;
            if (!evals.isEmpty()) {
                double totalSum = evals.stream()
                        .mapToDouble(e -> e.getRatings().stream()
                                .mapToDouble(r -> r.getScore().doubleValue()).sum())
                        .sum();
                grade = totalSum / evals.size();
            }

            List<SectionEvaluationReportDto.EvaluationDetailDto> details = evals.stream()
                    .map(eval -> {
                        String evaluatorName = eval.getEvaluator().getFirstName() + " " + eval.getEvaluator().getLastName();
                        List<SectionEvaluationReportDto.CriterionScoreDto> scores = eval.getRatings().stream()
                                .map(r -> new SectionEvaluationReportDto.CriterionScoreDto(
                                        r.getCriterion().getName(),
                                        r.getCriterion().getDescription(),
                                        r.getScore().doubleValue(),
                                        r.getCriterion().getMaxScore().doubleValue()
                                ))
                                .toList();
                        return new SectionEvaluationReportDto.EvaluationDetailDto(
                                evaluatorName,
                                eval.getPublicComments(),
                                eval.getPrivateComments(),
                                scores
                        );
                    })
                    .toList();

            String studentName = student.getLastName() + ", " + student.getFirstName();
            studentReports.add(new SectionEvaluationReportDto.StudentReportDto(
                    student.getId(), studentName, grade, maxGrade, details
            ));
        }

        // Non-submitters: students who submitted no evaluations for this week
        List<String> nonSubmitters = allStudents.stream()
                .filter(s -> evaluationRepository.findByEvaluator_IdAndWeek_Id(s.getId(), weekId).isEmpty())
                .map(s -> s.getFirstName() + " " + s.getLastName())
                .toList();

        String weekLabel = "Week " + week.getWeekNumber() + ": " + week.getStartDate() + " – " + week.getEndDate();
        return new SectionEvaluationReportDto(weekLabel, studentReports, nonSubmitters);
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
