package ie.silva.io.mock.server;

import java.io.InputStreamReader;
import java.io.Reader;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import lombok.extern.java.Log;

@Log
@Component
public class NasaMockServer {

    @Value("${mock.server.port}")
    private int mockServerPort;

    @Value("${nasa.api.mars.insight.route}")
    private String marsSolEndpointRoute;

    @Value("${nasa.api.key}")
    private String apiKey;

    @Value("classpath:nasa-mock-response.json")
    private Resource nasaMockResponse;

    private ClientAndServer mockServer;

    /**
     * Starts a new Nasa mock server on the configured port (under the test application.properties).
     */
    public void start() {
        log.info("Starting Nasa Mock Server");
        mockServer = ClientAndServer.startClientAndServer(mockServerPort);

        setupMarsSolEndpoint();
    }

    /**
     * Resets the running Nasa mock server. Ideal for testing negative and/or multiple scenarios.
     */
    public void reset() {
        log.info("Resetting Mock Server");
        mockServer.reset();
    }

    public void errorMode() {
        reset();
        log.info("Starting Nasa Mock Server in Error mode...");

        setupErrorScenarioForMarsSolEndpoint();
    }

    /**
     * Stops the Nasa Mock server for testing.
     */
    public void stop() {
        log.info("Stopping Nasa mock server");
        mockServer.stop();
    }

    private String getMockResponseBody() {
        try (final Reader reader = new InputStreamReader(nasaMockResponse.getInputStream())) {
            return FileCopyUtils.copyToString(reader);
        } catch (final Exception e) {
            return null;
        }
    }

    private void setupMarsSolEndpoint() {
        mockServer
                .when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/" + marsSolEndpointRoute + "/")
                                .withQueryStringParameter("api_key", apiKey)
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withBody(getMockResponseBody())
                );
    }

    private void setupErrorScenarioForMarsSolEndpoint() {
        mockServer
                .when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/" + marsSolEndpointRoute + "/")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(500)
                                .withBody("")
                );
    }
}
