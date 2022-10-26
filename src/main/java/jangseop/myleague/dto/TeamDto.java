package jangseop.myleague.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDto {

    private Long id;
    private String name;

    private String shortName;
    private int stat;

    public TeamDto(String name, String shortName, int stat) {
        this.name = name;
        this.shortName = shortName;
        this.stat = stat;
    }
}
