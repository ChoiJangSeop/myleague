package jangseop.myleague.dto;

import jangseop.myleague.domain.Playoff;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecordDto {

    private Long id;
    private int round;
    private Long participantId;
    private Playoff type;

    protected int win = 0;
    protected int draw = 0;
    protected int loss = 0;
    protected int setWin = 0;
    protected int setLoss = 0;
    protected int score = 0;
    protected int rank = 1;
    protected int promotion = 0;
}
