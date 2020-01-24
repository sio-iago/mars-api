package ie.silva.io.service;

import ie.silva.io.dto.response.SOLResponseDto;
import ie.silva.io.service.exception.ServiceException;

public interface NasaService {

    SOLResponseDto fetchSolAverageTemperature(final Integer solId) throws ServiceException;
}
