package jangseop.myleague.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.mapping.Collection;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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

    //== 생성 메서드 ==//
    public static League createLeague(
            String title, Date startedDate, Date endDate, int roundrobins, int promotion, Playoff playoff
    ) {
        League league = new League();

        league.title = title;
        league.startedDate = startedDate;
        league.endDate = endDate;
        league.method = Method.createMethod(roundrobins, promotion, playoff);

        return league;
    }

    //== 비즈니스 로직 ==//

    /**
     * update rank
     */
    public void updateRanking() {
        Collections.sort(participants);
        int length = participants.size();

        IntStream.range(0, length).forEach(index -> {
            participants.get(index)
                    .getRecord()
                    .setRank(index+1);
        });
    }

    /**
     * initiate matches
     */
    public void createMatches() {
        int numTeam = participants.size();
        int[] idx = IntStream.range(0, numTeam).toArray();

        if (numTeam % 2 == 1) {

            for (int rotation=0; rotation<method.getRoundrobins(); ++rotation) {
                for (int round=1; round<=numTeam; ++round) {

                    for (int i = 0; i < numTeam / 2; ++i) {
                        Participant homeTeam = participants.get(idx[i]);
                        Participant awayTeam = participants.get(idx[numTeam - 1 - i]);

                        Match match = Match.createMatch(null, rotation*numTeam+round, homeTeam, awayTeam);

                        homeTeam.getHomeMatches().add(match);
                        awayTeam.getAwayMatches().add(match);

                    }

                    for (int i = 0; i < numTeam; ++i) {
                        idx[i] = (idx[i] + 1) % numTeam;
                    }
                }
            }


        } else {
            for (int rotation=0; rotation<method.getRoundrobins(); ++rotation) {
                for (int round=1; round<=numTeam-1; ++round) {

                    for (int i = 0; i < numTeam / 2; ++i) {
                        Participant homeTeam = participants.get(idx[i]);
                        Participant awayTeam = participants.get(idx[numTeam - 1 - i]);

                        Match match = Match.createMatch(null, rotation*numTeam+round, homeTeam, awayTeam);

                        homeTeam.getHomeMatches().add(match);
                        awayTeam.getAwayMatches().add(match);

                    }

                    for (int i = 0; i < numTeam-1; ++i) {
                        idx[i] = (idx[i] + 1) % (numTeam - 1);
                    }
                }
            }
        }
    }

}
