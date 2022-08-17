package jangseop.myleague.repository;

import jangseop.myleague.domain.League;
import jangseop.myleague.domain.Match;
import jangseop.myleague.domain.Participant;
import jangseop.myleague.domain.Team;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    public List<Match> findByTeam(Team team) {
        return em.createQuery("select m from Match m where m.home = :team or m.away = :team", Match.class)
                .setParameter("team", team)
                .getResultList();
    }

}
