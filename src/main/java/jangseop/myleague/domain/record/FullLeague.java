package jangseop.myleague.domain.record;


import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("L")
@Getter
public class FullLeague extends Record {

    private int winPt = 100;
    private int drawPt = 0;
    private int lossPt = -1;
    private int roundRobin;

    // TODO 순위 산정 방식 커스터마이징 어떻게???
    @Override
    public void updateScore() {
        this.score = this.win * this.winPt + this.draw * this.drawPt + this.loss * this.lossPt + this.setWin - this.setLoss;
    }


    @Override
    public void addMatchResult(int matchSetWin, int matchSetLoss) {
        this.setWin += matchSetWin;
        this.setLoss += matchSetLoss;

        if (matchSetWin > matchSetLoss) {
            this.win++;
        } else if (matchSetWin < matchSetLoss) {
            this.loss++;
        } else {
            this.draw++;
        }
        this.updateScore();
        this.participant.updateRanking(this.round);
    }

    @Override
    public void removeMatchResult(int matchSetWin, int matchSetLoss)  {
        this.setWin -= matchSetWin;
        this.setLoss -= matchSetLoss;

        if (matchSetWin > matchSetLoss) {
            this.win--;
        } else if (matchSetWin < matchSetLoss) {
            this.loss--;
        } else {
            this.draw--;
        }

        this.updateScore();
        this.participant.updateRanking(this.round);
    }
}
