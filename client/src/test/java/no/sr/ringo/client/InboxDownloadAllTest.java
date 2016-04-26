package no.sr.ringo.client;

import no.sr.ringo.message.MessageMetaDataImpl;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.message.MessageWithLocationsImpl;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.peppol.SchemeId;
import no.sr.ringo.response.MessageListRingoResponseHandler;
import no.sr.ringo.response.MessagesQueryResponse;
import no.sr.ringo.standalone.executor.CommandLineExecutorException;
import no.sr.ringo.standalone.executor.RingoClientCommandExecutor;
import no.sr.ringo.standalone.executor.RingoClientCommandExecutorTest;
import no.sr.ringo.standalone.parser.RingoClientParams;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.*;

/**
 * Tests that if there are more than 25 messages all the messages will be downloaded.
 *
 * User: andy
 * Date: 3/6/12
 * Time: 4:20 PM
 */
public class InboxDownloadAllTest {


    private PrintStream mockStream;
    private List<Object> mocks;
    private RingoService mockRingoService;
    private MessageWithLocations messageWithLocation;
    private String uuid;

    @BeforeMethod
    public void setUp() throws Exception {
        mocks = new ArrayList<Object>();
        mockStream = EasyMock.createStrictMock(PrintStream.class);
        mockRingoService = EasyMock.createStrictMock(RingoService.class);

        MessageMetaDataImpl messageMetaData = new MessageMetaDataImpl();
        PeppolHeader peppolHeader = new PeppolHeader();
        peppolHeader.setReceiver(new PeppolParticipantId(SchemeId.NO_ORGNR,"976098897"));
        messageMetaData.setPeppolHeader(peppolHeader);
        uuid = UUID.randomUUID().toString();
        messageMetaData.setUuid(uuid);
        messageWithLocation = new MessageWithLocationsImpl(messageMetaData, new URI("self"), new URI("xmlDoc"));

        mocks.add(mockStream);
        mocks.add(mockRingoService);
    }

    @Test
    /*
    * Tests that if there are only messages which cause errors left in the inbox the loop will exit
    */
    public void testBatchIteration() throws IOException, CommandLineExecutorException {
        RingoClientParams params = RingoClientCommandExecutorTest.prepareParamsForInbox();

        RingoClient ringoClient = new RingoClientImpl(mockRingoService);

        List<MessageWithLocations> messagesWithLocations = new ArrayList<MessageWithLocations>();
        messagesWithLocations.add(messageWithLocation);

        final MessageListRingoResponseHandler messagesResponseHandler = new MessageListRingoResponseHandler(mockRingoService);
        Messages messages = new Messages(mockRingoService,new MessagesQueryResponse(messagesWithLocations), messagesResponseHandler);

        //expectations for the service

        //first we see there are 35 messages
        //download the message and mark them as read
        expect(mockRingoService.count(isA(Inbox.class))).andReturn(35);
        expect(mockRingoService.messages(isA(Inbox.class), isA(MessageListRingoResponseHandler.class))).andReturn(messages);
        mockRingoService.downloadMessage(isA(MessageWithLocations.class), isA(FileOutputStream.class));
        expectLastCall();
        expect(mockRingoService.markAsRead(messageWithLocation)).andReturn(Boolean.TRUE);

        //now we check again there is now only one message
        expect(mockRingoService.count(isA(Inbox.class))).andReturn(1);
        expect(mockRingoService.messages(isA(Inbox.class), isA(MessageListRingoResponseHandler.class))).andReturn(messages);
        mockRingoService.downloadMessage(isA(MessageWithLocations.class), isA(FileOutputStream.class));
        expectLastCall();
        expect(mockRingoService.markAsRead(messageWithLocation)).andReturn(Boolean.TRUE);

        //check for the last time and now there are 0 messages the test is complete
        expect(mockRingoService.count(isA(Inbox.class))).andReturn(0);

        //expectations for the print stream
        mockStream.println("Downloading message with UUID: "+uuid);
        mockStream.println("Downloading message with UUID: "+uuid);

        Path path = Paths.get(FileSystems.getDefault().getSeparator(),"tmp", "download");
        String s = path.toString();


        mockStream.println("Downloaded 2 files to directory " + params.getInboxPath());
        replay(mocks.toArray());

        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, ringoClient);
        executor.execute();

        verify(mocks.toArray());

    }


    @Test
    /*
     * Tests that if there are errors then the loop will exit
     */
    @SuppressWarnings("unchecked")
    public void testBatchIterationWithErrors() throws IOException, CommandLineExecutorException {
        RingoClientParams params = RingoClientCommandExecutorTest.prepareParamsForInbox();

        RingoClient ringoClient = new RingoClientImpl(mockRingoService);

        List<MessageWithLocations> messagesWithLocations = new ArrayList<MessageWithLocations>();
        messagesWithLocations.add(messageWithLocation);
        final MessageListRingoResponseHandler messagesResponseHandler = new MessageListRingoResponseHandler(mockRingoService);
        Messages messages = new Messages(mockRingoService,new MessagesQueryResponse(messagesWithLocations), messagesResponseHandler);

        //expectations for the service

        //first we see there are 1 messages
        //download the message and mark them as read
        expect(mockRingoService.count(isA(Inbox.class))).andReturn(1);
        expect(mockRingoService.messages(isA(Inbox.class), isA(MessageListRingoResponseHandler.class))).andReturn(messages);
        mockRingoService.downloadMessage(isA(MessageWithLocations.class), isA(FileOutputStream.class));
        expectLastCall();
        //marking as read fails
        expect(mockRingoService.markAsRead(messageWithLocation)).andReturn(Boolean.FALSE);

        //message which failed to be marked as read is still in the inbox
        //so there is 1 message, which should cause the loop to exit.
        expect(mockRingoService.count(isA(Inbox.class))).andReturn(1);

        //expectations for the print stream
        mockStream.println("Downloading message with UUID: "+uuid);
        mockStream.println(String.format("Message with UUID %s successfully downloaded, but marking as read failed.", uuid));
        mockStream.println("Downloaded 0 files to directory " + params.getInboxPath());
        replay(mocks.toArray());

        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, ringoClient);
        executor.execute();

        verify(mocks.toArray());

    }

}
