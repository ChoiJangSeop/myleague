package jangseop.myleague.repository;

import jangseop.myleague.domain.HeadCoach;
import jangseop.myleague.domain.Team;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class HeadCoachRepository {

    @PersistenceContext
    EntityManager em;

    public Long save(HeadCoach headCoach) {
        em.persist(headCoach);
        return headCoach.getId();
    }

    public HeadCoach findOne(Long id) {
        return em.find(HeadCoach.class, id);
    }

    public HeadCoach findByName(String name) {
        return em.createQuery("select h from HeadCoach h where h.name = :name", HeadCoach.class)
                .setParameter("name", name)
                .getResultList()
                .get(0);
    }

    public HeadCoach findByTeam(Team team) {
        return em.createQuery("select h from HeadCoach h where h.team = :team", HeadCoach.class)
                .setParameter("team", team)
                .getResultList()
                .get(0);
    }

    public List<HeadCoach> findAll() {
        return em.createQuery("select h from HeadCoach  h", HeadCoach.class)
                .getResultList();
    }
}