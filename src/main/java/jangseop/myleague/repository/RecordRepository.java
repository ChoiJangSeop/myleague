package jangseop.myleague.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jangseop.myleague.domain.QLeague;
import jangseop.myleague.domain.QParticipant;
import jangseop.myleague.domain.record.QRecord;
import jangseop.myleague.domain.record.Record;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RecordRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Record record) {
        em.persist(record);
        return record.getId();
    }

    public Record findOne(Long id) {
        return em.find(Record.class, id);
    }

    public void delete(Record record) {
        em.remove(record);
    }

    /**
     * search method
     */
    public List<Record> findAll(int round, Long leagueId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QRecord record = QRecord.record;
        QParticipant participant = QParticipant.participant;
        QLeague league = QLeague.league;

        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder onBuilder = new BooleanBuilder();

        builder.and(record.round.eq(round));
        onBuilder.and(record.participant.league.id.eq(leagueId));

        return queryFactory.selectFrom(record)
                .leftJoin(participant)
                .leftJoin(league)
                .on(onBuilder)
                .where(builder)
                .fetch();

    }

}
