package jangseop.myleague.domain;

import lombok.Getter;

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

    private String shortName;

    @OneToOne(mappedBy = "team", fetch = LAZY, cascade = CascadeType.ALL)
    private HeadCoach headCoach;

    @OneToMany(mappedBy = "team", fetch = LAZY)
    private List<Player> players = new ArrayList<>();


    @OneToMany(mappedBy = "team", fetch = LAZY)
    private List<Participant> participants = new ArrayList<>();

    private int teamStat;

    //== 생성 메서드 ==//

    public static Team createTeam(String name, String shortName, int teamStat) {
        Team team = new Team();
        team.name = name;
        team.shortName = shortName;
        team.teamStat = teamStat;

        return team;
    }

    // for test
    public static Team createTeam(String name,int teamStat) {
        Team team = new Team();
        team.name = name;
        team.teamStat = teamStat;

        return team;
    }


    public void setHeadCoach(HeadCoach headCoach) {
        this.headCoach = headCoach;
    }

    //== 비즈니스 로직 ==//

    /**
     * edit stat
     */
    public void setTeamStat(int teamStat) {
        this.teamStat = teamStat;
    }
    public void setName(String name) { this.name = name; }
}
