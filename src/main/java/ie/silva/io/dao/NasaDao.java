package ie.silva.io.dao;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ie.silva.io.dao.exception.DaoException;
import ie.silva.io.mapper.NasaResponseMapper;

@Component
public class NasaDao {

    @Value("${nasa.api.key}")
    private String nasaApiKey;

    @Value("${nasa.api.url}")
    private String nasaApiBaseUrl;

    @Value("${nasa.api.mars.insight.route}")
    private String marsSolInsightRoute;

    @Value("${nasa.dao.cache.expiration.seconds:30}")
    private int cacheExpirationInSecods;

    @Autowired
    private NasaResponseMapper nasaResponseMapper;

    private Map<String, BigDecimal> responseCache;

    private String getRequestUrlFromRoute(final String route) {
        return String.format(
                "%s/%s/?api_key=%s",
                nasaApiBaseUrl,
                route,
                nasaApiKey
        );
    }

    private String getMarsSolInsightUrl() {
        return getRequestUrlFromRoute(marsSolInsightRoute)
                .concat("&feedtype=json&ver=1.0");
    }

    private Map<String, BigDecimal> fetchAllSolAverageTemperaturesFromNasa() throws DaoException {
        final RestTemplate restTemplate = new RestTemplate();

        final String solResponse;
        try {
            solResponse = restTemplate.getForObject(getMarsSolInsightUrl(), String.class);
        } catch (final Exception ex) {
            throw new DaoException("3rd.party.api.offline");
        }

        return nasaResponseMapper.getSolEntriesFromResponse(solResponse);
    }

    private Map<String, BigDecimal> getResponseCache() {
        if (responseCache == null) {
            responseCache = new PassiveExpiringMap<>(cacheExpirationInSecods, TimeUnit.SECONDS);
        }

        return responseCache;
    }

    /**
     * Fetches the latest SOL average temperatures from Mars.
     * This method may cache the response from Nasa.
     *
     * @return Map of solId and average temperature
     * @throws DaoException if not cached and Nasa service replies with an error
     */
    public Map<String, BigDecimal> fetchAllSolAverageTemperatures() throws DaoException {
        Map<String, BigDecimal> cache = getResponseCache();

        if (cache.isEmpty()) {
            final Map<String, BigDecimal> nasaResponse = fetchAllSolAverageTemperaturesFromNasa();
            cache.putAll(nasaResponse);

            // Return the actual response after caching it, because if cacheExpirationInSeconds = 0 we will never cache
            return nasaResponse;
        }

        return cache;
    }
}
