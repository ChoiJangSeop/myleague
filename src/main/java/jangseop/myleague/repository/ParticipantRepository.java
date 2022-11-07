package jangseop.myleague.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jangseop.myleague.domain.*;
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

    public List<Participant> findAll(ParticipantSearch participantSearch) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QParticipant participant = QParticipant.participant;
//        QTeam team = QTeam.team;
//        QLeague league = QLeague.league;

        BooleanBuilder builder = new BooleanBuilder();

        if (participantSearch.getTeam() != null) {
            builder.and(participant.team.eq(participantSearch.getTeam()));
        }

        if (participantSearch.getLeague() != null) {
            builder.and(participant.league.eq(participantSearch.getLeague()));
        }

        List<Participant> participants = queryFactory.selectFrom(participant)
                .where(builder)
                .fetch();
        return participants;
    }

    public List<Participant> findAll() {
        return em.createQuery("select p from Participant p", Participant.class)
                .getResultList();
    }

    public Long delete(Participant participant) {
        Long id = participant.getId();
        em.remove(participant);
        return id;
    }
}
