package jangseop.myleague.domain;

import lombok.Getter;
import lombok.Setter;

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

    //== 비지니스 메서드 ==//

    /**
     * 생성 메서드
     */
    public static Player createPlayer(String name, Position position, int stat) {
        Player player = new Player();
        player.name = name;
        player.position = position;
        player.stat = stat;
        player.team = null;
        return player;
    }


    public void setTeam(Team team) {
        if (this.team != null) {
            this.team.getPlayers().remove(this);
        }
        this.team = team;
        team.getPlayers().add(this);
    }

    public void setStat(int stat) { this.stat = stat; }

    public void setPosition(Position position) { this.position = position; }


}
