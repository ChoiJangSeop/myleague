package jangseop.myleague.domain;

import jangseop.myleague.domain.record.FullLeague;
import jangseop.myleague.domain.record.Record;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.mapping.Collection;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static jangseop.myleague.domain.Playoff.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Participant {

    @Id
    @GeneratedValue
    @Column(name = "PARTICIPANT_ID")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "LEAGUE_ID")
    private League league;


    @OneToMany(mappedBy = "participant", fetch = LAZY, cascade = ALL)
    private List<Record> records = new ArrayList<>();

    private int totalRank;

    //== 생성 메서드 ==//

    public static  Participant createParticipant(Team team, League league) {
        Participant participant = new Participant();

        participant.team = team;
        team.getParticipants().add(participant);

        participant.league = league;
        league.getParticipants().add(participant);


        return participant;
    }

    //== 비즈니스 로직 ==//

    /**
     * 전체 순위 갱신
     */
    public void updateRanking(int round) {
        this.league.updateRecordRank(round);
        this.league.updateRanking();
    }

    /**
     * 라운드별 모든 경기 출력
     */
    public List<Match> getRoundMatches(int round) {
        Record findRecord = this.records.stream()
                .filter(record -> record.getRound() == round)
                .collect(Collectors.toList())
                .get(0);
        return findRecord.getAllMatches();
    }


    //== 연관관계 편의 메서드 ==//
    public void addRecord(Record record) {
        if (record.getParticipant() != null) {
            record.getParticipant().removeRecord(record);
        }

        this.records.add(record);
        record.setParticipant(this);
    }

    public void removeRecord(Record record) {
        this.records.remove(record);
        record.setParticipant(null);
    }


}
