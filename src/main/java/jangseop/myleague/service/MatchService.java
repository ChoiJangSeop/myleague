package jangseop.myleague.service;

import jangseop.myleague.domain.Match;
import jangseop.myleague.domain.Participant;
import jangseop.myleague.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    /**
     * 경기 생성 및 저장
     */
    public Match create(Date date, Participant home, Participant away) {

        validateCreateMatch(date, home, away);
        Match match = Match.createMatch(date, home, away);
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

        if (date != null &&
                (home.getLeague().getStartedDate().after(date) || home.getLeague().getEndDate().before(date))) {
            throw new IllegalStateException("경기 일정이 대회 일정 중에 있어야 합니다");
        }

    }
}
