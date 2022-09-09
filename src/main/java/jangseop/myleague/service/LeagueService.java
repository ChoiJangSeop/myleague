package jangseop.myleague.service;

import jangseop.myleague.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LeagueService {

    private final LeagueRepository leagueRepository;

}
