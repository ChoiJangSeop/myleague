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


    //== Bussiness Logic ==//

    /**
     * create Match
     */
    public static Match createMatch(Date date, Participant home, Participant away) {
        Match match = new Match();
        match.matchDate = date;
        match.home = home;
        match.away = away;

        return match;
    }
}
