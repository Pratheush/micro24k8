package com.mylearning.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WireMockDemoTests {
    private static WireMockServer wireMockServer;

    //@LocalServerPort
    private final Integer port=7070;

    @BeforeAll
    public static void setup() {
        // Create a WireMock server instance
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(7070));
        // Start the WireMock server
        wireMockServer.start();
    }

    @AfterAll
    public static void teardown() {
        // Stop the WireMock server
        wireMockServer.stop();
    }

    // Rest-Assured is configured with the base URI and the port to connect to the WireMock-URI which runs on localhost and port 7070
    @BeforeEach
    void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void test1() throws IOException {
        // web service is then stubbed:
        WireMock.configureFor("localhost", 7070);
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/baeldung"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("Welcome to Baeldung!")));

        // use of the Apache HttpClient API to represent a client connecting to the server
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //A request is executed, and a response is returned afterwards:
        HttpGet request = new HttpGet("http://localhost:7070/baeldung");
        HttpResponse httpResponse = httpClient.execute(request);

        // convert the httpResponse variable to a String using a helper method:
        String responseString = convertResponseToString(httpResponse);

        // // Verify the request
        WireMock.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/baeldung")));
        assertEquals("Welcome to Baeldung!", responseString);

        // using rest-assured Library to make http rest calls
        var restResponse=RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/baeldung")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body().asString();
        assertEquals("Welcome to Baeldung!",restResponse);
    }

    private String convertResponseToString(HttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(responseStream, "UTF-8");

        // sets the delimiter for the scanner.
        // here The "\\Z" pattern represents the end of the input, so the scanner will treat the entire input as a single token.
        // .next(): This method reads the next token from the input using the specified delimiter.
        // Since we set the delimiter to "\\Z", it will read the entire input until the end.
        String responseString = scanner.useDelimiter("\\Z").next();

        scanner.close();
        return responseString;
    }

    //  URL Matching
    @Test
    void test2() throws IOException {

        WireMock.configureFor("localhost", 7070);
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/baeldung/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("\"testing-library\": \"WireMock\"")));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:7070/baeldung/wiremock");
        HttpResponse httpResponse = httpClient.execute(request);
        String stringResponse = convertHttpResponseToString(httpResponse);

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/baeldung/wiremock")));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
        assertEquals("application/json", httpResponse.getFirstHeader("Content-Type").getValue());
        assertEquals("\"testing-library\": \"WireMock\"", stringResponse);

        // using rest-assured Library to make http rest calls
        var restResponse=RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/baeldung/wiremock")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body().asString();
        assertEquals("\"testing-library\": \"WireMock\"",restResponse);
    }

    // Request Header Matching
    @Test
    void test3() throws Exception {

        WireMock.configureFor("localhost", 7070);
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/baeldung/wiremock"))
                .withHeader("Accept", WireMock.matching("text/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "text/html")
                        .withBody("!!! Service Unavailable !!!")));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:7070/baeldung/wiremock");
        request.addHeader("Accept", "text/html");
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String stringResponse = null;
        try {
            stringResponse = convertHttpResponseToString(httpResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/baeldung/wiremock")));
        assertEquals(503, httpResponse.getStatusLine().getStatusCode());
        assertEquals("text/html", httpResponse.getFirstHeader("Content-Type").getValue());
        assertEquals("!!! Service Unavailable !!!", stringResponse);

        // using rest-assured Library to make http rest calls
        RestAssured.given()
                .header("Accept","text/html")
                .when()
                .get("/baeldung/wiremock")
                .then()
                .log().all()
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .contentType(ContentType.HTML)
                .body(Matchers.equalTo("!!! Service Unavailable !!!"));

    }

    // Request Body Matching
    @Test
    void test4() throws IOException{

        WireMock.configureFor("localhost", 7070);
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/baeldung/wiremock"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(WireMock.containing("\"testing-library\": \"WireMock\""))
                .withRequestBody(WireMock.containing("\"creator\": \"Tom Akehurst\""))
                .withRequestBody(WireMock.containing("\"website\": \"wiremock.org\""))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)));

        InputStream jsonInputStream
                = this.getClass().getClassLoader().getResourceAsStream("wiremock_intro.json");
        String jsonString = convertInputStreamToString(jsonInputStream);
        StringEntity entity = new StringEntity(jsonString);


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost("http://localhost:7070/baeldung/wiremock");
        request.addHeader("Content-Type", "application/json");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/baeldung/wiremock"))
                .withHeader("Content-Type", WireMock.equalTo("application/json")));
        assertEquals(200, response.getStatusLine().getStatusCode());

        File jsonFile = new File("src/test/resources/wiremock_intro.json");

        // using rest-assured Library to make http rest calls
        RestAssured.given()
                .contentType("application/json")
                .body(jsonFile)
                .when()
                .post("/baeldung/wiremock")
                .then()
                .log().all()    // here no contentType is mentioned because response contentType is ""
                .statusCode(HttpStatus.OK.value());
    }


    private HttpResponse generateClientAndReceiveResponseForPriorityTests() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:7070/baeldung/wiremock");
        request.addHeader("Accept", "text/xml");
        return httpClient.execute(request);
    }


    //  Stub Priority
    // The previous subsections deal with situations where an HTTP request matches only a single stub.
    //
    //It’s more complicated if there is more than a match for a request.
    // By default, the most recently added stub will take precedence in such a case
    @Test
    void test5_withoutPriority() throws Exception {

        WireMock.configureFor("localhost", 7070);

        // configure two stubs without consideration of the priority level:
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/baeldung/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)));
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/baeldung/wiremock"))
                .withHeader("Accept", WireMock.matching("text/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(503)));

        // create an HTTP client and execute a request using the helper method:
        HttpResponse httpResponse = generateClientAndReceiveResponseForPriorityTests();

        // The following code snippet verifies that the last configured stub is applied
        // regardless of the one defined before when a request matches both of them:
        WireMock.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/baeldung/wiremock")));
        assertEquals(503, httpResponse.getStatusLine().getStatusCode());

        RestAssured.given()
                .header("Accept","text/xml")
                .when()
                .get("/baeldung/wiremock")
                .then()
                .log().all()
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @Test
    void test6_withPriority() throws Exception {

        WireMock.configureFor("localhost", 7070);

        // configure two stubs with priority levels being set, where a lower number represents a higher priority:
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/baeldung/.*"))
                .atPriority(1)
                .willReturn(WireMock.aResponse()
                        .withStatus(200)));
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/baeldung/wiremock"))
                .atPriority(2)
                .withHeader("Accept", WireMock.matching("text/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(503)));

        // the creation and execution of an HTTP request:
        HttpResponse httpResponse = generateClientAndReceiveResponseForPriorityTests();

        // following code validates the effect of priority levels,
        // where the first configured stub is applied instead of the last:
        WireMock.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/baeldung/wiremock")));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());

        RestAssured.given()
                .header("Accept","text/xml")
                .when()
                .get("/baeldung/wiremock")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());
    }


    private String convertHttpResponseToString(HttpResponse httpResponse) throws IOException {
        InputStream inputStream = httpResponse.getEntity().getContent();
        return convertInputStreamToString(inputStream);
    }

    private String convertInputStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        String string = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return string;
    }
}
