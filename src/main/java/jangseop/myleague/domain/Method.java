package jangseop.myleague.domain;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Method {

    private int roundrobins;

    private int promotion;

    @Enumerated(value = EnumType.STRING)
    private Playoff playoff;

    //== business logic =//

    /**
     * create match schedule
     */

    /**
     * create playoff
     */
}
