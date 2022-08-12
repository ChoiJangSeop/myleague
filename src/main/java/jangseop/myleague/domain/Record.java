package jangseop.myleague.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Record {

    @Id @GeneratedValue
    @Column(name = "RECORD_ID")
    private Long id;

    private int win;
    private int loss;
    private int setWin;
    private int setLoss;
    private int score;
    private int rank;

    @OneToOne(mappedBy = "record")
    private Participant participant;
}
