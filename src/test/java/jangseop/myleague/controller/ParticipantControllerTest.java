package jangseop.myleague.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jangseop.myleague.domain.*;
import jangseop.myleague.domain.record.FullLeague;
import jangseop.myleague.domain.record.Record;
import jangseop.myleague.dto.ParticipantDto;
import jangseop.myleague.repository.LeagueRepository;
import jangseop.myleague.repository.ParticipantRepository;
import jangseop.myleague.repository.TeamRepository;
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

import static jangseop.myleague.domain.Playoff.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
class ParticipantControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ParticipantRepository participantRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private LeagueRepository leagueRepository;
    @Autowired private ParticipantService participantService;

    @Test
    public void getAll() throws Exception {

        // given
        Long teamId = teamRepository.save(Team.createTeam("Afreeca", 10));
        Long leagueId = leagueRepository.save(League.createLeague(
                "LCK", null, null, 1, 1, DOUBLE_ELIMINATION));
        participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId),
                leagueRepository.findOne(leagueId)));

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/participants")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void getOne() throws Exception {
        // given
        Long teamId = teamRepository.save(Team.createTeam("Afreeca", 10));
        Long leagueId = leagueRepository.save(League.createLeague(
                "LCK", null, null, 1, 1, DOUBLE_ELIMINATION));
        Long participantId = participantRepository.save(Participant.createParticipant(
                teamRepository.findOne(teamId),
                leagueRepository.findOne(leagueId)));

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/participants/" + participantId.intValue())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId", is(teamId.intValue())))
                .andExpect(jsonPath("$.leagueId", is(leagueId.intValue())));
    }

    @Test
    public void postOne() throws Exception {
        // given
        Long teamId = teamRepository.save(Team.createTeam("Afreeca", 10));
        Long leagueId = leagueRepository.save(League.createLeague(
                "LCK", null, null, 1, 1, KNOCKOUT));
        String dto = objectMapper.writeValueAsString(new ParticipantDto(teamId, leagueId, 1));

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dto)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().is2xxSuccessful());
        Assertions.assertThat(participantRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void getSearch() throws Exception {
        // given
        Long teamId1 = teamRepository.save(Team.createTeam("Afreeca", 15));
        Long teamId2 = teamRepository.save(Team.createTeam("Gen.G", 15));
        Long leagueId1 = leagueRepository.save(League.createLeague(
                "LCK", null, null, 1, 1, DOUBLE_ELIMINATION));
        Long leagueId2 = leagueRepository.save(League.createLeague(
                "LPL", null, null, 1, 1, DOUBLE_ELIMINATION));
        Participant p1 = participantService.create(teamId1, leagueId1);
        Participant p2 = participantService.create(teamId1, leagueId2);
        Participant p3 = participantService.create(teamId2, leagueId1);
        Participant p4 = participantService.create(teamId2, leagueId2);

        // case #1
        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/participants/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("league", leagueId1.toString()))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].leagueId", is(leagueId1.intValue())))
                .andExpect(jsonPath("$.content[1].leagueId", is(leagueId1.intValue())));

        // case #2
        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/participants/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("team", teamId1.toString()))
                .andDo(print())
        // then
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].teamId", is(teamId1.intValue())))
                .andExpect(jsonPath("$.content[1].teamId", is(teamId1.intValue())));

        // case #3
        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/participants/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("team", teamId1.toString())
                        .param("league", leagueId1.toString()))
                .andDo(print())
                // then
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].teamId", is(teamId1.intValue())))
                .andExpect(jsonPath("$.content[0].leagueId", is(leagueId1.intValue())));
    }

    @Test
    public void getOneRecord() throws Exception {
        // given
        Long teamId = teamRepository.save(Team.createTeam("Afreeca", 10));
        Long leagueId = leagueRepository.save(League.createLeague(
                "LCK", null, null, 1, 1, DOUBLE_ELIMINATION));
        Participant participant = Participant.createParticipant(teamRepository.findOne(teamId), leagueRepository.findOne(leagueId));
        Long participantId = participantRepository.save(participant);

        Record record = participantService.addRecord(participantId, 1, DOUBLE_ELIMINATION);


        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/participants/" + participantId.intValue() + "/records/" + record.getRound())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(jsonPath("$.participantId", is(participantId.intValue())));
    }
}