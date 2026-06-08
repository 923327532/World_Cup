package worldcup_service.dto;

import java.time.LocalDate;

public record TournamentDTO(
    Long id,
    String name,
    Integer year,
    LocalDate startDate,
    LocalDate endDate
) {}
