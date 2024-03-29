package jangseop.myleague.service;

import jangseop.myleague.domain.*;
import jangseop.myleague.domain.record.Record;
import jangseop.myleague.repository.*;
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

    private final RecordRepository recordRepository;

    /**
     * 경기 생성 및 저장
     */
    @Transactional
    public Match create(int round, Date date, Long homeId, Long awayId) {

        // 엔티티 조회
        Record home = recordRepository.findOne(homeId);
        Record away = recordRepository.findOne(awayId);

        // 경기 생성 검증
        validateParticipantCreateMatch(date, home.getParticipant(), away.getParticipant());
        validateRecordCreateMatch(home, away);

        // 경기 생성
        Match match = Match.createMatch(date, round, home, away);

        // 경기 저장
        matchRepository.save(match);

        return match;
    }

    /**
     * 경기 생성 검증
     */

    // 1. Participant validation
    public void validateParticipantCreateMatch(Date date, Participant home, Participant away) {


        if (home.getTeam() == away.getTeam()) {
            throw new IllegalStateException("경기에 참가하는 두 팀은 다른 팀이어야 합니다");
        }

        if (home.getLeague() != away.getLeague()) {
            throw new IllegalStateException("경기에 참가하는 팀은 같은 대회에 소속되어 있어야 합니다");
        }
    }

    // 2. Record validation
    public void validateRecordCreateMatch(Record home, Record away) {
        if (home.getRound() != away.getRound()) {
            // TODO refector error message
            throw new IllegalStateException("경기 기록이 서로 다른 라운드임");
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

    /**
     * 경기 삭제
     */
    @Transactional
    public void deleteMatch(Long matchId) {
        Match findMatch = matchRepository.findOne(matchId);
        matchRepository.delete(findMatch);
    }
}
