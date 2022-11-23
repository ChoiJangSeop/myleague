package jangseop.myleague.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Record {

    @Id @GeneratedValue
    @Column(name = "RECORD_ID")
    private Long id;

    // FIXME participant field's setter must be private
    @OneToOne(mappedBy = "record")
    private Participant participant;

    private int win = 0;
    private int draw = 0;
    private int loss = 0;
    private int setWin = 0;
    private int setLoss = 0;
    private int score = 0;
    private int rank = 1;


    //== 비즈니스 메서드 ==//

    /**
     * 매치 결과를 전적에 업데이트
     */
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
    }

    /**
     * 경기 취소로 전적 초기화
     */
    public void removeMatchResult(int matchSetWin, int matchSetLoss) {
        this.setWin -= matchSetWin;
        this.setLoss -= matchSetLoss;

        if (matchSetWin > matchSetLoss) {
            this.win--;
        } else if (matchSetWin < matchSetLoss) {
            this.loss--;
        } else {
            this.draw--;
        }
    }

    /**
     * set rank
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * TODO 점수 계산
     */
    public void updateScore() {
        this.score = (win - loss) * 100 + setWin - setLoss;
    }
}
