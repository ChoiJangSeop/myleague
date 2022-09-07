package jangseop.myleague.repository;

import jangseop.myleague.domain.League;
import jangseop.myleague.domain.Team;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class LeagueRepository {

    @PersistenceContext
    EntityManager em;

    public Long save(League league) {
        em.persist(league);
        return league.getId();
    }

    public League findOne(Long id) {
        return em.find(League.class, id);
    }





}
