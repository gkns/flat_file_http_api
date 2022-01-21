package com.example.flat_file_http_api.camel;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.flat_file_http_api.camel.routes.FileRoute;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;



@CamelSpringBootTest
@EnableAutoConfiguration
@SpringBootTest(
        properties = { "camel.springboot.name=customName" },
        classes = { FileRoute.class }
)

public class TextRecordToDatabaseQueryProcessorTest {
    @Autowired
    Environment environment;

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:test")
    MockEndpoint mockEndpoint;

    @Autowired
    CamelContext context;

    private String endpointURI;
    private String sampleMsgBody = "2000-01-01T17:25:49Z ddedric_strosin@adams.co.uk"
            + " dfad33e7-f734-4f70-af29-c42f2b467142\n";

    // Have to use BeforeEach instead of BeforeAll becuase we need the -
    // Camel context initialized to get the endpoint of the Route under test (FileRoute)
    @BeforeEach
    public void setUp() throws Exception{
        // Replace the destination Endpoint for testing purposes
        AdviceWith.adviceWith(context, "textFileRecordRouter", a->
                a.weaveByToUri("jdbc:*").replace().to("mock:test"));
        endpointURI = context.getRoute("textFileRecordRouter").getEndpoint().getEndpointUri();
    }

    @AfterEach
    public void tearDown() throws Exception{
        // Replace the destination Endpoint for testing purposes
        AdviceWith.adviceWith(context, "textFileRecordRouter", a->
                a.weaveByToUri("mock:test").replace().to("jdbc:dataSource"));
    }

    @Test
    public void shouldAutowireProducerTemplate() {
        assertNotNull(producerTemplate);
    }

    @Test
    public void shouldInjectEndpoint() throws InterruptedException {
        mockEndpoint.setExpectedMessageCount(1);
        producerTemplate.sendBody(endpointURI, sampleMsgBody);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldTransformBodyAsValidQuery() throws InterruptedException {
        String expectedQuery = "INSERT INTO session_logs (session_id, email, event_time)"
                + " VALUES ('dfad33e7-f734-4f70-af29-c42f2b467142',"
                + " 'ddedric_strosin@adams.co.uk', '2000-01-01 17:25:49')";

        mockEndpoint.setExpectedMessageCount(1);
        producerTemplate.sendBody(endpointURI, sampleMsgBody);
        mockEndpoint.expectedBodiesReceived(expectedQuery);

        mockEndpoint.assertIsSatisfied();

    }

    @Test
    public void shouldNotThrowOnInvalidInputMessage() throws InterruptedException {
        mockEndpoint.setExpectedMessageCount(0);
        producerTemplate.sendBody(endpointURI, "junk");

        mockEndpoint.assertIsSatisfied();

    }


}
