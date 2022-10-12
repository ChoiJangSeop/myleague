package jangseop.myleague.service;

import jangseop.myleague.domain.League;
import jangseop.myleague.dto.LeagueDto;
import jangseop.myleague.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LeagueService {

    private final LeagueRepository leagueRepository;

    @Transactional
    public League create(LeagueDto leagueDto) {

        validateTitleDuplicate(leagueDto.getTitle());

        League league = League.createLeague(
                leagueDto.getTitle(),
                leagueDto.getStartedDate(),
                leagueDto.getEndDate(),
                leagueDto.getRoundRobins(),
                leagueDto.getPromotions(),
                leagueDto.getPlayoff());
        Long id = leagueRepository.save(league);
        return league;
    }

    private void validateTitleDuplicate(String title) {
        long duplicateTitles = leagueRepository.findAll().stream()
                .filter(league -> (league.getTitle() == title))
                .count();

        if (duplicateTitles > 0) {
            throw new IllegalStateException("이미 중복된 이름을 가진 리그가 존재합니다");
        }
    }

    public void registerMatches() {

    }

}
