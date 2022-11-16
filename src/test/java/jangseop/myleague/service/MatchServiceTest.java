package jangseop.myleague.service;

import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import jangseop.myleague.domain.*;
import jangseop.myleague.repository.MatchRepository;
import jangseop.myleague.repository.TeamRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MatchServiceTest {

    @PersistenceContext EntityManager em;

    @Autowired private MatchService matchService;
    @Autowired private ParticipantService participantService;

    @Autowired private TeamRepository teamRepository;
    @Autowired private MatchRepository matchRepository;

    @Test
    public void 경기생성() throws Exception {
        // given
        League LCK = new League();
        em.persist(LCK);

        Team DK = Team.createTeam("DWG KIA", 15);
        Team AF = Team.createTeam("Afreeca Freecs", 16);

        em.persist(DK);
        em.persist(AF);


        HeadCoach oktop = HeadCoach.createHeadCoach("oktop");
        oktop.setTeam(AF);

        HeadCoach daney = HeadCoach.createHeadCoach("Daney");
        daney.setTeam(DK);


        Player kiin = Player.createPlayer("kiin", Position.TOP, 15);
        Player nuguri = Player.createPlayer("nuguri", Position.TOP, 16);

        kiin.registerTeam(AF);
        daney.setTeam(DK);

        teamRepository.save(DK);
        teamRepository.save(AF);

        Participant lck_af = participantService.create(AF.getId(), LCK.getId());
        Participant lck_dk = participantService.create(DK.getId(), LCK.getId());


        // when
        Match match = matchService.create(null, lck_af.getId(), lck_dk.getId());
        Match findMatch = matchRepository.findOne(match.getId());

        // then
        assertThat(match).isEqualTo(findMatch);
    }

    @Test
    public void 경기생성검증_동일팀() throws Exception {
        // given
        League LCK = new League();
        League LPL = new League();

        em.persist(LCK);
        em.persist(LPL);

        Team AF = Team.createTeam("Afreeca Freecs", 16);
        teamRepository.save(AF);

        HeadCoach oktop = HeadCoach.createHeadCoach("oktop");
        Player kiin = Player.createPlayer("kiin", Position.TOP, 15);
        oktop.setTeam(AF);
        kiin.registerTeam(AF);

        Participant lck_af = participantService.create(AF.getId(), LCK.getId());
        Participant lpl_af = participantService.create(AF.getId(), LPL.getId());

        // when

        // then
        assertThrows(IllegalStateException.class, () -> {
            matchService.create(null, lck_af.getId(), lpl_af.getId());
        });
    }

    @Test
    public void 경기생성검증_다른대회() throws Exception {
        // given
        League LCK = new League();
        League LPL = new League();

        em.persist(LCK);
        em.persist(LPL);
        Team AF = Team.createTeam("Afreeca Freecs", 16);
        Team RNG = Team.createTeam("Royal Never Giveup", 13);
        em.persist(AF);
        em.persist(RNG);

        HeadCoach oktop = HeadCoach.createHeadCoach("oktop");
        HeadCoach uzi = HeadCoach.createHeadCoach("Uzi");
        oktop.setTeam(AF);
        uzi.setTeam(RNG);

        Player kiin = Player.createPlayer("kiin", Position.TOP, 15);
        Player ming = Player.createPlayer("ming", Position.TOP, 10);
        kiin.registerTeam(AF);
        ming.registerTeam(RNG);


        // when
        Participant af_lck = participantService.create(AF.getId(), LCK.getId());
        Participant rng_lpl = participantService.create(RNG.getId(), LPL.getId());

        // then
        assertThrows(IllegalStateException.class, () -> {
            matchService.create(null, af_lck.getId(), rng_lpl.getId());
        });
    }
    
    @Test
    public void 경기결과업데이트() throws Exception {
        // given
        League LCK = new League();
        em.persist(LCK);

        Team DK = Team.createTeam("DWG KIA", 15);
        Team AF = Team.createTeam("Afreeca Freecs", 16);

        em.persist(DK);
        em.persist(AF);

        Participant af_lck = participantService.create(AF.getId(), LCK.getId());
        Participant dk_lck = participantService.create(DK.getId(), LCK.getId());

        Match match = matchService.create(null, af_lck.getId(), dk_lck.getId());

        // when
        match.matchTeams(0, 2);
        match.matchTeams(2, 1);

        // then
        assertThat(af_lck.getRecord().getWin()).isEqualTo(1);
        assertThat(dk_lck.getRecord().getLoss()).isEqualTo(1);
        assertThat(af_lck.getRecord().getSetWin()).isEqualTo(2);
        assertThat(af_lck.getRecord().getSetLoss()).isEqualTo(1);
        assertThat(dk_lck.getRecord().getSetWin()).isEqualTo(1);
        assertThat(dk_lck.getRecord().getSetLoss()).isEqualTo(2);
    }

    @Test
    public void 경기검색() throws Exception {
        // given
        League LCK = new League();
        League LPL = new League();
        em.persist(LCK);
        em.persist(LPL);

        Team DK = Team.createTeam("DWG KIA", 15);
        Team AF = Team.createTeam("Afreeca Freecs", 16);
        Team KT = Team.createTeam("KT Rolster", 10);

        em.persist(DK);
        em.persist(AF);
        em.persist(KT);

        Participant af_lck = participantService.create(AF.getId(), LCK.getId());
        Participant dk_lck = participantService.create(DK.getId(), LCK.getId());
        Participant kt_lck = participantService.create(KT.getId(), LCK.getId());
        Participant af_lpl = participantService.create(AF.getId(), LPL.getId());
        Participant dk_lpl = participantService.create(DK.getId(), LPL.getId());

        Match match1 = matchService.create(null, af_lck.getId(), dk_lck.getId());
        Match match2 = matchService.create(null, af_lck.getId(), kt_lck.getId());
        Match match3 = matchService.create(null, af_lpl.getId(), dk_lpl.getId());

        // when
        List<Match> matchByDK = matchService.searchMatch(DK.getId(), null);
        List<Match> matchByLCK = matchService.searchMatch(null, LCK.getId());
        List<Match> matchByAF_LCK = matchService.searchMatch(AF.getId(), LCK.getId());

        // then
        Assertions.assertThat(matchByDK.size()).isEqualTo(2);
        Assertions.assertThat(matchByLCK.size()).isEqualTo(2);
        Assertions.assertThat(matchByAF_LCK.size()).isEqualTo(2);
    }

    @Test
    public void 경기검색_출력순서() throws Exception {
        // given
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date1_str = "2022-10-02";
        String date2_str = "2022-10-01";
        String date3_str = "2022-09-24";
        String date4_str = "2021-12-21";
        // order : 4 -> 3 -> 2 -> 1

        Date date1 = format.parse(date1_str);
        Date date2 = format.parse(date2_str);
        Date date3 = format.parse(date3_str);
        Date date4 = format.parse(date4_str);

        League LCK = new League();
        League LPL = new League();
        em.persist(LCK);
        em.persist(LPL);

        Team DK = Team.createTeam("DWG KIA", 15);
        Team AF = Team.createTeam("Afreeca Freecs", 16);

        em.persist(DK);
        em.persist(AF);

        Participant af_lck = participantService.create(AF.getId(), LCK.getId());
        Participant dk_lck = participantService.create(DK.getId(), LCK.getId());

        matchService.create(date1, af_lck.getId(), dk_lck.getId());
        matchService.create(date3, af_lck.getId(), dk_lck.getId());
        matchService.create(date2, af_lck.getId(), dk_lck.getId());
        matchService.create(date4, af_lck.getId(), dk_lck.getId());

        // when
        List<Match> matchByLCK = matchService.searchMatch(null, LCK.getId());

        // then
        Date prev = format.parse("2020-01-01");
        for (int i=0; i<matchByLCK.size(); ++i) {
            Assertions.assertThat(prev.getTime() <= matchByLCK.get(i).getMatchDate().getTime()).isEqualTo(true);
            prev = matchByLCK.get(i).getMatchDate();
        }
    }
}

//    League LCK = new League();
//    League LPL = new League();
//
//    HeadCoach oktop = HeadCoach.createHeadCoach("oktop");
//    HeadCoach uzi = HeadCoach.createHeadCoach("Uzi");
//    HeadCoach daney = HeadCoach.createHeadCoach("Daney");
//
//    Player kiin = Player.createPlayer("kiin", Position.TOP, 15);
//    Player nuguri = Player.createPlayer("nuguri", Position.TOP, 16);
//    Player ming = Player.createPlayer("ming", Position.TOP, 10);
//
//    Team DK = Team.createTeam("DWG KIA", 15, daney, nuguri);
//    Team AF = Team.createTeam("Afreeca Freecs", 16, oktop, kiin);
//    Team RNG = Team.createTeam("Royal Never Giveup", 13, uzi, ming);