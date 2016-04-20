package no.sr.ringo.standalone;

import no.sr.ringo.client.ClientErrorNotifier;
import no.sr.ringo.client.ErrorNotificationData;
import no.sr.ringo.standalone.parser.RingoClientConnectionParams;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Permission;

import static org.easymock.EasyMock.*;

/**
 * User: Adam
 * Date: 4/10/13
 * Time: 9:22 AM
 */
public class RingoClientMainTest {


    //Do not allow main class to do System.exit()
    @BeforeMethod
    public void setUp() throws Exception {
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @AfterMethod
    public void tearDown() throws Exception {
        System.setSecurityManager(null);
    }

    @Test(expectedExceptions = SecurityException.class)
    public void testNotifierCalled() {

        ClientErrorNotifier mockErrorNotifier = createMock(ClientErrorNotifier.class);

        String[] args = {"-u", "adam", "-p", "superSecretPassword", "-a", "http://ringo.domain.com", "-d", "-i", "/tmp/download", "-r", "RecipientId"};

        RingoClientMain.injectErrorNotifier(mockErrorNotifier);
        mockErrorNotifier.sendErrorNotification(isA(ErrorNotificationData.class), isA(RingoClientConnectionParams.class));
        expectLastCall();
        replay(mockErrorNotifier);

        RingoClientMain.main(args);
        verify(mockErrorNotifier);

    }

    @Test(expectedExceptions = SecurityException.class)
    public void testApplicationContinuesWhenErrorNotifierThrowsAnException() {

        ClientErrorNotifier mockErrorNotifier = createMock(ClientErrorNotifier.class);

        String[] args = {"-u", "adam", "-p", "superSecretPassword", "-a", "http://ringo.domain.com", "-d", "-i", "/tmp/download", "-r", "RecipientId"};

        RingoClientMain.injectErrorNotifier(mockErrorNotifier);
        mockErrorNotifier.sendErrorNotification(isA(ErrorNotificationData.class), isA(RingoClientConnectionParams.class));
        expectLastCall().andThrow(new IllegalStateException("Simulating exception"));
        replay(mockErrorNotifier);

        RingoClientMain.main(args);
        verify(mockErrorNotifier);
        System.exit(-1);

    }

    public class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new SecurityException("Not allowing System.exit");
        }
    }

}
