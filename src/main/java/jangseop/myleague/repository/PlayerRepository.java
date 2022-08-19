package jangseop.myleague.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jangseop.myleague.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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

    public List<Player> findAll(PlayerSearch playerSearch) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QPlayer player = QPlayer.player;
        QTeam team = QTeam.team;

        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(playerSearch.getName())) {
            builder.and(player.name.contains(playerSearch.getName()));
        }

        if (playerSearch.getTeam() != null) {
            builder.and(player.team.id.eq(playerSearch.getTeam().getId()));
        }

        if (playerSearch.getPosition() != null) {
            builder.and(player.position.eq(playerSearch.getPosition()));
        }

        return queryFactory.selectFrom(player)
                .leftJoin(team)
                .on(builder)
                .fetch();
    }
}
