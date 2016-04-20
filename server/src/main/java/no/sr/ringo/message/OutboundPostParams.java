package no.sr.ringo.message;

import java.io.InputStream;

/**
 * @author adam
 * @author thore
 */
public class OutboundPostParams {
    private String senderIdString;
    private String recipientIdString;
    private String processIdString;
    private String documentIdString;
    private String channelIdString;
    private InputStream inputStream;
    private String uploadMode;
    private String filename;

    private OutboundPostParams() {
    }

    public String getSenderIdString() {
        return senderIdString;
    }

    public String getRecipientIdString() {
        return recipientIdString;
    }

    public String getProcessIdString() {
        return processIdString;
    }

    public String getDocumentIdString() {
        return documentIdString;
    }

    public String getChannelIdString() {
        return channelIdString;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getUploadMode() {
        return uploadMode;
    }

    public String getFilename() {
        return filename;
    }

    public static class Builder {
        private OutboundPostParams params;

        public Builder() {
            params = new OutboundPostParams();
        }

        public Builder senderId(String sender) {
            params.senderIdString = sender;
            return this;
        }

        public Builder recipientId(String recipient) {
            params.recipientIdString = recipient;
            return this;
        }

        public Builder channelId(String channel) {
            params.channelIdString = channel;
            return this;
        }
        public Builder processId(String process) {
            params.processIdString = process;
            return this;
        }
        public Builder documentId(String documentId) {
            params.documentIdString = documentId;
            return this;
        }
        public Builder inputStream(InputStream inputStream) {
            params.inputStream = inputStream;
            return this;
        }

        public Builder uploadMode(String uploadMode) {
            params.uploadMode = uploadMode;
            return this;
        }

        public Builder fileName(String fileName) {
            params.filename = fileName;
            return this;
        }

        public OutboundPostParams build() {
            return params;
        }

    }

}
