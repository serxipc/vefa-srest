package no.sr.ringo.document;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.InputStream;

/**
* User: andy
* Date: 10/29/12
* Time: 1:16 PM
*/
public class InputStreamPeppolDocument extends ClientPeppolDocument {
    private final InputStream inputStream;

    public InputStreamPeppolDocument(InputStream inputStream) {
        super();
        this.inputStream = inputStream;
    }

    @Override
    public ContentBody getContentBody() {
        return new InputStreamBody(inputStream,"application/xml");
    }
}
