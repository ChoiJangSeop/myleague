package jangseop.myleague.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team", fetch = LAZY, cascade = CascadeType.ALL)
    private List<Player> players = new ArrayList<>();

    @OneToOne(mappedBy = "team", fetch = LAZY, cascade = CascadeType.ALL)
    private HeadCoach headCoach;

    @OneToMany(mappedBy = "team", fetch = LAZY)
    private List<Participant> Participants = new ArrayList<>();

    private int teamStat;

    //== 생성 메서드 ==//

    public static Team createTeam(String name, int teamStat) {
        Team team = new Team();
        team.name = name;
        team.teamStat = teamStat;

        return team;
    }

    public void setHeadCoach(HeadCoach headCoach) {
        this.headCoach = headCoach;
    }

    //== 비즈니스 로직 ==//
    public void setTeamStat(int teamStat) {
        this.teamStat = teamStat;
    }

    //== 연관관계 편의 메서드 ==//

    /**
     * Player Registration
     */
    public void addPlayer(Player player) {
        this.players.add(player);
        player.setTeam(this);
    }

    /**
     * Player Deregistration
     */
    public void removePlayer(Player player) {
        this.players.remove(player);
        player.setTeam(null);
    }



}
