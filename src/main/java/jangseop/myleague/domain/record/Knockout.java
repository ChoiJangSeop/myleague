package jangseop.myleague.domain.record;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("T")
@Getter
public class Tournament extends Record {

    private int currentRound;
    private TournamentStatus status;


    @Override
    public void addMatchResult(int matchSetWin, int matchSetLoss) {
        this.setWin += matchSetWin;
        this.setLoss += matchSetLoss;

        if (matchSetWin > matchSetLoss) {
            this.win++;
            this.currentRound++;
        } else if (matchSetWin < matchSetLoss) {
            this.loss++;
            this.status = TournamentStatus.OUT;
        } else {
            this.draw++;
        }
    }

    @Override
    public void updateScore() {

    }
}
