package jangseop.myleague.domain;

import lombok.Getter;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
public class Match {

    @Id
    @GeneratedValue
    @Column(name = "MATCH_ID")
    private Long id;

    private Date matchDate;

    @ManyToOne
    @JoinColumn(name = "HOME_PARTICIPANT_ID")
    private Participant home;

    @ManyToOne
    @JoinColumn(name = "AWAY_PARTICIPANT_ID")
    private Participant away;

    private int homeScore = -1;
    private int awayScore = -1;

    //== 생성 메서드 ==//

    /**
     * create Match
     */
    public static Match createMatch(Date date, Participant home, Participant away) {
        Match match = new Match();
        match.matchDate = date;

        match.home = home;
        match.away = away;
        home.getHomeMatches().add(match);
        away.getAwayMatches().add(match);

        return match;
    }

    //== 비지니스 로직 ==//

    /**
     * match
     */
    public void matchTeams(int homeScore, int awayScore) {

        if (this.homeScore != -1 && this.awayScore != -1) {
            this.home.getRecord().removeMatchResult(this.homeScore, this.awayScore);
            this.away.getRecord().removeMatchResult(this.awayScore, this.homeScore);
        }

        this.homeScore = homeScore;
        this.awayScore = awayScore;

        this.home.getRecord().addMatchResult(homeScore, awayScore);
        this.away.getRecord().addMatchResult(awayScore, homeScore);
    }
}
