package jangseop.myleague.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
public class League {

    @Id
    @GeneratedValue
    @Column(name = "LEAGUE_ID")
    private Long id;

    private Date startedDate;

    private Date endDate;

    @OneToMany(mappedBy = "league")
    private List<Participant> participants = new ArrayList<>();

//    @Embedded
//    private Method method;

}
