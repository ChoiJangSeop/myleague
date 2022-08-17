package jangseop.myleague.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class HeadCoach {

    @Id
    @GeneratedValue
    @Column(name = "HEAD_COACH_ID")
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;


    //== 생성 메서드 ==//
    public static HeadCoach createHeadCoach(String name) {
        HeadCoach headCoach = new HeadCoach();
        headCoach.name = name;
        headCoach.team = null;
        return headCoach;
    }

    //== 연관관계 편의 메서드 ==//

    public void setTeam(Team team) {
        this.team = team;
        team.setHeadCoach(this);
    }
}
