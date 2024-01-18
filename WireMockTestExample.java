package kz.spring.example;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Сервер заглушка, который может подменять запросы по указанному порту
 */
@WireMockTest(httpPort = 8080)
public class WireMockTestExample {
    
    @Test
    public void test() throws IOException, InterruptedException {
        stubFor(get(urlPathMatching("/baeldung/.*")).willReturn(
                aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody("Hello, Body")
        ));

        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/baeldung/test"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Answer body: " + response.body());

        /*
         * 15:44:07.426 [main] INFO org.eclipse.jetty.server.Server -- jetty-11.0.17; built: 2023-10-09T18:39:14.424Z; git: 48e7716b9462bebea6732b885dbebb4300787a5c; jvm 17.0.9+8-LTS
         * 15:44:07.449 [main] INFO org.eclipse.jetty.server.handler.ContextHandler -- Started o.e.j.s.ServletContextHandler@4aeaadc1{/__admin,null,AVAILABLE}
         * 15:44:07.457 [main] INFO org.eclipse.jetty.server.handler.ContextHandler.ROOT -- RequestHandlerClass from context returned com.github.tomakehurst.wiremock.http.StubRequestHandler. Normalized mapped under returned 'null'
         * 15:44:07.458 [main] INFO org.eclipse.jetty.server.handler.ContextHandler -- Started o.e.j.s.ServletContextHandler@28c88600{/,null,AVAILABLE}
         * 15:44:07.466 [main] INFO org.eclipse.jetty.server.AbstractConnector -- Started NetworkTrafficServerConnector@10993713{HTTP/1.1, (http/1.1, h2c)}{0.0.0.0:8080}
         * 15:44:07.474 [main] INFO org.eclipse.jetty.server.Server -- Started Server@737edcfa{STARTING}[11.0.17,sto=1000] @1004ms
         * Answer: Hello, Body
         * 15:44:07.738 [main] INFO org.eclipse.jetty.server.Server -- Stopped Server@737edcfa{STOPPING}[11.0.17,sto=1000]
         * 15:44:07.738 [main] INFO org.eclipse.jetty.server.Server -- Shutdown Server@737edcfa{STOPPING}[11.0.17,sto=1000]
         * 15:44:07.741 [main] INFO org.eclipse.jetty.server.AbstractConnector -- Stopped NetworkTrafficServerConnector@10993713{HTTP/1.1, (http/1.1, h2c)}{0.0.0.0:8080}
         * 15:44:07.742 [main] INFO org.eclipse.jetty.server.handler.ContextHandler -- Stopped o.e.j.s.ServletContextHandler@28c88600{/,null,STOPPED}
         * 15:44:07.743 [main] INFO org.eclipse.jetty.server.handler.ContextHandler -- Stopped o.e.j.s.ServletContextHandler@4aeaadc1{/__admin,null,STOPPED}
         */
    }
}
