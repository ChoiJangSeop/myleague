package jangseop.myleague.repository;

import jangseop.myleague.domain.Team;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class TeamRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Team team) {
        em.persist(team);
        return team.getId();
    }

    public Team findOne(Long id) {
        return em.find(Team.class, id);
    }

    public List<Team> findByName(String name) {
        return em.createQuery("select t from Team t where t.name = :name"
                , Team.class)
                .setParameter("name", name)
                .getResultList();
    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class)
                .getResultList();
    }
}
