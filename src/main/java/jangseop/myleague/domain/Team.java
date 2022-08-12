package jangseop.myleague.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Player> players = new ArrayList<>();

    @OneToOne(mappedBy = "team")
    private HeadCoach headCoach;

    @OneToMany(mappedBy = "team")
    private List<Participant> Participants = new ArrayList<>();

    private int teamStat;
}
