package jangseop.myleague.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jangseop.myleague.domain.*;
import jangseop.myleague.domain.record.QRecord;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class MatchRepository {

    @PersistenceContext
    EntityManager em;

    public Long save(Match match) {
        em.persist(match);
        return match.getId();
    }

    public Match findOne(Long id) {
        return em.find(Match.class, id);
    }

    public List<Match> findAll(MatchSearch matchSearch) {

        List<Participant> participantsByTeam = null;
        List<Participant> participantsByLeague = null;

        if (matchSearch.getTeam() != null) {
            participantsByTeam = matchSearch.getTeam().getParticipants();
        }

        if (matchSearch.getLeague() != null) {
            participantsByLeague = matchSearch.getLeague().getParticipants();
        }

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QMatch match = QMatch.match;

        QRecord home = new QRecord("home");
        QRecord away = new QRecord("away");

        QParticipant homeParticipant = new QParticipant("homeParticipant");

        BooleanBuilder whereBuilder = new BooleanBuilder();

        if (participantsByTeam != null) {
            whereBuilder.and((match.home.participant.in(participantsByTeam))
                            .or(match.away.participant.in(participantsByTeam)));
        }

        if (participantsByLeague != null) {
            whereBuilder.and(match.home.participant.in(participantsByLeague));
        }


        List<Match> matches = queryFactory.selectFrom(match)
                .leftJoin(match.away, away)
                .leftJoin(match.home, home)
                .where(whereBuilder)
                .fetch();

        Collections.sort(matches);

        return matches;
    }

    public List<Match> findAll() {
        return em.createQuery("select m from Match m", Match.class)
                .getResultList();
    }

    public List<Match> findByTeam(Team team) {
        return em.createQuery("select m from Match m where m.home = :team or m.away = :team", Match.class)
                .setParameter("team", team)
                .getResultList();
    }

    public void delete(Match match) {
        em.remove(match);
    }

}

class MatchComparator implements Comparator<Match> {

    @Override
    public int compare(Match m1, Match m2) {
        return m1.compareTo(m2);
    }
}
