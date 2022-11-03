package jangseop.myleague.dto;

import jangseop.myleague.domain.Playoff;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeagueDto {
    private String title;
    private Date startedDate;
    private Date endDate;
    private int roundRobins;
    private int promotions;
    private Playoff playoff;
}
