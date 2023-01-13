package jangseop.myleague.domain.record;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class KnockoutRule {

    @GeneratedValue @Id
    private Long id;

    private int round;
    private int winRound;
    private int lossRound;
}
