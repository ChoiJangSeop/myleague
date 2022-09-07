package jangseop.myleague.service;

import jangseop.myleague.domain.Match;
import jangseop.myleague.domain.Participant;
import jangseop.myleague.repository.MatchRepository;
import jangseop.myleague.repository.ParticipantRepository;
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
    private final ParticipantRepository participantRepository;

    /**
     * 경기 생성 및 저장
     */
    public Match create(Date date, Long homeId, Long awayId) {

        // 엔티티 조회
        Participant home = participantRepository.findOne(homeId);
        Participant away = participantRepository.findOne(awayId);

        // 경기 생성 검증
        validateCreateMatch(date, home, away);

        // 경기 생성
        Match match = Match.createMatch(date, home, away);

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

        if (date != null &&
                (home.getLeague().getStartedDate().after(date) || home.getLeague().getEndDate().before(date))) {
            throw new IllegalStateException("경기 일정이 대회 일정 중에 있어야 합니다");
        }

    }
}
