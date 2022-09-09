package jangseop.myleague.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Player {

    @Id @GeneratedValue
    @Column(name = "PLAYER_ID")
    private Long id;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @Enumerated(value = EnumType.STRING)
    private Position position;

    private int stat;

    // == 생성 메서드 ==//

    public static Player createPlayer(String name, Position position, int stat) {
        Player player = new Player();
        player.name = name;
        player.position = position;
        player.stat = stat;
        player.team = null;
        return player;
    }


    //== 연관관계 편의 메서드 ==//


    //== 비즈니스 로직 ==//

    /**
     * 팀 등록
     */
    public void registerTeam(Team team) {
        if (this.team != null) {
            this.team.getPlayers().remove(this);
        }
        this.team = team;

        if (team != null) {
            team.getPlayers().add(this);
        }
    }

    /**
     * 팀 해제
     */
    public void deregisterTeam() {
        this.team.getPlayers().remove(this);
        this.team = null;
    }

    /**
     * 스탯 수정
     */
    public void setStat(int stat) { this.stat = stat; }

    /**
     * 포지션 수정
     */
    public void setPosition(Position position) { this.position = position; }


}
