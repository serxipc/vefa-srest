package no.sr.ringo.client;

import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;

/**
 * Created by soc on 25.04.2016.
 */
public class FileNameTest {

    @Test
    public void testPathNames() throws Exception {

        File file = new File("/tmp/../foo");
        String s = file.toString();
        Path path = Paths.get("/tmp", "foo");
        System.out.println("File: " + s + ", Path: " + path + ", path to file: " + path.toFile() + ", file to path: " + file.toPath());
        String absolutePath = file.getAbsolutePath();

        File absoluteFile = file.getAbsoluteFile();
        String absolutePath1 = file.getAbsolutePath();
        File canonicalFile = file.getCanonicalFile();
        String canonicalPath1 = file.getCanonicalPath();
        String path2 = file.getPath();
        Path path3 = file.toPath();

        String canonicalPath = file.getCanonicalPath();
        Path path1 = path.toAbsolutePath();

    }
}
