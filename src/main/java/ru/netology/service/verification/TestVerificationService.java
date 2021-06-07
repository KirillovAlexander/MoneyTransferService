package ru.netology.service.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestVerificationService implements VerificationService {
    @Value("${verification.test.value}")
    private String testValue;

    public TestVerificationService() {
    }

    @Override
    public String getNewVerificationCode() {
        return testValue;
    }
}
