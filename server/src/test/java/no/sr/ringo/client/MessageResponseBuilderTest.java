/* Created by steinar on 02.01.12 at 14:32 */
package no.sr.ringo.client;

import no.sr.ringo.common.PeppolMessageTestdataGenerator;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.message.MessageWithLocationsImpl;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.resource.OutboxResource;
import no.sr.ringo.response.*;
import no.sr.ringo.response.xml.XmlResponseParserImpl;
import org.easymock.EasyMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.UriBuilder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * Generates sample XML responses as they would have been returned from the Ringo server, parses them into
 * their corresponding Java objects and verifies that all required information have flowed from XML to the
 * generated Java objects.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class MessageResponseBuilderTest {

    private static Logger log = LoggerFactory.getLogger(MessageResponseBuilderTest.class);
    
    private MessageListRingoResponseHandler messageListResponseHandler;
    private InboxRingoResponseHandler inboxResponseHandler;
    private OutboxRingoResponseHandler outboxResponseHandler;
    private RingoService mockRingoService;
    private Navigation navigation;

    @BeforeMethod
    protected void setUp() throws Exception {
        URI next = new URI("https://localhost:8080/outbox?startIndex=3");
        URI previous = new URI("https://localhost:8080/outbox?startIndex=1");
        navigation = new Navigation(previous, next);

        //we need to mock ringo because of navigation links
        mockRingoService = EasyMock.createStrictMock(RingoService.class);

        messageListResponseHandler = new MessageListRingoResponseHandler(mockRingoService);
        expect(mockRingoService.next(navigation, messageListResponseHandler)).andStubReturn(null);
        replay(mockRingoService);

        inboxResponseHandler = new InboxRingoResponseHandler(mockRingoService);
        outboxResponseHandler = new OutboxRingoResponseHandler(mockRingoService);
    }

    @Test
    public void outboxPostResponse() throws MalformedURLException {

        OutboxPostResponse messageResponse = sampleSingleMessageResponse();

        String xml = messageResponse.asXml();

        MessageListRingoResponseHandler parser = messageListResponseHandler;
        XmlResponseParserImpl formatter = new XmlResponseParserImpl(parser);

        byte[] bytes = xml.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Messages messages1 = formatter.parse(bais);

        final Iterator<Message> iterator = messages1.iterator();

        assertEquals(iterator.hasNext(), true);
        final Message message = iterator.next();
        assertEquals(messageResponse.getMessage().getReceived().compareTo(message.getContents().getReceived()),0 );
        assertNotNull(message);
        assertEquals(iterator.hasNext(), false);
    }

    /** Generates some test data */
    private OutboxPostResponse sampleSingleMessageResponse() {
        MessageWithLocationsImpl outbound = PeppolMessageTestdataGenerator.outboundMesssageNotSent();
        return new OutboxPostResponse(outbound);
    }

    @Test
    public void uriBuilder() throws URISyntaxException {
        UriBuilder uriBuilder = UriBuilder.fromUri(new URI("https://localhost:8080/x/z"));
        UriBuilder b = UriBuilder.fromResource(OutboxResource.class);
        log.debug(b.build().toString());
    }

    @Test
    public void singleOutboxMessageNotSent() {
        OutboxQueryResponse outboxQueryResponse = PeppolMessageTestdataGenerator.outboxQueryResponseWithOneMessageNotSent();

        XmlResponseParserImpl xmlResponseParser = new XmlResponseParserImpl(outboxResponseHandler);
        Messages messages1 = xmlResponseParser.parse(inputStreamFromMessageResponse(outboxQueryResponse));

        final Iterator<Message> iterator = messages1.iterator();

        assertEquals(iterator.hasNext(), true);
        assertNotNull(iterator.next());
        assertEquals(iterator.hasNext(), false);
    }



    /**
     * Ensure that setting the navigation link, still works. I.e. we currently don't support navigation on
     * outbox entries.
     */
    @Test
    public void outboxQueryResponseWithNavigation() throws Exception {
        OutboxQueryResponse outboxQueryResponse = PeppolMessageTestdataGenerator.outboxQueryResponseWithOneMessageNotSent();

        // Adds the navigation link, pretending that we have a response with more than a single message.
        outboxQueryResponse.setNavigation(navigation);

        XmlResponseParserImpl xmlResponseParser = new XmlResponseParserImpl(outboxResponseHandler);
        Messages messages1 = xmlResponseParser.parse(inputStreamFromMessageResponse(outboxQueryResponse));

        final Iterator<Message> iterator = messages1.iterator();

        assertEquals(iterator.hasNext(), true);
        assertNotNull(iterator.next());
        assertEquals(iterator.hasNext(), false);
    }


    @Test
    public void outboxMessageSentResponse() {
        OutboxQueryResponse outboxQueryResponse = PeppolMessageTestdataGenerator.outboxQueryResponseWithOneMessageSent();

        XmlResponseParserImpl xmlResponseParser = new XmlResponseParserImpl(outboxResponseHandler);
        Messages messages1 = xmlResponseParser.parse(inputStreamFromMessageResponse(outboxQueryResponse));

        final Iterator<Message> iterator = messages1.iterator();
        Message message = iterator.next();

        assertNotNull(message.getContents().getDelivered());

    }

    @Test
    public void singleInboxMessage() {
        List<MessageWithLocations> list = new ArrayList<MessageWithLocations>();
        list.add(PeppolMessageTestdataGenerator.inBoundMesssageNotSent());

        InboxQueryResponse inboxQueryResponse = new InboxQueryResponse(list);

        XmlResponseParserImpl xmlResponseParser = new XmlResponseParserImpl(inboxResponseHandler);
        Messages messages1 = xmlResponseParser.parse(inputStreamFromMessageResponse(inboxQueryResponse));

        final Iterator<Message> iterator = messages1.iterator();

        assertEquals(iterator.hasNext(), true);
        assertNotNull(iterator.next());
        assertEquals(iterator.hasNext(), false);
    }

    @Test
    public void inboxQueryResponse() throws Exception {
        List<MessageWithLocations> list = new ArrayList<MessageWithLocations>();
        list.add(PeppolMessageTestdataGenerator.inBoundMesssageNotSent());

        InboxQueryResponse messageListResponse = new InboxQueryResponse(list);

        URI next = new URI("https://localhost:8080/inbox?startIndex=100");
        URI previous = new URI("https://localhost:8080/inbox?startIndex=49");
        Navigation navigation = new Navigation(previous, next);

        messageListResponse.setNavigation(navigation);

        XmlResponseParserImpl xmlResponseParser = new XmlResponseParserImpl(inboxResponseHandler);
        Messages messages1 = xmlResponseParser.parse(inputStreamFromMessageResponse(messageListResponse));

        final Iterator<Message> iterator = messages1.iterator();

        assertEquals(iterator.hasNext(), true);
        assertNotNull(iterator.next());
        assertEquals(iterator.hasNext(), false);

    }

    @Test
    public void messagesQueryResponse() throws MalformedURLException, URISyntaxException {

        List<MessageWithLocations> list = new ArrayList<MessageWithLocations>();
        list.add(PeppolMessageTestdataGenerator.inBoundMesssageNotSent());
        list.add(PeppolMessageTestdataGenerator.outboundMesssageNotSent());

        MessagesQueryResponse messagesQueryResponse = new MessagesQueryResponse(list);
        messagesQueryResponse.setNavigation(navigation);

        // Parses the generated test XML response into a proper object
        XmlResponseParserImpl xmlResponseParser = new XmlResponseParserImpl(messageListResponseHandler);
        Messages messages1 = xmlResponseParser.parse(inputStreamFromMessageResponse(messagesQueryResponse));

        final Iterator<Message> iterator = messages1.iterator();

        assertEquals(iterator.hasNext(), true);
        assertNotNull(iterator.next());
        assertEquals(iterator.hasNext(), true);
        assertNotNull(iterator.next());
        assertEquals(iterator.hasNext(), false);
    }

    @Test
    public void messagesQueryWithNavigationResponse() throws MalformedURLException, URISyntaxException {

        List<MessageWithLocations> list = new ArrayList<MessageWithLocations>();
        list.add(PeppolMessageTestdataGenerator.inBoundMesssageNotSent());

        MessagesQueryResponse messagesQueryResponse = new MessagesQueryResponse(list);


        messagesQueryResponse.setNavigation(navigation);

        reset(mockRingoService);
        //first time return some more messages
        MessageListRingoResponseHandler parser = messageListResponseHandler;
        expect(mockRingoService.next(navigation, messageListResponseHandler)).andReturn(new Messages(mockRingoService, messagesQueryResponse, messageListResponseHandler));
        //second time no more messages
        expect(mockRingoService.next(navigation, messageListResponseHandler)).andReturn(null);
        replay(mockRingoService);

        XmlResponseParserImpl formatter = new XmlResponseParserImpl(parser);

        Messages messages1 = formatter.parse(inputStreamFromMessageResponse(messagesQueryResponse));

        final Iterator<Message> iterator = messages1.iterator();

        assertEquals(iterator.hasNext(), true);
        assertNotNull(iterator.next());
        assertEquals(iterator.hasNext(), true);
        assertNotNull(iterator.next());
        assertEquals(iterator.hasNext(), false);
        
        verify(mockRingoService);
    }


    @Test
    /**
     * Tests that peppol header contains both old and new data for documentType and process
     */
    public void testMessagePeppolHeaderTest() {
        OutboxPostResponse messageResponse = sampleSingleMessageResponse();

        String xml = messageResponse.asXml();
        assertNotNull(xml);
        String expectedHeader =
                "          <peppol-header>\n" +
                "            <sender>9908:976098897</sender>\n" +
                "            <receiver>9908:976098897</receiver>\n" +
                "            <channel>TEST</channel>\n" +
                "            <document-id>" + PeppolDocumentTypeId.EHF_INVOICE.stringValue()+ "</document-id>\n" +
                "            <process-id>urn:www.cenbii.eu:profile:bii04:ver1.0</process-id>\n" +
                "          </peppol-header>";
        assertTrue(xml.contains(expectedHeader), "Ooops, sampleResponse: " + xml);
    }

    /**
     * Helper method which will provide an InputStream from the supplied message query response.
     *
     * @param messagesQueryResponse
     * @return
     */
    private InputStream inputStreamFromMessageResponse(MessagesQueryResponse messagesQueryResponse) {
        String xml = messagesQueryResponse.asXml();
        log.debug(xml);

        byte[] bytes = xml.getBytes();
        return new ByteArrayInputStream(bytes);
    }

}
