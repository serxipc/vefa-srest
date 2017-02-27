/*
 * Copyright 2010-2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.sr.ringo.persistence.file;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.sr.ringo.config.RingoConfigProperty;
import no.sr.ringo.message.ReceptionId;
import no.sr.ringo.transport.TransferDirection;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *   Computes the path for the various artifacts stored in the file system based upon the supplied {@link FileRepoKey}, which holdes
 *   the metadata used in the key.
 *
 * @author steinar
 *         Date: 17.10.2016
 *         Time: 11.02
 */
public class ArtifactPathComputer {

    private final Path basePath;

    private DateTimeFormatter isoDateFormat = DateTimeFormatter.ISO_LOCAL_DATE;

    @Inject
    public ArtifactPathComputer(@Named(RingoConfigProperty.PAYLOAD_BASE_PATH) Path basePath) {

        this.basePath = basePath;
    }

    public Path createPayloadPathFrom(FileRepoKey fileRepoKey) {

        if (fileRepoKey == null) {
            throw new IllegalArgumentException("Missing argument, null not allowed");
        }
        String filename = createBaseFilename(fileRepoKey, ArtifactType.PAYLOAD.getFileNameSuffix());

        Path resolvedPath = createCompletePath(fileRepoKey, filename);

        return resolvedPath;
    }

    public Path createNativeEvidencePathFrom(FileRepoKey fileRepoKey) {

        String fileName = createBaseFilename(fileRepoKey, ArtifactType.EVIDENCE.getFileNameSuffix());
        return createCompletePath(fileRepoKey, fileName);
    }

    String createBaseFilename(FileRepoKey fileRepoKey, String suffix) {
        return normalizeFilename(fileRepoKey.getReceptionId().toString()) + suffix;
    }

    Path createCompletePath(FileRepoKey fileRepoKey, String filename) {
        if (fileRepoKey == null) {
            throw new IllegalArgumentException("Missing required argument fileRepoKey");
        }
        if (filename == null) {
            throw new IllegalArgumentException("filename is required argument");
        }

        if (fileRepoKey.getReceiver() == null) {
            throw new IllegalArgumentException("recipientId is required property on fileRepoKey");
        }
        if (fileRepoKey.getSender() == null) {
            throw new IllegalArgumentException("senderId is required property on fileRepoKey");
        }

        if (fileRepoKey.getDate() == null) {
            throw new IllegalArgumentException("receivedTimeStamp is required property on fileRepoKey");
        }

        Path basePath = createBasePath(fileRepoKey.direction);
        final Date date = fileRepoKey.getDate();
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        Path path = Paths.get(basePath.toString(),normalizeFilename(fileRepoKey.getReceiver().getIdentifier()), normalizeFilename(fileRepoKey.getSender().getIdentifier()), isoDateFormat.format(ldt));
        return path.resolve(filename);
    }

    public Path createBasePath(TransferDirection transferDirection) {
        return Paths.get(basePath.toString(), transferDirection.name());
    }

    public static String normalizeFilename(String s) {
        return s.replaceAll("[^a-zA-Z0-9.-]", "_"); // allow alpha-numericals, punctation and minus (all others will be replaced by underlines)
    }


    public static class FileRepoKey {
        private final TransferDirection direction;
        private final ReceptionId receptionId;
        private final ParticipantIdentifier sender;
        private final ParticipantIdentifier receiver;
        private final Date date;

        public FileRepoKey(TransferDirection direction, ReceptionId receptionId, ParticipantIdentifier sender, ParticipantIdentifier receiver, Date date) {
            this.direction = direction;
            this.receptionId = receptionId;
            this.sender = sender;
            this.receiver = receiver;
            this.date = date;
        }

        public ReceptionId getReceptionId() {
            return receptionId;
        }

        public ParticipantIdentifier getSender() {
            return sender;
        }

        public ParticipantIdentifier getReceiver() {
            return receiver;
        }

        public Date getDate() {
            return date;
        }
    }

}
