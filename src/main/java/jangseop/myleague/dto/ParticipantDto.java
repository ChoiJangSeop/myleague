package jangseop.myleague.dto;

import jangseop.myleague.domain.Record;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ParticipantDto {

    private Long teamId;
    private Long leagueId;

    private int win;
    private int draw;
    private int loss;
    private int setWin;
    private int setLoss;
    private int score;
    private int rank;

    public ParticipantDto(Long teamId, Long leagueId, Record record) {
        this.teamId = teamId;
        this.leagueId = leagueId;

        this.win = record.getWin();
        this.loss = record.getLoss();
        this.draw = record.getDraw();
        this.setWin = record.getSetWin();
        this.setLoss = record.getSetLoss();
        this.score = record.getScore();
        this.rank = record.getRank();
    }


}
