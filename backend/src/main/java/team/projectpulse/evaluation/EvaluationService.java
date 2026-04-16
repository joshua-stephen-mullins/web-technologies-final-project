package team.projectpulse.evaluation;

import org.springframework.stereotype.Service;

// Owner: Whitney (Person 3)
@Service
public class EvaluationService {
    // TODO: Implement
    // - submitEvaluation(request): PeerEvaluation                                  UC-28
    // - getMyEvaluationReport(studentId, weekId): PeerEvaluationAverage            UC-29
    // - generateSectionEvalReport(sectionId, weekId): WeeklyPeerEvaluationReport   UC-31
    // - generateStudentEvalReport(studentId, startWeek, endWeek): List<PeerEvaluationAverage> UC-33
    //
    // Grade algorithm (UC-31):
    //   For each evaluatee: collect all evaluations received that week,
    //   sum criterion scores per evaluator = totalScore,
    //   average totalScores across all evaluators = final grade
}
