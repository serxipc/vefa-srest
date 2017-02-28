package no.difi.ringo.tools;

import no.sr.ringo.message.MessageMetaDataImpl;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author steinar
 *         Date: 27.02.2017
 *         Time: 15.48
 */
public class PersistenceObjectMotherTest {

    @Test
    public void testSampleInboundTransmissionMetaData() throws Exception {
        final MessageMetaDataImpl tmd = (MessageMetaDataImpl) PersistenceObjectMother.sampleInboundTransmissionMetaData();

        final MessageMetaDataImpl mmd = new MessageMetaDataImpl(tmd);

        assertEquals(mmd, tmd);
    }

}