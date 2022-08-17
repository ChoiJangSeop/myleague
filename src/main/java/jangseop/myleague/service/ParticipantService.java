package jangseop.myleague.service;

import jangseop.myleague.domain.League;
import jangseop.myleague.domain.Participant;
import jangseop.myleague.domain.Team;
import jangseop.myleague.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    /**
     * 참가 도메인 생성 및 저장
     */
    public Participant create(Team team, League league) {
        validateCreateParticipant(team, league);
        Participant participant = Participant.createParticipant(team, league);
        participantRepository.save(participant);
        return participant;
    }

    /**
     * 참가 도메인 생성 검증
     */
    public void validateCreateParticipant(Team team, League league) {
        List<Participant> participants = league.getParticipants();

        for (Participant participant : participants) {
            if (team == participant.getTeam()) {
                throw new IllegalStateException("이미 해당 리그에 소속된 팀입니다");
            }
        }
    }

    /**
     * TODO 참가팀 검색 (대회,팀) 동적쿼리
     */





}
