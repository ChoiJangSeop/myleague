package jangseop.myleague.service;

import jangseop.myleague.domain.Player;
import jangseop.myleague.domain.PlayerSearch;
import jangseop.myleague.domain.Position;
import jangseop.myleague.domain.Team;
import jangseop.myleague.repository.PlayerRepository;
import jangseop.myleague.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class PlayerServiceTest {

    @Autowired private PlayerService playerService;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private TeamService teamService;
    @Autowired private TeamRepository teamRepository;

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

        Long af = teamService.create("Afreeca", "AF", 12);
        kiin.registerTeam(teamRepository.findOne(af));

        // when
        List<Player> findPlayerByName = playerService.findPlayer("kiin", null, null);
        List<Player> findPlayerByPosition = playerService.findPlayer(null, null, "TOP");
        List<Player> findPlayerByPosAndTeam = playerService.findPlayer(null, af, "TOP");


        // then
        assertThat(findPlayerByName.get(0)).isEqualTo(kiin);
        assertThat(findPlayerByName.size()).isEqualTo(1);

        assertThat(findPlayerByPosition.size()).isEqualTo(2);
        assertThat(findPlayerByPosition.contains(kiin)).isEqualTo(true);
        assertThat(findPlayerByPosition.contains(doran)).isEqualTo(true);

        assertThat(findPlayerByPosAndTeam.size()).isEqualTo(1);
        assertThat(findPlayerByPosAndTeam.get(0)).isEqualTo(kiin);


    }
}