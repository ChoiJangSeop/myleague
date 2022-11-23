package jangseop.myleague.service;

import jangseop.myleague.domain.*;
import jangseop.myleague.repository.LeagueRepository;
import jangseop.myleague.repository.MatchRepository;
import jangseop.myleague.repository.ParticipantRepository;
import jangseop.myleague.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final ParticipantRepository participantRepository;
    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;

    /**
     * 경기 생성 및 저장
     */
    @Transactional
    public Match create(int round, Date date, Long homeId, Long awayId) {

        // 엔티티 조회
        Participant home = participantRepository.findOne(homeId);
        Participant away = participantRepository.findOne(awayId);

        // 경기 생성 검증
        validateCreateMatch(date, home, away);

        // 경기 생성
        Match match = Match.createMatch(date, round, home, away);

        // 경기 저장
        matchRepository.save(match);

        return match;
    }

    /**
     * 경기 생성 검증
     */
    public void validateCreateMatch(Date date, Participant home, Participant away) {


        if (home.getTeam() == away.getTeam()) {
            throw new IllegalStateException("경기에 참가하는 두 팀은 다른 팀이어야 합니다");
        }

        if (home.getLeague() != away.getLeague()) {
            throw new IllegalStateException("경기에 참가하는 팀은 같은 대회에 소속되어 있어야 합니다");
        }
    }

    /**
     * 경기 결과 입력
     */
    @Transactional
    public Match playMatch(Long matchId, int homeScore, int awayScore) {
        Match match = matchRepository.findOne(matchId);
        match.matchTeams(homeScore, awayScore);

        return match;
    }

    @Transactional
    public Match cancelMatch(Long matchId) {
        Match match = matchRepository.findOne(matchId);
        match.cancelMatchTeams();

        return match;
    }

    // TODO dynamic search method
    /**
     * 동적 검색 메서드
     */
    public List<Match> searchMatch(Long teamId, Long leagueId) {

        Team team = null;
        League league = null;

        if (teamId != null) {
            team = teamRepository.findOne(teamId);
        }

        if (leagueId != null) {
            league = leagueRepository.findOne(leagueId);
        }

        MatchSearch matchSearch = new MatchSearch(team, league);
        return matchRepository.findAll(matchSearch);
    }
}
