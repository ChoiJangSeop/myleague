package jangseop.myleague.service;

import jangseop.myleague.domain.HeadCoach;
import jangseop.myleague.domain.Player;
import jangseop.myleague.domain.Team;
import jangseop.myleague.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    @Autowired
    private final TeamRepository teamRepository;

    /**
     * 팀 생성
     */
    @Transactional
    public Long create(String name, int teamStat, HeadCoach headCoach, Player ...players) {
        validateCreateTeam(name, headCoach, players);
        Team team = Team.createTeam(name, teamStat, headCoach, players);
        return teamRepository.save(team);
    }

    /**
     * 팀 생성 검증
     * 1. 팀 이름 중복 금지
     * 2. 선수/감독 이중등록 금지
     */
    public void validateCreateTeam(String name, HeadCoach headCoach, Player ...players) {
        List<Team> findTeams = teamRepository.findAll();

        // duplicate name
        for (Team findTeam : findTeams) {
            if (findTeam.getName().equals(name)) {
                throw new IllegalStateException("이미 동일한 이름의 팀이 존재합니다");
            }
        }

        if (headCoach.getTeam() != null) {
            throw new IllegalStateException("이미 다른 팀에 소속된 감독입니다");
        }

        for (Player player : players) {
            if (player.getTeam() != null) {
                throw new IllegalStateException("이미 다른 팀에 소속된 선수입니다");
            }
        }
    }


    /**
     * 팀 조회
     */
    public Team findTeam(Long teamId) {
        return teamRepository.findOne(teamId);
    }

    public Team findTeamByName(String name) {
        return teamRepository.findByName(name).get(0);
    }

    /**
     * 대회 참가
     */
    public void perticipate() {}
}