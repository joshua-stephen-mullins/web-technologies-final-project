package team.projectpulse.student.dto;

import java.util.List;

// Owner: Oscar (Person 2)
public record StudentInviteRequest(List<String> emails, Integer sectionId) {}
