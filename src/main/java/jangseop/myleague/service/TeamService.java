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
    public Long create(String name, int teamStat) {
        validateCreateTeam(name);
        Team team = Team.createTeam(name, teamStat);
        return teamRepository.save(team);
    }

    /**
     * 팀 생성 검증
     * 1. 팀 이름 중복 금지
     * 2. 선수/감독 이중등록 금지
     */
    public void validateCreateTeam(String name) {
        List<Team> findTeams = teamRepository.findAll();

        // duplicate name
        for (Team findTeam : findTeams) {
            if (findTeam.getName().equals(name)) {
                throw new IllegalStateException("이미 동일한 이름의 팀이 존재합니다");
            }
        }
    }

    @Transactional
    public Team update(Long teamId, String name, int stat) {
        Team team = teamRepository.findOne(teamId);

        // TODO [refactoring] convert optional type
        if (team == null) {
            Long newTeamId = create(name, stat);
            team = teamRepository.findOne(newTeamId);
        } else {
            team.setName(name);
            team.setTeamStat(stat);
        }

        return team;
    }

    public Team findTeamByName(String name) {
        return teamRepository.findByName(name).get(0);
    }

    /**
     * 대회 참가
     */
    public void perticipate() {}
}
