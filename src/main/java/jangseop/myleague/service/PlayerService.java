package jangseop.myleague.service;

import jangseop.myleague.domain.Player;
import jangseop.myleague.domain.Position;
import jangseop.myleague.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * 선수 생성/저장
     */
    public Player create(String name, Position position, int stat) {
        validateNameDuplicate(name);
        Player player = Player.createPlayer(name, position, stat);
        playerRepository.save(player);
        return player;
    }

    public void validateNameDuplicate(String name) {
        Player findPlayer = playerRepository.findByName(name);

        if (findPlayer != null) {
            throw new IllegalStateException("중복되는 이름입니다");
        }
    }

    /**
     * 선수 조회 (동적쿼리)
     */
    // TODO 선수 조회 메서드 : 동적 쿼리로 구현
    public List<Player> findPlayer() {
        return null;
    }
}
