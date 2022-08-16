package jangseop.myleague.repository;

import jangseop.myleague.domain.Player;
import jangseop.myleague.domain.Position;
import jangseop.myleague.domain.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PlayerRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Player player) {
        em.persist(player);
        return player.getId();
    }

    public Player findOne(Long id) {
        return em.find(Player.class, id);
    }

    public Player findByName(String name) {
        List<Player> findPlayer = em.createQuery("select p from Player p where p.name = :name", Player.class)
                .setParameter("name", name)
                .getResultList();

        if (findPlayer.size() == 0) return null;
        return findPlayer.get(0);
    }

    public List<Player> findByPosition(Position position) {
        return em.createQuery("select p from Player p where p.position = :position", Player.class)
                .setParameter("position", position)
                .getResultList();
    }

    public List<Player> findByTeam(Team team) {
        return em.createQuery("select p from Player p where p.team = :team", Player.class)
                .setParameter("team", team)
                .getResultList();
    }

    public List<Player> findAll() {
        return em.createQuery("select p from Player p", Player.class)
                .getResultList();
    }
}
