package ie.silva.io.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ie.silva.io.dto.response.SOLResponseDto;
import ie.silva.io.resource.exception.NotFoundException;
import ie.silva.io.resource.exception.RestException;
import ie.silva.io.service.NasaService;
import ie.silva.io.service.exception.ServiceException;
import lombok.extern.java.Log;

@RestController
@RequestMapping("/nasa")
@Log
public class NasaResource {

    @Autowired
    private NasaService nasaService;


    @GetMapping("/temperature")
    public SOLResponseDto getMarsTemperature(@RequestParam(value = "SOL", required = false) final Integer solId)
            throws RestException {

        try {
            return nasaService.fetchSolAverageTemperature(solId);
        } catch (final ServiceException ex) {
            throw new NotFoundException();
        }
    }
}
