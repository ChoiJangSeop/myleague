package jangseop.myleague.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Getter
public class Method {

    @Column(nullable = true)
    private int roundrobins;


    @Column(nullable = true)
    private int promotion;

    @Enumerated(value = EnumType.STRING)
    private Playoff playoff;

    //== 생성 메서드 ==//

    public static Method createMethod(int roundrobins, int promotion, Playoff playoff) {
        Method method = new Method();

        method.roundrobins = roundrobins;
        method.promotion = promotion;
        method.playoff = playoff;

        return method;
    }
}
