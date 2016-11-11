package no.sr.ringo.account;

import eu.peppol.persistence.AccountId;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * User: andy
 * Date: 2/3/12
 * Time: 4:27 PM
 */
public class AccountIdTest {
    @Test
    public void testEquals() throws Exception {
        final AccountId accountId1 = AccountId.valueOf("1");
        final AccountId accountId2 = AccountId.valueOf("1");

        assertEquals(accountId1, accountId2);
    }
}
