package jangseop.myleague.service;

import jangseop.myleague.domain.League;
import jangseop.myleague.dto.LeagueDto;
import jangseop.myleague.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LeagueService {

    private final LeagueRepository leagueRepository;

    @Transactional
    public League create(LeagueDto leagueDto) {

        validateTitleDuplicate(leagueDto.getTitle(), leagueDto.getStartedDate(), leagueDto.getEndDate());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);

        Date parseStartedDate = null;
        Date parseEndDate = null;

        try {
            if (!leagueDto.getStartedDate().equals("")) {
                parseStartedDate = format.parse(leagueDto.getStartedDate());
                parseEndDate = format.parse(leagueDto.getEndDate());
            }
        } catch (ParseException e) {
            throw new IllegalStateException("유효하지 않은 날짜 형식입니다");
        }

        validateDateOrder(leagueDto.getStartedDate(), leagueDto.getEndDate());

        League league = League.createLeague(
                leagueDto.getTitle(),
                parseStartedDate,
                parseEndDate,
                leagueDto.getRoundRobins(),
                leagueDto.getPromotions(),
                leagueDto.getPlayoff());
        Long id = leagueRepository.save(league);
        return league;
    }

    private void validateTitleDuplicate(String title, String startedDate, String endDate) {

        // 1. duplicate title
        long duplicateTitles = leagueRepository.findAll().stream()
                .filter(league -> (league.getTitle() == title))
                .count();

        if (duplicateTitles > 0) {
            throw new IllegalStateException("이미 중복된 이름을 가진 리그가 존재합니다");
        }
    }

    private void validateDateOrder(String startedDate, String endDate) {
        if (startedDate.equals("") && endDate.equals("")) return;

        int startedDate_int = Integer.parseInt(startedDate.replaceAll("-", ""));
        int endDate_int = Integer.parseInt(endDate.replaceAll("-", ""));

        if (startedDate_int > endDate_int) {
            throw new IllegalStateException("시작 날짜가 종료 날짜보다 이전이야야 합니다.");
        }
    }

    public void registerMatches() {

    }

}
