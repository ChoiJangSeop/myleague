package jangseop.myleague.service;

import jangseop.myleague.domain.Player;
import jangseop.myleague.domain.PlayerSearch;
import jangseop.myleague.domain.Position;
import jangseop.myleague.domain.Team;
import jangseop.myleague.repository.PlayerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class PlayerServiceTest {

    @Autowired private PlayerService playerService;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private TeamService teamService;

    @Test
    public void 선수생성() throws Exception {
        // given
        Player player = playerService.create("Kiin", Position.TOP, 20);

        // when
        Player findPlayer = playerRepository.findByName("Kiin");

        // then
        assertThat(player).isEqualTo(findPlayer);
    }

    @Test
    public void 선수명중복검증() throws Exception {
        // given
        playerService.create("Kiin", Position.TOP, 20);

        // when

        // then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> {
            playerService.create("Kiin", Position.JGL, 15);
        });
    }

    @Test
    public void 선수검색() throws Exception {
        // given
        Player kiin = playerService.create("kiin", Position.TOP, 15);
        Player doran = playerService.create("doran", Position.TOP, 12);

        Team af = Team.createTeam("Afreeca Freecs", 12);
        kiin.setTeam(af);

        // when
        Player findPlayerByName = playerRepository
                .findAll(new PlayerSearch("kiin", null, null))
                .get(0);

        Player findPlayerByTeam = playerRepository
                .findAll(new PlayerSearch(null, af, null))
                .get(0);

        Player findPlayerByTeamPosition = playerRepository
                .findAll(new PlayerSearch(null, af, Position.TOP))
                .get(0);

        int numFindPlayerByPosition = playerRepository
                .findAll(new PlayerSearch(null, null, Position.TOP))
                .size();

        // then
        assertThat(findPlayerByName).isEqualTo(kiin);
        assertThat(findPlayerByTeam).isEqualTo(kiin);
        assertThat(findPlayerByTeamPosition).isEqualTo(kiin);
        assertThat(numFindPlayerByPosition).isEqualTo(2);
    }
}