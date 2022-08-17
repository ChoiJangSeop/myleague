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
public class Participant {

    @Id
    @GeneratedValue
    @Column(name = "PARTICIPANT_ID")
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "LEAGUE_ID")
    private League league;

    @OneToMany(mappedBy = "home", fetch = LAZY, cascade = ALL)
    private List<Match> homeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "away", fetch = LAZY, cascade = ALL)
    private List<Match> awayMatches = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "RECORD_ID")
    private Record record;


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

}
