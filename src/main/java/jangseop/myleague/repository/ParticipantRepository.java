package jangseop.myleague.repository;

import jangseop.myleague.domain.League;
import jangseop.myleague.domain.Participant;
import jangseop.myleague.domain.Team;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ParticipantRepository {

    @PersistenceContext
    EntityManager em;

    public Long save(Participant participant) {
        em.persist(participant);
        return participant.getId();
    }

    public Participant findOne(Long id) {
        return em.find(Participant.class, id);
    }

    public List<Participant> findByTeam(Team team) {
        return em.createQuery("select p from Participant p where p.team = :team", Participant.class)
                .setParameter("team", team)
                .getResultList();
    }

    public List<Participant> findByLeague(League league) {
        return em.createQuery("select p from Participant p where p.league = :league", Participant.class)
                .setParameter("league", league)
                .getResultList();
    }
}
