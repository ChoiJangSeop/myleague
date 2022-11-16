package jangseop.myleague.domain;

import lombok.Getter;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;

@Entity
@Getter
public class Match implements Comparable<Match> {

    @Id
    @GeneratedValue
    @Column(name = "MATCH_ID")
    private Long id;

    private int round;
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
    public static Match createMatch(Date date, int round, Participant home, Participant away) {
        Match match = new Match();

        match.matchDate = date;
        match.round = round;

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
            this.home.removeMatchResult(this.homeScore, this.awayScore);
            this.away.removeMatchResult(this.awayScore, this.homeScore);
        }

        this.homeScore = homeScore;
        this.awayScore = awayScore;

        this.home.addMatchResult(homeScore, awayScore);
        this.away.addMatchResult(awayScore, homeScore);
    }

    /**
     * cancel match
     */
    public void cancelMatchTeams() {
        if (this.homeScore != -1 && this.awayScore != -1) {
            this.home.removeMatchResult(this.homeScore, this.awayScore);
            this.away.removeMatchResult(this.awayScore, this.homeScore);
        }

        this.homeScore = -1;
        this.awayScore = -1;
    }

    @Override
    public int compareTo(Match m) {

        // null handling
        if (m.getMatchDate() == null) return 1;
        if (this.matchDate == null) return -1;

        if (m.getMatchDate().getTime() < this.matchDate.getTime()) return 1;
        else if (m.getMatchDate().getTime() > this.matchDate.getTime()) return -1;
        else return 0;
    }
}
