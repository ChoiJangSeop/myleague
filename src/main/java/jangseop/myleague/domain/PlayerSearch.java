package jangseop.myleague.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSearch {

    private String name;
    private Team team;
    private Position position;
}
