package ru.netology.cardtransfer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import ru.netology.dto.AmountDTO;
import ru.netology.dto.OperationDTO;
import ru.netology.dto.ResponseTransferDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CardTransferApplicationTests {

    private final static String HOST = "http://localhost:";

    @Autowired
    TestRestTemplate restTemplate;
    public static GenericContainer<?> app = new GenericContainer<>("money_transfer_service").withExposedPorts(5500);

    @BeforeAll
    public static void setUp() {
        app.start();
    }

    @Test
    void contextLoads() {
        OperationDTO operationDTO = new OperationDTO("1111111111111111", "11/21", "111", "2222222222222222",
                new AmountDTO("RUR", 100));

        ResponseEntity<ResponseTransferDTO> response = restTemplate.postForEntity(HOST + app.getMappedPort(5500) + "/transfer",
                operationDTO, ResponseTransferDTO.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getOperationId(), notNullValue());

//        ResponseTransferDTO entity =
//                restTemplate.postForObject(HOST + app.getMappedPort(5500) + "/transfer", operationDTO, ResponseTransferDTO.class);
//
//        assertThat(response.getBody().getOperationId(), notNullValue());
    }
}
