package team.projectpulse.evaluation;

import org.springframework.stereotype.Service;

// Owner: Whitney (Person 3)
// Enforces UC-29 BR-5: students can only see their own public comments + grade, not private comments or evaluators
@Service
public class EvaluationSecurityService {
    // TODO: Implement
    // - canViewFullReport(username, evaluateeId): boolean  (instructors yes, student only own)
}
