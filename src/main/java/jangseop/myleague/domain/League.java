package jangseop.myleague.domain;

import jangseop.myleague.domain.record.Record;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.mapping.Collection;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity
@Getter @Setter
public class League {

    @Id
    @GeneratedValue
    @Column(name = "LEAGUE_ID")
    private Long id;

    private String title;

    private Date startedDate;

    private Date endDate;

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();

    @Embedded
    private Method method;

    @Enumerated(value = EnumType.STRING)
    private LeagueStatus leagueStatus;

    //== 생성 메서드 ==//
    public static League createLeague(
            String title, Date startedDate, Date endDate, int roundrobins, int promotion, Playoff playoff
    ) {
        League league = new League();

        league.title = title;
        league.startedDate = startedDate;
        league.endDate = endDate;
        league.leagueStatus = LeagueStatus.PROCEEDING;
        league.method = Method.createMethod(roundrobins, promotion, playoff);

        return league;
    }

    /**
     * setting league
     */
    public void setAll(String title, Date startedDate, Date endDate, LeagueStatus status, Method method) {
        this.title = title;
        this.startedDate = startedDate;
        this.endDate = endDate;
        this.leagueStatus = status;
        this.method = method;
    }

    //== 비즈니스 로직 ==//

    /**
     * update rank
     */
    public void updateRanking() {
        Comparator<Participant> comparator = new Comparator<>() {
            @Override
            public int compare(Participant p1, Participant p2) {
                p1.getRecords().sort(Comparator.comparing(Record::getRound).reversed());
                p2.getRecords().sort(Comparator.comparing(Record::getRound).reversed());

                if (p1.getRecords().get(0).getRound() > p2.getRecords().get(0).getRound()) return -1;
                else if (p1.getRecords().get(0).getRound() < p2.getRecords().get(0).getRound()) return 1;
                else {
                    int idx = 0;
                    while (idx < p1.getRecords().size()) {
                        if (p1.getRecords().get(idx).getRank() < p2.getRecords().get(idx).getRank()) return -1;
                        else if (p1.getRecords().get(idx).getRank() > p2.getRecords().get(idx).getRank()) return 1;
                        idx++;
                    }
                    return 0;
                }
            }
        };

        Collections.sort(this.participants, comparator);

        int currRank = 1;
        int tieNum = 1;

        this.participants.get(0).setTotalRank(currRank);

        for (int idx=1; idx<this.participants.size(); ++idx) {
            if (comparator.compare(this.participants.get(idx), this.participants.get(idx-1)) == 0) {
                this.participants.get(idx).setTotalRank(currRank);
                tieNum++;
            } else {
                currRank += tieNum; tieNum = 1;
                this.participants.get(idx).setTotalRank(currRank);
            }
        }
    }



    /**
     * update rank of each round
     */
    public void updateRecordRank(int round) {

        List<Record> records = this.participants.stream()
                .map(participant -> (
                    participant.getRecords().stream()
                            .filter(record -> record.getRound() == round)
                            .collect(Collectors.toList()).get(0)
                )).collect(Collectors.toList());

        // sorting by score
        records.sort(Comparator.comparing(Record::getScore).reversed());

        // insert ranking
        int currRank = 1;
        int numTie = 1;

        records.get(0).setRank(currRank);

        for (int i=1; i<records.size(); ++i) {
            if (records.get(i-1).getScore() == records.get(i).getScore()) {
                records.get(i).setScore(currRank);
                numTie++;
            } else {
                currRank += numTie;
                records.get(i).setRank(currRank);
                numTie = 1;
            }
        }
    }
}
