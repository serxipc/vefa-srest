package no.sr.ringo.utils;

import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.testng.Assert.*;

public class SbdhUtilsTest {


    @Test
    public void testRemoveSbdhEnvelope() throws Exception {

        InputStream in = this.getClass().getResourceAsStream("/sbdh-file.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }

        String xmlWithSbdh = out.toString();
        assertTrue(xmlWithSbdh.startsWith("<StandardBusinessDocument"));
        assertTrue(xmlWithSbdh.endsWith("</StandardBusinessDocument>"));

        String xmlWithoutSbdh = SbdhUtils.removeSbdhEnvelope(xmlWithSbdh);
        assertTrue(xmlWithoutSbdh.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Invoice"));
        assertTrue(xmlWithoutSbdh.endsWith("</Invoice>"));

    }


}