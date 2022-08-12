package jangseop.myleague.domain;

import javax.persistence.*;

@Entity
public class HeadCoach {

    @Id
    @GeneratedValue
    @Column(name = "HEAD_COACH_ID")
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}
