package ie.silva.io.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ie.silva.io.dao.exception.DaoException;
import ie.silva.io.mock.server.NasaMockServer;

@SpringBootTest(
        properties = {"nasa.dao.cache.expiration.seconds=5"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class NasaDaoTest {

    @Autowired
    private NasaDao nasaDao;

    @Autowired
    private NasaMockServer mockServer;

    @Test
    public void testShouldRetrieveResponseCacheItAndRetrieveFromCacheOnNextCall() throws DaoException {
        // Fetches to cache
        mockServer.start();
        final Map<String, BigDecimal> responseFromNasa = nasaDao.fetchAllSolAverageTemperatures();
        assertNotNull(responseFromNasa, "Should always receive a response.");

        // Stop mock server and verify it's cached
        mockServer.stop();
        final Map<String, BigDecimal> responseFromCache = nasaDao.fetchAllSolAverageTemperatures();

        assertEquals(responseFromNasa, responseFromCache, "Response should've been cached.");
    }

    @Test
    public void testAfterExpirationCacheShouldBeExpiredAndDaoExceptionThrown() throws Exception {
        assertThrows(DaoException.class, () -> {
            mockServer.start();
            nasaDao.fetchAllSolAverageTemperatures();

            mockServer.stop();

            Thread.sleep(5100);

            nasaDao.fetchAllSolAverageTemperatures();
        });
    }
}
