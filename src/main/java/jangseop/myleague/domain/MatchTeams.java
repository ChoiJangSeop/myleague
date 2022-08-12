package jangseop.myleague.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MatchTeams {

    private Participant home;
    private Participant away;
}
