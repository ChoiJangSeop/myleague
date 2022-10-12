package jangseop.myleague.service;

import jangseop.myleague.domain.League;
import jangseop.myleague.domain.Participant;
import jangseop.myleague.domain.Team;
import jangseop.myleague.repository.ParticipantRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ParticipantServiceTest {

    @PersistenceContext EntityManager em;

    @Autowired private ParticipantService participantService;
    @Autowired private ParticipantRepository participantRepository;

    @Test
    public void 참가자생성() throws Exception {
        // given
        Team GEN = Team.createTeam("gen.g", 20);
        League LCK = new League();

        em.persist(GEN);
        em.persist(LCK);

        Participant gen_lck = participantService.create(GEN.getId(), LCK.getId());
        participantRepository.save(gen_lck);

        // when
        Participant findParticipant = participantRepository.findOne(gen_lck.getId());

        // then
        assertThat(findParticipant).isEqualTo(gen_lck);
    }

    @Test
    public void 참가자생성검증_동일팀참가() throws Exception {
        // given
        Team GEN = Team.createTeam("gen.g", 20);
        League LCK = new League();
        League Worlds = new League();
        em.persist(GEN);
        em.persist(LCK);
        em.persist(Worlds);

        // when
        Participant gen_lck = participantService.create(GEN.getId(), LCK.getId());

        // then

        // 다른 리그 참가
        participantService.create(GEN.getId(), Worlds.getId());

        // 동일 리그 참가시 예외
        assertThrows(IllegalStateException.class, () -> {
            participantService.create(GEN.getId(), LCK.getId());
        });
    }

    @Test
    public void 참가자검색() throws Exception {
        // given
        Team GEN = Team.createTeam("gen.g", 20);
        Team C9 = Team.createTeam("cloud9", 12);
        League LCK = new League();
        League Worlds = new League();

        em.persist(GEN);
        em.persist(C9);
        em.persist(LCK);
        em.persist(Worlds);

        Participant gen_lck = participantService.create(GEN.getId(), LCK.getId());
        Participant gen_worlds = participantService.create(GEN.getId(), Worlds.getId());
        Participant c9_worlds = participantService.create(C9.getId(), Worlds.getId());

        // when
        Participant findParticipant = participantService
                .searchParticipants(GEN.getId(), Worlds.getId())
                .get(0);

        List<Participant> findParticipantsByTeam = participantService
                .searchParticipants(GEN.getId(), null);

        List<Participant> findParticipantsByLeague = participantService
                .searchParticipants(null, Worlds.getId());

        // then
        assertThat(findParticipant).isEqualTo(gen_worlds);

        assertThat(findParticipantsByTeam.size()).isEqualTo(2);
        assertThat(findParticipantsByTeam).contains(gen_lck, gen_worlds);

        assertThat(findParticipantsByLeague.size()).isEqualTo(2);
        assertThat(findParticipantsByLeague).contains(gen_worlds, c9_worlds);
    }

}