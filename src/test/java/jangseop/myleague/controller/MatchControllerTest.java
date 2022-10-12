package jangseop.myleague.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jangseop.myleague.domain.*;
import jangseop.myleague.dto.MatchDto;
import jangseop.myleague.repository.LeagueRepository;
import jangseop.myleague.repository.MatchRepository;
import jangseop.myleague.repository.ParticipantRepository;
import jangseop.myleague.repository.TeamRepository;
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
    @Autowired ParticipantService participantService;
    @Autowired ParticipantRepository participantRepository;

    @Test
    public void getAll() throws Exception {
        // given
        Map<String,Long> db = initDB();
        Match match = matchService.create(null, db.get("participantId1"), db.get("participantId2"));

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/matches")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.matchDtoList", hasSize(1)));
    }

    @Test
    public void getOne() throws Exception {
        // given
        Map<String, Long> db = initDB();
        Long matchId1 = matchService.create(null, db.get("participantId1"), db.get("participantId2")).getId();
        Long matchId2 = matchService.create(null, db.get("participantId2"), db.get("participantId3")).getId();

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/matches/"+matchId1.intValue())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeId", is(db.get("participantId1").intValue())));
    }

    @Test
    public void postOne() throws Exception {
        // given
        Map<String, Long> db = initDB();
        MatchDto dto = new MatchDto(1, null, db.get("participantId1"), db.get("participantId2"));
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
        Match match = matchService.create(null, db.get("participantId1"), db.get("participantId2"));
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

        assertThat(participantRepository.findOne(db.get("participantId1")).getRecord().getLoss())
                .isEqualTo(1);
        assertThat(participantRepository.findOne(db.get("participantId2")).getRecord().getWin())
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
        Match AFvsGEN = matchService.create(null, db.get("participantId1"), db.get("participantId2"));
        Match KTvsAF = matchService.create(null, db.get("participantId3"), db.get("participantId1"));
        Match AFvsGEN_lpl = matchService.create(null, db.get("participantId4"), db.get("participantId5"));

        // case #1 : search by team
        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/matches/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("team", db.get("teamId1").toString()))   // af
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.matchDtoList", hasSize(3)));

        // case #2 : search by league
        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/matches/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("league", db.get("leagueId").toString()))    // lck
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.matchDtoList", hasSize(2)));

        // case #3 : search by team and league
        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/matches/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("team", db.get("teamId1").toString())        // af
                .param("league", db.get("leagueId").toString()))    // lck
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.matchDtoList", hasSize(2)));
    }

    private Map<String ,Long> initDB() {
        Long leagueId = leagueRepository.save(League.createLeague(
                "LCK", null, null, 1, 1, Playoff.SINGLE_ELIMINATION
        ));
        Long leagueId2 = leagueRepository.save(League.createLeague(
                "LPL", null, null, 1, 1, Playoff.SINGLE_ELIMINATION
        ));
        Long teamId1 = teamRepository.save(Team.createTeam("Afreeca", 11));
        Long teamId2 = teamRepository.save(Team.createTeam("Gen.G", 12));
        Long teamId3 = teamRepository.save(Team.createTeam("KT", 9));

        Long participantId1 = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId1),
                leagueRepository.findOne(leagueId)
        ));
        Long participantId2 = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId2),
                leagueRepository.findOne(leagueId)
        ));
        Long participantId3 = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId3),
                leagueRepository.findOne(leagueId)
        ));

        Long participantId4 = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId1),
                leagueRepository.findOne(leagueId2)
        ));

        Long participantId5 = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId2),
                leagueRepository.findOne(leagueId2)
        ));

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
        return ret;
    }

}