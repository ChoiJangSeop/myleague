package jangseop.myleague.dto;

import jangseop.myleague.domain.Position;
import jangseop.myleague.domain.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlayerDto {

    private Long id;

    private String name;
    private Position position;
    private int stat;
    private Long teamId = -1L;

    public PlayerDto(Long id, String name, Position position, int stat, Team team) {

        this.id = id;
        this.name = name;
        this.position = position;
        this.stat = stat;
        // TODO null checking
        if (team != null) this.teamId = team.getId();
    }

    public PlayerDto(String name, Position position, int stat) {

        this.name = name;
        this.position = position;
        this.stat = stat;
    }


    public PlayerDto(Long id, String name, Position position, int stat) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.stat = stat;
    }
}
