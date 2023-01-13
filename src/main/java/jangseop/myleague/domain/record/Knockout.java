package jangseop.myleague.domain.record;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("T")
@Getter
public class Knockout extends Record {

    private int currentRound;

    private KnockoutStatus status;

    @ElementCollection
    private List<Integer> winNextRound = new ArrayList<>();

    @ElementCollection
    private List<Integer> lossNextRound = new ArrayList<>();

    @ElementCollection
    private List<Integer> prevRoundLog = new ArrayList<>();

    @Override
    public void addMatchResult(int matchSetWin, int matchSetLoss) {
        this.setWin += matchSetWin;
        this.setLoss += matchSetLoss;

        if (matchSetWin > matchSetLoss) {
            this.win++;
            this.currentRound = this.winNextRound.get(this.currentRound);
        } else if (matchSetWin < matchSetLoss) {
            this.loss++;
            if (this.lossNextRound.get(this.currentRound) == -1) {
                this.status = KnockoutStatus.OUT;
            } else {
                this.currentRound = this.lossNextRound.get(this.currentRound);
            }
        } else {
            this.draw++;
            // TODO draw knockout strategy???
        }
    }

    @Override
    public void removeMatchResult(int matchSetWin, int matchSetLoss) {

    }

    @Override
    public void updateScore() {
        this.score = this.currentRound;
    }
}
