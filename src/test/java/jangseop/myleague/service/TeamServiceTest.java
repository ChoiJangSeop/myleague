package jangseop.myleague.service;

import jangseop.myleague.domain.HeadCoach;
import jangseop.myleague.domain.Player;
import jangseop.myleague.domain.Team;
import jangseop.myleague.repository.TeamRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class TeamServiceTest {


    @Autowired TeamRepository teamRepository;
    @Autowired TeamService teamService;

    @Test
    public void 팀생성() throws Exception {
        // given
        Long id = teamService.create("Afreeca Freecs", "AF", 10);

        // when
        Team findTeam = teamRepository.findOne(id);

        // then
        assertThat(findTeam.getName()).isSameAs("Afreeca Freecs");
    }

    @Test()
    public void 팀이름중복검증() throws Exception {
        // given
        // when
        teamService.create("team", "t", 19);

        // then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> {
            teamService.create("team", "t", 19);
        });
    }
}