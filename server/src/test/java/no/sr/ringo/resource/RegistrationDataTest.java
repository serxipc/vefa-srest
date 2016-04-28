package no.sr.ringo.resource;

import no.sr.ringo.account.RegistrationData;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * User: Adam
 * Date: 4/3/12
 * Time: 10:44 AM
 */
public class RegistrationDataTest {

    @Test
    public void testFromJson() throws Exception {
        RegistrationData rd = RegistrationData.fromJson("{\"name\":\"adam\",\"address1\":\"add1\",\"address2\":\"add2\",\"zip\":\"0976\",\"city\":\"oslo\",\"country\":\"norge\",\"contactPerson\":\"adam m\",\"email\":\"adam@sendregning.no\",\"phone\":\"888 999 000\",\"username\":\"adamm\",\"password\":\"secret\",\"orgNo\":\"976098897\",\"registerSmp\":\"true\"}");
        RegistrationData expected = new RegistrationData("adam", "secret", "adamm", "add1", "add2", "0976", "oslo", "norge", "adam m", "adam@sendregning.no", "888 999 000", "976098897", true);
        assertEquals(expected, rd);

        rd = RegistrationData.fromJson("{\"name\":\"adam\",\"address1\":\"add1\",\"address2\":\"add2\",\"zip\":\"0976\",\"city\":\"oslo\",\"country\":\"norge\",\"contactPerson\":\"adam m\", \"email\":\"adam@sendregning.no\",\"phone\":\"888 999 000\",\"username\":\"adamm\",\"password\":\"secret\",\"orgNo\":\"976098897\",\"registerSmp\":\"false\"}");
        expected = new RegistrationData("adam", "secret", "adamm", "add1", "add2", "0976", "oslo", "norge", "adam m", "adam@sendregning.no", "888 999 000", "976098897",false);
        assertEquals(expected, rd);

    }
}
