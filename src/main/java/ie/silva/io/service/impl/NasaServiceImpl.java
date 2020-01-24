package ie.silva.io.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.silva.io.dao.NasaDao;
import ie.silva.io.dao.exception.DaoException;
import ie.silva.io.dto.response.SOLResponseDto;
import ie.silva.io.service.NasaService;
import ie.silva.io.service.exception.ServiceException;
import lombok.extern.java.Log;

@Log
@Service
public class NasaServiceImpl implements NasaService {

    @Autowired
    private NasaDao nasaDao;

    @Override
    public SOLResponseDto fetchSolAverageTemperature(final Integer solId) throws ServiceException {

        final Map<String, BigDecimal> solEntriesById;
        try {
            solEntriesById = nasaDao.fetchAllSolAverageTemperatures();
        } catch (final DaoException ex) {
            log.warning("Nasa API seems to be offline!");
            throw new ServiceException(ex.getMessage());
        }

        if (solEntriesById.isEmpty()) {
            log.warning("Nasa API did not return the expected data.");
            throw new ServiceException("no.data.available");
        }

        SOLResponseDto solResponseDto = new SOLResponseDto();
        if (solId == null) {
            final BigDecimal temperatureSum = solEntriesById
                    .values()
                    .stream()
                    .reduce(new BigDecimal(0.0), BigDecimal::add);

            final BigDecimal averageTemperature = temperatureSum.divide(
                    new BigDecimal(solEntriesById.size()),
                    BigDecimal.ROUND_HALF_UP
            );

            solResponseDto.setAverage(averageTemperature);
        } else {
            try {
                final BigDecimal solAverageTemperature = solEntriesById.getOrDefault(String.valueOf(solId), null);

                if (solAverageTemperature == null) {
                    log.fine("Attempted to get average temperature with invalid solId: " + solId);
                    throw new ServiceException("sol.not.found");
                }

                solResponseDto.setAverage(solAverageTemperature);
            } catch (final NumberFormatException ex) {
                log.fine("Attempted to get average temperature with non numeric solId.");
                throw new ServiceException("invalid.sol.id");
            }
        }

        return solResponseDto;
    }
}
