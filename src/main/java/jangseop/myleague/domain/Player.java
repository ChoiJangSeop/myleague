package jangseop.myleague.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Player {

    @Id @GeneratedValue
    @Column(name = "PLAYER_ID")
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @Enumerated(value = EnumType.STRING)
    private Position position;

    private int stat;
}
