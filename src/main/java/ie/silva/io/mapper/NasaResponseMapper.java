package ie.silva.io.mapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.java.Log;

@Log
@Component
public class NasaResponseMapper {

    private String getTemperaturePathForSol(final String solId) {
        return String.format(
                "$['%s']['AT']['av']",
                solId
        );
    }

    public Map<String, BigDecimal> getSolEntriesFromResponse(final String nasaResponse) {
        if (nasaResponse != null && !nasaResponse.trim().isEmpty()) {
            try {
                final DocumentContext responseContext = JsonPath.parse(nasaResponse);

                final List<String> solIds = responseContext.read("sol_keys");

                if (solIds != null) {
                    final Map<String, BigDecimal> solDtosById = new HashMap<>(solIds.size());
                    solIds.forEach(
                            solId ->
                                    solDtosById.put(
                                            solId,
                                            responseContext.read(getTemperaturePathForSol(solId), BigDecimal.class)
                                    )
                    );

                    return solDtosById;
                }
            } catch (final Exception ex) {
                log.fine(String.format("Empty nasaResponse received: %s", nasaResponse));
                return Collections.emptyMap();
            }
        }

        return Collections.emptyMap();
    }

}
