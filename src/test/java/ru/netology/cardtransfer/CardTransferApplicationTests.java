package ru.netology.cardtransfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.testcontainers.containers.GenericContainer;
import ru.netology.cardtransfer.dto.ConfirmTestDTO;
import ru.netology.dto.AmountDTO;
import ru.netology.dto.OperationDTO;
import ru.netology.dto.ResponseTransferDTO;
import ru.netology.service.verification.VerificationService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CardTransferApplicationTests {

    private final static String HOST = "http://localhost:";
    private final static int PORT = 5500;
    private static String operationId;
    private final static HttpHeaders headers = new HttpHeaders();

    static {
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    VerificationService verificationService;

    public static GenericContainer<?> app = new GenericContainer<>("money_transfer_service").withExposedPorts(PORT);

    @BeforeAll
    public static void setUp() {
        app.start();
    }

    @Test
    void givenNewOperation_whenTransfer_thenResponseStatusOkAndOperationIdIsNotNull() {
        OperationDTO operationDTO = new OperationDTO("1111111111111111", "11/21", "111", "2222222222222222",
                new AmountDTO("RUR", 100L));

        Gson gson = new GsonBuilder().create();
        String jsonObject = gson.toJson(operationDTO);
        HttpEntity<String> request = new HttpEntity<>(jsonObject, headers);

        ResponseEntity<ResponseTransferDTO> response = restTemplate.postForEntity(HOST + app.getMappedPort(PORT) + "/transfer",
                request, ResponseTransferDTO.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getOperationId(), notNullValue());

        operationId = response.getBody().getOperationId().toString();
    }

    @Test
    @AfterTestMethod("givenNewOperation_whenTransfer_thenResponseStatusOkAndOperationIdIsNotNull")
    public void givenOperationId_whenConfirmStatus_thenResponseStatusOkAndOperationIdIsNotNull() {
        ConfirmTestDTO confirmTestDTO = new ConfirmTestDTO(operationId, verificationService.getNewVerificationCode());
        Gson gson = new GsonBuilder().create();
        String jsonObject = gson.toJson(confirmTestDTO);
        HttpEntity<String> request = new HttpEntity<>(jsonObject, headers);

        ResponseEntity<ResponseTransferDTO> response = restTemplate.postForEntity(HOST + app.getMappedPort(PORT) + "/confirmOperation",
                request, ResponseTransferDTO.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getOperationId().toString(), is(equalTo(operationId)));
    }
}
