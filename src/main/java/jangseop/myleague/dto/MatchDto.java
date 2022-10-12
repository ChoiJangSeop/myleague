package jangseop.myleague.dto;

import jangseop.myleague.domain.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MatchDto {

    private int round;
    private Date matchDate;

    private Long homeId;
    private Long awayId;

    private int homeScore;
    private int awayScore;

    public MatchDto(Match match) {
        this.round = match.getRound();
        this.matchDate = match.getMatchDate();
        this.homeId = match.getHome().getId();
        this.awayId = match.getAway().getId();
        this.homeScore = match.getHomeScore();
        this.awayScore = match.getAwayScore();
    }

    public MatchDto(int round, Date matchDate, Long homeId, Long awayId) {
        this.round = round;
        this.matchDate = matchDate;
        this.homeId = homeId;
        this.awayId = awayId;
        this.homeScore = -1;
        this.awayScore = -1;
    }
}
