package jangseop.myleague.service;

import jangseop.myleague.domain.Player;
import jangseop.myleague.domain.PlayerSearch;
import jangseop.myleague.domain.Position;
import jangseop.myleague.domain.Team;
import jangseop.myleague.repository.PlayerRepository;
import jangseop.myleague.repository.TeamRepository;
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
    private final TeamRepository teamRepository;

    /**
     * 선수 생성/저장
     */
    @Transactional
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
     * 선수 정보 수정
     */
    @Transactional
    public Player update(Long playerId, String name, Position position, int stat) {
        Player player = playerRepository.findOne(playerId);

        // TODO [exception] name must not be modify

        if (player == null) {
            player = create(name, position, stat);
        } else {
            player.setInfo(name, position, stat);
        }

        return player;
    }

    /**
     * 선수 팀 등록
     */
    @Transactional
    public Player registerTeam(Long teamId, Long playerId) {
        Player player = playerRepository.findOne(playerId);
        Team team = teamRepository.findOne(teamId);
        player.registerTeam(team);

        return player;
    }

    /**
     * 선수 팀 등록 해제
     */
    @Transactional
    public Player deregisterTeam(Long playerId) {
        Player player = playerRepository.findOne(playerId);
        player.deregisterTeam();

        return player;
    }

    /**
     * 선수 조회 (동적쿼리)
     */
    public List<Player> findPlayer(String name, Long teamId, String position_str) {

        Team team;
        Position position;

        try {
            if (position_str != null) position = Position.valueOf(position_str);
            else position = null;
        } catch (IllegalArgumentException e) {
            position = null;
        }

        return playerRepository.findAll(new PlayerSearch(name, teamId, position));
    }

    /**
     * 선수 삭제
     */
    @Transactional
    public void deletePlayer(Long playerId) {
        Player findPlayer = playerRepository.findOne(playerId);
        playerRepository.delete(findPlayer);
    }
}
