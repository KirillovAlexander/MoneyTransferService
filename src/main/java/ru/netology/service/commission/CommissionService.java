package ru.netology.service.commission;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CommissionService {

    @Value("${commission.pct}")
    private BigDecimal pct;

    public CommissionService() {
    }

    public BigDecimal getPct() {
        return pct;
    }
}
