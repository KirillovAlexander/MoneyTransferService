package ru.netology.service.commission;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CommissionService {

    @Value("${commission.pct}")
    private double pctAsDouble;
    @Value("${rounding.scale}")
    private int SCALE;
    @Value("${rounding.mode}")
    private RoundingMode ROUNDING_MODE;
    private BigDecimal pct;

    public CommissionService() {
    }

    @PostConstruct
    private void init() {
        this.pct = new BigDecimal(pctAsDouble).setScale(SCALE, ROUNDING_MODE);
    }

    public BigDecimal getPct() {
        return pct;
    }
}
