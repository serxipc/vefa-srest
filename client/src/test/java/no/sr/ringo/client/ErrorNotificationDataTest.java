package no.sr.ringo.client;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * User: Adam
 * Date: 4/11/13
 * Time: 9:31 AM
 */
public class ErrorNotificationDataTest {

    @Test
    public void testPasswordIsMaskedWithLongOption() {
        String commandLine = "--username sr --password pass --upload --address https://localhost --outboxPath path";
        ErrorNotificationData end = new ErrorNotificationData(commandLine, null, null);
        assertEquals("--username sr --password ***** --upload --address https://localhost --outboxPath path", end.getCommandLine());
    }

    @Test
    public void testPasswordIsMaskedWithLongOptionAtEnd() {
        String commandLine = "--username sr --upload --address https://localhost --outboxPath path --password pass";
        ErrorNotificationData end = new ErrorNotificationData(commandLine, null, null);
        assertEquals("--username sr --upload --address https://localhost --outboxPath path --password *****", end.getCommandLine());
    }

    @Test
    public void testPasswordIsMaskedWithLongOptionAtBeginning() {
        String commandLine = "  --password   pass   --username sr --upload --address https://localhost --outboxPath path";
        ErrorNotificationData end = new ErrorNotificationData(commandLine, null, null);
        assertEquals("  --password   *****   --username sr --upload --address https://localhost --outboxPath path", end.getCommandLine());
    }

    @Test
    public void testPasswordIsMaskedWithShortOption() {
        String commandLine = "--username sr -p pass --upload --address https://localhost --outboxPath path";
        ErrorNotificationData end = new ErrorNotificationData(commandLine, null, null);
        assertEquals("--username sr -p ***** --upload --address https://localhost --outboxPath path", end.getCommandLine());
    }

    @Test
    public void testPasswordIsMaskedWithShortOptionAtEnd() {
        String commandLine = "--username sr --upload --address https://localhost --outboxPath path --p pass";
        ErrorNotificationData end = new ErrorNotificationData(commandLine, null, null);
        assertEquals("--username sr --upload --address https://localhost --outboxPath path --p *****", end.getCommandLine());
    }

    @Test
    public void testPasswordIsMaskedWithShortOptionAtBeginning() {
        String commandLine = "-p pass --username sr --upload --address https://localhost --outboxPath path";
        ErrorNotificationData end = new ErrorNotificationData(commandLine, null, null);
        assertEquals("-p ***** --username sr --upload --address https://localhost --outboxPath path", end.getCommandLine());
    }

}
