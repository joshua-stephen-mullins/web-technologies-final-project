package team.projectpulse.evaluation;

// Owner: Whitey (Person 3)
// One student's evaluation of one teammate for one week
// Related UCs: UC-28 (submit), UC-29 (student view), UC-31 (section report), UC-33 (student report)
public class PeerEvaluation {
    // TODO: Implement JPA entity
    // Fields: id, evaluator (ManyToOne -> User), evaluatee (ManyToOne -> User),
    //         week (ManyToOne), publicComments, privateComments, submittedAt
    // Relationships: ratings (OneToMany -> Rating)
    // Unique constraint: (evaluatorId, evaluateeId, weekId)
}
