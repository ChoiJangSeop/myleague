package jangseop.myleague.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jangseop.myleague.domain.*;
import jangseop.myleague.domain.record.Record;
import jangseop.myleague.dto.MatchDto;
import jangseop.myleague.repository.*;
import jangseop.myleague.service.MatchService;
import jangseop.myleague.service.ParticipantService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
class MatchControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired MatchRepository matchRepository;
    @Autowired MatchService matchService;
    @Autowired TeamRepository teamRepository;
    @Autowired LeagueRepository leagueRepository;
    @Autowired RecordRepository recordRepository;
    @Autowired ParticipantService participantService;
    @Autowired ParticipantRepository participantRepository;

    @Test
    public void getAll() throws Exception {
        // given
        Map<String,Long> db = initDB();
        Match match = matchService.create(1, null, db.get("recordId1"), db.get("recordId2"));

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/matches")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void getOne() throws Exception {
        // given
        Map<String, Long> db = initDB();
        Long matchId1 = matchService.create(1, null, db.get("recordId1"), db.get("recordId2")).getId();
        Long matchId2 = matchService.create(1, null, db.get("recordId2"), db.get("recordId3")).getId();

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/matches/"+matchId1.intValue())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeId", is(db.get("recordId1").intValue())));
    }

    @Test
    public void postOne() throws Exception {
        // given
        Map<String, Long> db = initDB();
        MatchDto dto = new MatchDto(1, null, db.get("recordId1"), db.get("recordId2"));
        String content = objectMapper.writeValueAsString(dto);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().is2xxSuccessful());
        assertThat(matchRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void putPlayMatch() throws Exception {
        // given
        Map<String, Long> db = initDB();
        Match match = matchService.create(1, null, db.get("recordId1"), db.get("recordId2"));
        MatchDto dto = new MatchDto();
        dto.setHomeScore(1); dto.setAwayScore(2);
        String content = objectMapper.writeValueAsString(dto);

        // when
        mockMvc.perform(MockMvcRequestBuilders.put("/matches/"+ match.getId().intValue() + "/play")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().is2xxSuccessful());

        assertThat(matchRepository.findOne(match.getId()).getHomeScore()).isEqualTo(dto.getHomeScore());
        assertThat(matchRepository.findOne(match.getId()).getAwayScore()).isEqualTo(dto.getAwayScore());

        assertThat(recordRepository.findOne(db.get("recordId1")).getLoss())
                .isEqualTo(1);
        assertThat(recordRepository.findOne(db.get("recordId2")).getWin())
                .isEqualTo(1);

        // etc
        mockMvc.perform(MockMvcRequestBuilders.get("/participants/"+db.get("participantId1").intValue())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        mockMvc.perform(MockMvcRequestBuilders.get("/participants/"+db.get("participantId2").intValue())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void getSearch() throws Exception {
        // given
        Map<String, Long> db = initDB();
        Match AFvsGEN = matchService.create(1, null, db.get("recordId1"), db.get("recordId2"));
        Match KTvsAF = matchService.create(1, null, db.get("recordId3"), db.get("recordId1"));
        Match AFvsGEN_lpl = matchService.create(1, null, db.get("recordId4"), db.get("recordId5"));

        // case #1 : search by team
        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/matches/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("team", db.get("teamId1").toString()))   // af
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));

        // case #2 : search by league
        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/matches/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("league", db.get("leagueId").toString()))    // lck
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // case #3 : search by team and league
        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/matches/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("team", db.get("teamId1").toString())        // af
                .param("league", db.get("leagueId").toString()))    // lck
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    private Map<String ,Long> initDB() {
        Long leagueId = leagueRepository.save(League.createLeague(
                "LCK", null, null, 1, 1, Playoff.DOUBLE_ELIMINATION
        ));
        Long leagueId2 = leagueRepository.save(League.createLeague(
                "LPL", null, null, 1, 1, Playoff.DOUBLE_ELIMINATION
        ));
        Long teamId1 = teamRepository.save(Team.createTeam("Afreeca", 11));
        Long teamId2 = teamRepository.save(Team.createTeam("Gen.G", 12));
        Long teamId3 = teamRepository.save(Team.createTeam("KT", 9));

        Long participantId1 = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId1),
                leagueRepository.findOne(leagueId)
        ));
        Long recordId1 = participantService.addRecord(participantId1, 1, Playoff.FULL_LEAGUE).getId();

        Long participantId2 = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId2),
                leagueRepository.findOne(leagueId)
        ));
        Long recordId2 = participantService.addRecord(participantId2, 1, Playoff.FULL_LEAGUE).getId();

        Long participantId3 = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId3),
                leagueRepository.findOne(leagueId)
        ));
        Long recordId3 = participantService.addRecord(participantId3, 1, Playoff.FULL_LEAGUE).getId();

        Long participantId4 = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId1),
                leagueRepository.findOne(leagueId2)
        ));
        Long recordId4 = participantService.addRecord(participantId4, 1, Playoff.FULL_LEAGUE).getId();

        Long participantId5 = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId2),
                leagueRepository.findOne(leagueId2)
        ));
        Long recordId5 = participantService.addRecord(participantId5, 1, Playoff.FULL_LEAGUE).getId();

        HashMap<String, Long> ret = new HashMap<>();
        ret.put("leagueId", leagueId);
        ret.put("teamId1", teamId1);
        ret.put("teamId2", teamId2);
        ret.put("teamId3", teamId3);
        ret.put("participantId1", participantId1);
        ret.put("participantId2", participantId2);
        ret.put("participantId3", participantId3);
        ret.put("participantId4", participantId4);
        ret.put("participantId5", participantId5);
        ret.put("recordId1", recordId1);
        ret.put("recordId2", recordId2);
        ret.put("recordId3", recordId3);
        ret.put("recordId4", recordId4);
        ret.put("recordId5", recordId5);
        return ret;
    }

}