package jangseop.myleague.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jangseop.myleague.domain.Player;
import jangseop.myleague.domain.Position;
import jangseop.myleague.domain.Team;
import jangseop.myleague.dto.PlayerDto;
import jangseop.myleague.repository.PlayerRepository;
import jangseop.myleague.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
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

import static jangseop.myleague.domain.Position.*;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Transactional
class PlayerControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TeamRepository teamRepository;
    @Autowired private PlayerRepository playerRepository;

    @Test
    public void getAll() throws Exception {
        // given
        playerRepository.save(Player.createPlayer("kiin", TOP, 15));
        playerRepository.save(Player.createPlayer("doran", TOP, 12));

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/players")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.playerDtoList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.playerDtoList[0].name", Matchers.is("kiin")));
    }

    @Test
    public void getOne() throws Exception {
        // given
        Long playerId = playerRepository.save(Player.createPlayer("kiin", TOP, 15));

        // case #1 : get existed player

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("kiin")));

        // case#2 : get not found player

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId+1)
                .contentType(MediaType.APPLICATION_JSON))
        // then
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void postOne() throws Exception {
        // given
        String player = objectMapper.writeValueAsString(new PlayerDto("kiin", TOP, 15));

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(player)
                .accept(MediaType.APPLICATION_JSON))

        // then
                .andDo(print())
                .andExpect(status().isOk());
        Assertions.assertThat(playerRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void putRegister() throws Exception {
        // given
        Long playerId = playerRepository.save(Player.createPlayer("KIIN", TOP, 15));
        Long teamId = teamRepository.save(Team.createTeam("Afreeca", 10));

        // when
        mockMvc.perform(MockMvcRequestBuilders.put("/players/" + playerId + "/register/" + teamId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/players/"+playerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.teamId", Matchers.is(teamId.intValue())));
    }

    @Test
    public void putDeregister() throws Exception {
        // given
        Long playerId = playerRepository.save(Player.createPlayer("kiin", TOP, 15));
        Long teamId = teamRepository.save(Team.createTeam("Afreeca", 11));
        playerRepository.findOne(playerId).registerTeam(teamRepository.findOne(teamId));

        mockMvc.perform(MockMvcRequestBuilders.get("/players/"+playerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId", Matchers.is(teamId.intValue())));

        // when
        mockMvc.perform(MockMvcRequestBuilders.put("/players/" + playerId + "/deregister")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

        // then
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(MockMvcRequestBuilders.get("/players/"+playerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId", Matchers.is(-1)));
    }

    @Test
    public void getSearch() throws Exception {
        // given
        Long playerId1 = playerRepository.save(Player.createPlayer("kiin", TOP, 15));
        Long playerId2 = playerRepository.save(Player.createPlayer("doran", TOP, 15));
        Long playerId3 = playerRepository.save(Player.createPlayer("fate", MID, 15));

        Long teamId1 = teamRepository.save(Team.createTeam("Afreeca", 15));
        Long teamId2 = teamRepository.save(Team.createTeam("Gen.G", 15));

        playerRepository.findOne(playerId1).registerTeam(teamRepository.findOne(teamId1));
        playerRepository.findOne(playerId2).registerTeam(teamRepository.findOne(teamId2));
        playerRepository.findOne(playerId3).registerTeam(teamRepository.findOne(teamId1));

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/players/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("position", "TOP"))
                .andDo(print())
                .andExpect(jsonPath("$._embedded.playerDtoList", hasSize(2)));


        mockMvc.perform(MockMvcRequestBuilders.get("/players/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("team", teamId1.toString()))
                .andDo(print())
                .andExpect(jsonPath("$._embedded.playerDtoList", hasSize(2)));


        mockMvc.perform(MockMvcRequestBuilders.get("/players/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("position", "TOP")
                        .param("teamId", teamId2.toString()))
                .andDo(print())
                .andExpect(jsonPath("$._embedded.playerDtoList", hasSize(1)));
        // then
    }

}