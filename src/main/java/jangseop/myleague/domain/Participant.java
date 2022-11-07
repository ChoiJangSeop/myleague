package jangseop.myleague.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Participant implements Comparable<Participant> {

    @Id
    @GeneratedValue
    @Column(name = "PARTICIPANT_ID")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "LEAGUE_ID")
    private League league;

    @OneToMany(mappedBy = "home", fetch = LAZY, cascade = ALL)
    private List<Match> homeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "away", fetch = LAZY, cascade = ALL)
    private List<Match> awayMatches = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "RECORD_ID")
    private Record record;


    //== 정렬 연산을 위한 Comparable interface 구현 ==//

    @Override
    public int compareTo(Participant o) {
        return o.record.getScore() - this.record.getScore();
    }


    //== 생성 메서드 ==//

    public static  Participant createParticipant(Team team, League league) {
        Participant participant = new Participant();

        participant.team = team;
        team.getParticipants().add(participant);

        participant.league = league;
        league.getParticipants().add(participant);

        participant.record = new Record();


        return participant;
    }

    //== 비즈니스 로직 ==//

    /**
     * add match result
     */
    public void addMatchResult(int myScore, int otherScore) {
        record.addMatchResult(myScore, otherScore);
        record.updateScore();
        league.updateRanking();
    }

    /**
     * remove match result
     */
    public void removeMatchResult(int myScore, int otherScore) {
        record.removeMatchResult(myScore, otherScore);
        record.updateScore();
        league.updateRanking();
    }


}
