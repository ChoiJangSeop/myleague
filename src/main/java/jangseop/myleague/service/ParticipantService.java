package jangseop.myleague.service;

import jangseop.myleague.domain.*;
import jangseop.myleague.domain.record.Record;
import jangseop.myleague.repository.LeagueRepository;
import jangseop.myleague.repository.ParticipantRepository;
import jangseop.myleague.repository.RecordRepository;
import jangseop.myleague.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;
    private final RecordRepository recordRepository;

    /**
     * 참가 도메인 생성 및 저장
     */
    @Transactional
    public Participant create(Long teamId, Long leagueId) {

        // 엔티티 조회
        Team team = teamRepository.findOne(teamId);
        League league = leagueRepository.findOne(leagueId);

        // 생성 검증
        validateCreateParticipant(team, league);

        // 참가자 생성
        Participant participant = Participant.createParticipant(team, league);
        // 참가자 저장
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
     * 참가팀 검색 (대회,팀) 동적쿼리
     */
    public List<Participant> searchParticipants(Long teamId, Long leagueId) {

        Team team;
        League league;

        if (teamId == null) {
            team = null;
        } else {
            team = teamRepository.findOne(teamId);
        }

        if (leagueId == null) {
            league = null;
        } else {
            league = leagueRepository.findOne(leagueId);
        }

        return participantRepository.findAll(new ParticipantSearch(team, league));
    }

    /**
     * 참가 도메인 삭제
     */
    @Transactional
    public void delete(Long id) {
        Participant participant = participantRepository.findOne(id);
        participantRepository.delete(participant);
    }

    /**
     * 새로운 라운드(기록) 추가
     */
    @Transactional
    public Record addRecord(Long id, int round, Playoff type) {
        Record record = Record.createRecord(round, type);
        recordRepository.save(record);

        Participant findParticipant = participantRepository.findOne(id);
        findParticipant.addRecord(record);

        return record;
    }

    /**
     * 특정 라운드의 경기 읽기
     */
    public List<Match> getRoundMatches(Long id, int round) {
        Participant findParticipant = participantRepository.findOne(id);
        return findParticipant.getRoundMatches(round);
    }

}
