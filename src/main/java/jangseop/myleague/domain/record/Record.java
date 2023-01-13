package jangseop.myleague.domain.record;

import jangseop.myleague.domain.Match;
import jangseop.myleague.domain.Participant;
import jangseop.myleague.domain.Playoff;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static jangseop.myleague.domain.Playoff.*;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Record {

    @Id @GeneratedValue
    @Column(name = "RECORD_ID")
    private Long id;

    protected int round;
    protected Playoff type;

    // FIXME participant field's setter must be private
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "PARTICIPANT_ID")
    protected Participant participant;

    @OneToMany(mappedBy = "home", fetch = LAZY, cascade = ALL)
    private List<Match> homeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "away", fetch = LAZY, cascade = ALL)
    private List<Match> awayMatches = new ArrayList<>();

    protected int win = 0;
    protected int draw = 0;
    protected int loss = 0;
    protected int setWin = 0;
    protected int setLoss = 0;
    protected int score = 0;
    protected int rank = 1;

    protected int promotion = 0;


    /**
     * set rank
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * 생성 메서드
     */
    public static Record createRecord(int round, Playoff type) {
        Record record = null;
        if (type == FULL_LEAGUE) record = new FullLeague();
        else if (type == KNOCKOUT) record = new Knockout();
        // TODO 테스트용 나중에 지우기
        else record = new Knockout();

        // null checking
        if (record == null) return null;

        record.type = type;
        record.round = round;
        return record;
    }
    //== 비즈니스 메서드 ==//

    /**
     * 경기 취소로 전적 초기화
     */
    abstract public void removeMatchResult(int matchSetWin, int matchSetLoss);

    /**
     * 매치 결과를 전적에 업데이트
     */
    abstract public void addMatchResult(int matchSetWin, int matchSetLoss);

    /**
     * TODO 점수 계산
     */
    abstract public void updateScore();

}
