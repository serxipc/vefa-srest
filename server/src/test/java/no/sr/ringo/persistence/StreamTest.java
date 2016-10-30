package no.sr.ringo.persistence;

import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.testng.Assert.assertNotNull;

/**
 * @author steinar
 *         Date: 25.10.2016
 *         Time: 20.46
 */
public class StreamTest {

    @Test
    public void testJoin() {
        StringBuffer sb = new StringBuffer();

        List<String> names = Arrays.asList("Java8", "Mina", "Steinar", "Johanne", "Buster");
        Stream<String> s = names.stream();
        s.forEach(str -> {sb.append(str).append('\n'); } );

    }

    @Test
    public void testFilesLines() throws URISyntaxException, IOException {
        URL resource = StreamTest.class.getClassLoader().getResource("logback-test.xml");
        assertNotNull(resource);

        Stream<String> stringStream = Files.lines(Paths.get(resource.toURI()), Charset.forName("UTF-8"));

        StringBuffer sb = new StringBuffer();

        String collect = stringStream.collect(joining("\n"));
    }
}
