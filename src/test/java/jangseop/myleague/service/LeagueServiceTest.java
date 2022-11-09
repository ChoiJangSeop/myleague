package jangseop.myleague.service;

import jangseop.myleague.domain.League;
import jangseop.myleague.domain.Playoff;
import jangseop.myleague.dto.LeagueDto;
import jangseop.myleague.repository.LeagueRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class LeagueServiceTest {

    @Autowired private LeagueRepository leagueRepository;
    @Autowired private LeagueService leagueService;

    @Test
    public void 리그수정() throws Exception {
        // given
        LeagueDto dto = new LeagueDto(
                1L,
                "League",
                "2022-01-01", "2022-12-31",
                1, 1, Playoff.DOUBLE_ELIMINATION);

        League league = leagueService.create(dto);


        // when
        LeagueDto replaceDto = new LeagueDto(
                1L,
                "League",
                "2022-01-01", "2022-03-31",
                2, 1, Playoff.DOUBLE_ELIMINATION);
        leagueService.update(league.getId(), replaceDto);
        League findLeague = leagueRepository.findOne(league.getId());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);

        // then
        Assertions.assertThat(findLeague.getEndDate()).isEqualTo(format.parse("2022-03-31"));
        Assertions.assertThat(findLeague.getMethod().getRoundrobins()).isEqualTo(replaceDto.getRoundRobins());

    }


}