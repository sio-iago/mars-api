package ie.silva.io.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ie.silva.io.dto.response.SOLResponseDto;
import ie.silva.io.mock.server.NasaMockServer;

@SpringBootTest(
        // Disabling caching for testing offline scenarios
        properties = {"nasa.dao.cache.expiration.seconds=0"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class NasaResourceTest {

    @Autowired
    private NasaMockServer mockServer;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        mockServer.start();
    }

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    void testShouldReturnAverageIfCalledWithNoSOL() {
        final ResponseEntity<SOLResponseDto> response = restTemplate
                .getForEntity("/nasa/temperature", SOLResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Should be OK");
        assertNotNull(response.getBody(), "Body should have a response");
        assertNotNull(
                response.getBody().getAverage(),
                "Temperature response should not be null"
        );
    }

    @Test
    void testShouldReturnCorrectAverageForSOL410() {
        final ResponseEntity<SOLResponseDto> response = restTemplate
                .getForEntity("/nasa/temperature?SOL=410", SOLResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Should be OK");
        assertNotNull(response.getBody(), "Body should have a response");

        // We should use string here because the floating point will give us an error
        assertEquals(
                new BigDecimal("-68.223"),
                response.getBody().getAverage(),
                "SOL410 temperature should match expected"
        );
    }

    @Test
    void testShouldReturnNotFoundIfInvalidOrInexistentSOL() {
        final ResponseEntity<SOLResponseDto> response = restTemplate
                .getForEntity("/nasa/temperature?SOL=-1", SOLResponseDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return NOT_FOUND");
    }

    @Test
    void testShouldReturnNotFoundIfNasaAPIIsOffline() {
        mockServer.errorMode();

        final ResponseEntity<SOLResponseDto> response = restTemplate
                .getForEntity("/nasa/temperature", SOLResponseDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return NOT_FOUND");
    }

    @Test
    void testShouldReturnBadRequestIfNonNumericSOLProvided() {
        mockServer.errorMode();

        final ResponseEntity<SOLResponseDto> response = restTemplate
                .getForEntity("/nasa/temperature?SOL=non-numeric", SOLResponseDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should return BAD_REQUEST");
    }
}
