package jangseop.myleague.service;

import jangseop.myleague.domain.Player;
import jangseop.myleague.domain.Position;
import jangseop.myleague.repository.PlayerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class PlayerServiceTest {

    @Autowired private PlayerService playerService;
    @Autowired private PlayerRepository playerRepository;

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
}