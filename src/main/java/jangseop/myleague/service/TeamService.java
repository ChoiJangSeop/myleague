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
    public Long create(String name, String shortName, int teamStat) {

        shortName = shortName.replaceAll("\\s", "").toUpperCase();

        validateCreateTeam(name, shortName);
        Team team = Team.createTeam(name, shortName, teamStat);
        return teamRepository.save(team);
    }

    /**
     * 팀 생성 검증
     * 1. 팀 이름 중복 금지
     * 2. 선수/감독 이중등록 금지
     */
    public void validateCreateTeam(String name, String shortName) {

        if (name.length() == 0 || shortName.length() == 0) {
            throw new IllegalStateException("팀명과 팀이니셜을 모두 입력해야 합니다");
        }

        List<Team> findTeams = teamRepository.findAll();

        // duplicate name
        for (Team findTeam : findTeams) {
            if (findTeam.getShortName().equals(shortName) || findTeam.getName().equals(name)) {
                throw new IllegalStateException("이미 동일한 이름의 팀명(이니셜)이 존재합니다");
            }
        }
    }

    @Transactional
    public Team update(Long teamId, String name, String shortName, int stat) {
        Team team = teamRepository.findOne(teamId);

        if (name.length() == 0 || shortName.length() == 0) {
            throw new IllegalStateException("모든 입력창을 입려해주세요");
        }

        shortName = shortName.replaceAll("\\s", "").toUpperCase();

        // TODO [refactoring] convert optional type
        if (team == null) {
            Long newTeamId = create(name, shortName, stat);
            team = teamRepository.findOne(newTeamId);
        } else {
            team.setAll(name, shortName, stat);
        }

        return team;
    }

    @Transactional
    public void delete(Long teamId) {
        Team team = teamRepository.findOne(teamId);
        teamRepository.delete(team);
    }

    @Transactional
    public void inactiveTeam(Long teamId) {
        Team team = teamRepository.findOne(teamId);
        team.inactiveTeam();
    }

    public Team findTeamByName(String name) {
        return teamRepository.findByName(name).get(0);
    }

    /**
     * 대회 참가
     */
    public void perticipate() {}

    @Transactional
    public void activateTeam(Long teamId) {
        Team team = teamRepository.findOne(teamId);
        team.activeTeam();
    }
}
