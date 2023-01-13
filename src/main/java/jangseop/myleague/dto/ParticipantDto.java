package jangseop.myleague.dto;

import jangseop.myleague.domain.record.Record;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ParticipantDto {

    private Long id;

    private Long teamId;
    private Long leagueId;

    private int totalRank;

    public ParticipantDto(Long teamId, Long leagueId, int totalRank) {
        this.id = id;
        this.teamId = teamId;
        this.leagueId = leagueId;
        this.totalRank = totalRank;
    }


}
