package jangseop.myleague.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Participant {

    @Id
    @GeneratedValue
    @Column(name = "PARTICIPANT_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "LEAGUE_ID")
    private League league;

    @OneToMany(mappedBy = "home")
    private List<Match> homeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "away")
    private List<Match> awayMatches = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "RECORD_ID")
    private Record record;


}
