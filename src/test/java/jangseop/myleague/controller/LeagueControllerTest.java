package jangseop.myleague.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jangseop.myleague.domain.League;
import jangseop.myleague.domain.Playoff;
import jangseop.myleague.dto.LeagueDto;
import jangseop.myleague.repository.LeagueRepository;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Transactional
class LeagueControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private LeagueRepository leagueRepository;

    @Test
    public void getAll() throws Exception {
        // given
        leagueRepository.save(League.createLeague(
                "LCK", null, null, 1, 1, Playoff.DOUBLE_ELIMINATION
        ));

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/leagues")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(jsonPath("$._embedded.leagueDtoList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.leagueDtoList[0].title", is("LCK")));
    }

    @Test
    public void getOne() throws Exception {
        // given
        Long id = leagueRepository.save(League.createLeague(
                "LCK", null, null, 1, 1, Playoff.DOUBLE_ELIMINATION
        ));

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/leagues/"+id.intValue())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("LCK")));
    }

    @Test
    public void postOne() throws Exception {
        // given
        String dto = objectMapper.writeValueAsString(
                new LeagueDto("LCK", null, null, 1, 1, Playoff.DOUBLE_ELIMINATION));

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/leagues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dto)
                .accept(MediaType.APPLICATION_JSON))
        // then
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        Assertions.assertThat(leagueRepository.findAll().size()).isEqualTo(1);
    }
}