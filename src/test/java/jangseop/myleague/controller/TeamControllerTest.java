package jangseop.myleague.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jangseop.myleague.domain.Team;
import jangseop.myleague.dto.TeamDto;
import jangseop.myleague.repository.TeamRepository;
import jangseop.myleague.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    TeamRepository teamRepository;


    @Test
    public void getAll() throws Exception {
        // given
        teamRepository.save(Team.createTeam("Afreeca", 15));
        teamRepository.save(Team.createTeam("Gen.G", 12));

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/teams")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.teamDtoList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.teamDtoList[1].name", is("Gen.G")));
    }

    @Test
    public void getOne() throws Exception {
        // given
        Long teamId = teamRepository.save(Team.createTeam("Afreeca", 12));

        // case#1

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/teams/"+teamId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Afreeca")));


        // case#2

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/teams/"+(teamId+1))
                        .contentType(MediaType.APPLICATION_JSON))

        // then
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void postOne() throws Exception {
        // given
        String team = objectMapper.writeValueAsString(new TeamDto("Afreeca", 11));

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(team)
                .accept(MediaType.APPLICATION_JSON))
        // then
                .andDo(print())
                .andExpect(status().isOk());
        Assertions.assertThat(teamRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void putOne() throws Exception {
        // given
        Long teamId = teamRepository.save(Team.createTeam("Afreeca", 15));
        String newTeam = objectMapper.writeValueAsString(new TeamDto("Gen.G", 11));

        // case#1 : update name

        // when
        mockMvc.perform(MockMvcRequestBuilders.put("/teams/"+teamId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newTeam)
                .accept(MediaType.APPLICATION_JSON))

        // then
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(MockMvcRequestBuilders.get("/teams/"+teamId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name", is("Gen.G")))
                .andExpect(jsonPath("$.stat", is(11)));
    }


}