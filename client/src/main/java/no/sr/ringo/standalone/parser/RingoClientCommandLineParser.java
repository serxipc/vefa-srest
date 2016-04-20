package no.sr.ringo.standalone.parser;

import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolParticipantId;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import static no.sr.ringo.exception.NotifyingException.NotificationType;

/**
 * Responsible for parsing command line, validation of arguments and construction of RingoClientParams object
 * <p/>
 * User: adam
 * Date: 1/27/12
 * Time: 8:08 AM
 */
public class RingoClientCommandLineParser {

    //PeppolParticipantId format consists of 4 country code digits, colon and the actual number
    private static final String PARTICIPANT_ID_FORMAT = "[0-9]{4,4}:.*";

    private static Options options = new Options();

    protected static final String ADDRESS_PROD = "https://ringo.domain.com";
    protected static final String ADDRESS_TEST = "https://ringo-test.domain.com";

    //specifies production environment
    private static final String PROD = "t";

    //address and log in data
    private static final String ADDRESS = "a";
    private static final String USERNAME = "u";
    private static final String PASSWORD = "p";

    //available operations
    private static final String UPLOAD = "l";
    private static final String DOWNLOAD = "d";
    private static final String UPLOAD_SINGLE = "n";
    private static final String SMP = "s";

    //parameters for download
    private static final String INBOX = "i";

    //parameters for upload
    private static final String OUTBOX = "o";
    private static final String FILENAME = "f";
    private static final String ARCHIVE = "v";
    private static final String CHANNEL_ID = "c";
    private static final String SENDER_ID = "x";
    private static final String RECIPIENT_ID = "r";
    private static final String PROXY_ADDRESS = "q";
    private static final String PROXY_PORT = "e";

    //params for SMP lookup
    private static final String PARTICIPANT_ID = "z";
    private static final String VALID_FORMAT_PEPPOL_PARTICIPANT = "valid format is: <4 digit agency code>:<Organisation identifier>";
    private static final String INVALID_PEPOL_PARTICIPANT = "Invalid participantId '%s', " + VALID_FORMAT_PEPPOL_PARTICIPANT;

    CommandLine commandLine;
    RingoClientParams params = new RingoClientParams();

    public RingoClientCommandLineParser() {
        prepareCommandLineOptions();
    }

    /**
     * Validates specified command line options.
     *
     * @param args
     * @return ringoClientParams parsed from options
     */
    public void parseCommandLine(String[] args) throws CommandLineParserException {

        try {
            CommandLineParser parser = new PosixParser();
            commandLine = parser.parse(options, args);

        } catch (ParseException e) {
            usage();
            throw new CommandLineParserException(e.getMessage());
        }

    }
    public RingoClientConnectionParams extractConnectionParams() throws CommandLineParserException{
        //extract username, password, URI and proxy
        return extractConnectionParams(params, commandLine);

    }

    public RingoClientParams extractOperationParams() throws CommandLineParserException{

        //extract operation and required arguments
        extractCommonMandatoryParams(params, commandLine);

        validateDisallowedOptionCombinations(commandLine);

        switch (params.getOperation()) {
            case DOWNLOAD:
                extractDownloadParams(params, commandLine);
                break;
            case UPLOAD:
                extractUploadParams(params, commandLine, false);
                break;
            case UPLOAD_SINGLE:
                extractUploadParams(params, commandLine, true);
                break;
            case SMP_LOOKUP:
                extractSMPLookupParams(params, commandLine);
                break;
            default:
        }

        return params;
    }

    private RingoClientConnectionParams extractConnectionParams(RingoClientParams params, CommandLine commandLine) throws CommandLineParserException {
        RingoClientConnectionParams connectionParams = new RingoClientConnectionParams();

        if (!commandLine.hasOption(USERNAME)) {
            throw new CommandLineParserException("Username required (--username)");
        }

        String username = commandLine.getOptionValue(USERNAME);
        if (StringUtils.isBlank(username)) {
            throw new CommandLineParserException("Username value required");
        }
        connectionParams.setUsername(username);

        if (!commandLine.hasOption(PASSWORD)) {
            throw new CommandLineParserException("Password required (--password)");
        }

        String password = commandLine.getOptionValue(PASSWORD);
        if (StringUtils.isBlank(password)) {
            throw new CommandLineParserException("'Password value' required");
        }
        connectionParams.setPassword(password);

        String address = null;

        // if address option is specified, try to take the address from params
        if (commandLine.hasOption(ADDRESS)) {
            address = commandLine.getOptionValue(ADDRESS);
            if (StringUtils.isBlank(address)) {
                throw new CommandLineParserException("AccessPoint address value required when --address option used");
            }
        } else {
            // if not specified, use default (test address or production if prod flag used)
            if (commandLine.hasOption(PROD)) {
                address = ADDRESS_PROD;
            } else {
                address = ADDRESS_TEST;
            }
        }

        try {
            connectionParams.setAccessPointURI(new URI(address));
        } catch (URISyntaxException e) {
            throw new CommandLineParserException(String.format("Provided address ('%s') is not valid", address));
        }

        boolean proxyAddress = commandLine.hasOption(PROXY_ADDRESS);
        boolean proxyPort = commandLine.hasOption(PROXY_PORT);

        if ((proxyAddress && !proxyPort) || (proxyPort && !proxyAddress)) {
            throw new CommandLineParserException("When using proxy, both address and port need to be specified.");
        }

        if (commandLine.hasOption(PROXY_ADDRESS)) {
            connectionParams.setProxyAddress(commandLine.getOptionValue(PROXY_ADDRESS));
        }

        if (commandLine.hasOption(PROXY_PORT)) {

            try {
                Integer port = Integer.valueOf(commandLine.getOptionValue(PROXY_PORT));
                connectionParams.setProxyPort(port);
            } catch (Exception e) {
                throw new CommandLineParserException("Wrong proxy port: " + commandLine.getOptionValue(PROXY_PORT) + ".");
            }
        }

        return connectionParams;
    }

    /*
    * Tries to extract parameters that are common for each operation and put them into params
    */
    private void extractCommonMandatoryParams(RingoClientParams params, CommandLine commandLine) throws CommandLineParserException {

        boolean isDownload = commandLine.hasOption(DOWNLOAD);
        boolean isUpload = commandLine.hasOption(UPLOAD);
        boolean isUploadSingle = commandLine.hasOption(UPLOAD_SINGLE);
        boolean isSmp = commandLine.hasOption(SMP);


        if (isDownload) {
            params.setOperation(RingoClientParams.ClientOperation.DOWNLOAD);
        } else if (isUpload) {
            params.setOperation(RingoClientParams.ClientOperation.UPLOAD);
        } else if (isUploadSingle) {
            params.setOperation(RingoClientParams.ClientOperation.UPLOAD_SINGLE);
        } else if (isSmp) {
            params.setOperation(RingoClientParams.ClientOperation.SMP_LOOKUP);
        } else {
            throw new CommandLineParserException("One of: '--upload', '--uploadSingle', --download' '--smp' option required.");
        }

    }

    /**
     * Extracts parameters specific for download operation
     */
    private void extractDownloadParams(RingoClientParams params, CommandLine commandLine) throws CommandLineParserException {
        if (commandLine.hasOption(INBOX)) {
            String inboxString = commandLine.getOptionValue(INBOX);
            File inboxPath = new File(inboxString);
            if (inboxPath == null || !inboxPath.isDirectory()){
                throw new CommandLineParserException("Specified download path '" + inboxString+ "' does not exist or is not a directory", NotificationType.DOWNLOAD);
            }
            params.setInboxPath(inboxPath);
        }
    }

    /**
     * Extracts parameters specific for upload operation
     */
    private void extractUploadParams(RingoClientParams params, CommandLine commandLine, boolean singleUpload) throws CommandLineParserException {

        //directories for single upload
        if (singleUpload) {
            // handling single upload, outbox path required and it must exist and be a file
            if (commandLine.hasOption(FILENAME) && StringUtils.isNotBlank(commandLine.getOptionValue(FILENAME))) {
                String filenameString = commandLine.getOptionValue(FILENAME);
                File filenamePath = new File(filenameString);

                if (!filenamePath.exists() || !filenamePath.isFile()) {
                    throw new CommandLineParserException(String.format("Specified file (%s) for single file upload doesn't exist or is not a file", filenameString));
                }
                params.setOutboxPath(filenamePath);
            } else {
                throw new CommandLineParserException(String.format("When performing single file upload --filename option is required and it must point to a file"));
            }

            // optional recipientId parameter available
            if (commandLine.hasOption(RECIPIENT_ID)) {
                String recipientId = commandLine.getOptionValue(RECIPIENT_ID);
                if (StringUtils.isBlank(recipientId)) {
                    throw new CommandLineParserException(String.format("Value for recipientId required when using --recipientId option"));
                }
                if (!recipientId.matches(PARTICIPANT_ID_FORMAT)) {
                    throw new CommandLineParserException(String.format("Invalid recipientId '%s', " + VALID_FORMAT_PEPPOL_PARTICIPANT, recipientId));
                }

                final PeppolParticipantId peppolParticipantId = PeppolParticipantId.valueFor(recipientId);
                if (peppolParticipantId == null) {
                    throw new CommandLineParserException(String.format("Invalid recipientId '%s', " + VALID_FORMAT_PEPPOL_PARTICIPANT, recipientId));
                }
                params.setRecipientId(peppolParticipantId);
            }

            // optional senderId parameter available
            if (commandLine.hasOption(SENDER_ID)) {
                String senderId = commandLine.getOptionValue(SENDER_ID);
                if (StringUtils.isBlank(senderId)) {
                    throw new CommandLineParserException(String.format("Value for senderId required when using --senderId option"));
                }
                if (!senderId.matches(PARTICIPANT_ID_FORMAT)) {
                    throw new CommandLineParserException(String.format("Invalid senderId '%s', " + VALID_FORMAT_PEPPOL_PARTICIPANT, senderId));
                }

                final PeppolParticipantId peppolParticipantId = PeppolParticipantId.valueFor(senderId);
                if (peppolParticipantId == null) {
                    throw new CommandLineParserException(String.format("Invalid senderId '%s', " + VALID_FORMAT_PEPPOL_PARTICIPANT, senderId));
                }
                params.setSenderId(peppolParticipantId);

            }

        } else {
            if (commandLine.hasOption(OUTBOX)) {
                String outboxString = commandLine.getOptionValue(OUTBOX);
                File outboxPath = new File(outboxString);
                if (outboxPath == null || !outboxPath.isDirectory()){
                    throw new CommandLineParserException("Outbox path " + outboxString+ " does not exist or is not a directory", NotificationType.BATCH_UPLOAD);
                }
                params.setOutboxPath(outboxPath);
            }
        }


        if (commandLine.hasOption(ARCHIVE)) {
            String archiveString = commandLine.getOptionValue(ARCHIVE);
            File archivePath = new File(archiveString);
            params.setArchivePath(archivePath);
        }
        NotificationType notificationType = !singleUpload ? NotificationType.BATCH_UPLOAD : null;

        if (params.getOutboxPath() != null && params.getArchivePath() != null && params.getOutboxPath().equals(params.getArchivePath())) {
            throw new CommandLineParserException("Outbox path cannot be the same as archive path", notificationType);
        }

        if (!commandLine.hasOption(CHANNEL_ID)) {
            throw new CommandLineParserException("ChannelId required for file upload required (--channelId)", notificationType);
        }

        String channelId = commandLine.getOptionValue(CHANNEL_ID);
        if (StringUtils.isBlank(channelId)) {
            throw new CommandLineParserException("ChannelId required value for file upload required", notificationType);
        }
        params.setChannelId(new PeppolChannelId(channelId));

    }


    /**
     * Throws CommandLineParserException when disallowed combination occurs
     */
    private void validateDisallowedOptionCombinations(CommandLine commandLine) throws CommandLineParserException {
        boolean upload = commandLine.hasOption(UPLOAD);
        boolean upload_single = commandLine.hasOption(UPLOAD_SINGLE);
        boolean download = commandLine.hasOption(DOWNLOAD);
        boolean smp = commandLine.hasOption(SMP);

        boolean inbox = commandLine.hasOption(INBOX);
        boolean outbox = commandLine.hasOption(OUTBOX);
        boolean archive = commandLine.hasOption(ARCHIVE);

        boolean smpParticipantId = commandLine.hasOption(PARTICIPANT_ID);
        boolean singleUploadRecipientId = commandLine.hasOption(RECIPIENT_ID);
        boolean singleUploadSenderId = commandLine.hasOption(SENDER_ID);

        NotificationType notificationType = upload ? NotificationType.BATCH_UPLOAD : download ? NotificationType.DOWNLOAD : null;

        //ParticipantId allowed for SMP lookup only
        if (!smp) {
            if (smpParticipantId) {
                throw new CommandLineParserException("ParticipantId option allowed only for SMP lookup.", notificationType);
            }
        }

        //outbox and archive allowed for upload options only
        if (!upload && !upload_single) {
            if (outbox) {
                throw new CommandLineParserException("Outbox path option allowed only for upload.", notificationType);
            } else if (archive) {
                throw new CommandLineParserException("Archive path option allowed only for upload.", notificationType);
            }
        }

        //recipientId allowed for single upload only
        if (singleUploadRecipientId && !upload_single) {
            throw new CommandLineParserException("RecipientId option allowed only for single upload only. For multiple upload it will be extracted from xml file.", notificationType);
        }

        //senderId allowed for single upload only
        if (singleUploadSenderId && !upload_single) {
            throw new CommandLineParserException("SenderId option allowed only for single upload only. For multiple upload it will be extracted from xml file.", notificationType);
        }

        //inbox allowed for download only
        if (!download) {
            if (inbox) {
                throw new CommandLineParserException("Inbox path option allowed only for download.", notificationType);
            }
        }
    }

    /**
     * Extracts parameters specific for upload operation
     */
    private void extractSMPLookupParams(RingoClientParams params, CommandLine commandLine) throws CommandLineParserException {
        if (!commandLine.hasOption(PARTICIPANT_ID)) {
            throw new CommandLineParserException("ParticipantId required for smp lookup (--participantId)");
        }

        String participantIdString = commandLine.getOptionValue(PARTICIPANT_ID);

        if (!participantIdString.matches(PARTICIPANT_ID_FORMAT)) {
            throw new CommandLineParserException(String.format(INVALID_PEPOL_PARTICIPANT, participantIdString));
        }
        final PeppolParticipantId peppolParticipantId = PeppolParticipantId.valueFor(participantIdString);
        if (peppolParticipantId == null) {
            throw new CommandLineParserException(String.format(INVALID_PEPOL_PARTICIPANT, participantIdString));
        }
        params.setPeppolParticipantId(peppolParticipantId);
    }

    /**
     * Prepares command line options
     */
    private void prepareCommandLineOptions() {

        Option username = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("Username")
                .withLongOpt("username").create(USERNAME);

        Option password = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("Password")
                .withLongOpt("password").create(PASSWORD);


        Option download = OptionBuilder
                .withType(String.class)
                .withDescription("Download")
                .withLongOpt("download").create(DOWNLOAD);

        Option smp = OptionBuilder
                .withDescription("SMP lookup")
                .withLongOpt("smp").create(SMP);

        Option upload = OptionBuilder
                .withDescription("Upload")
                .withLongOpt("upload").create(UPLOAD);

        Option production = OptionBuilder
                .withDescription("Use production environment")
                .withLongOpt("prod").create(PROD);

        Option uploadSingle = OptionBuilder
                .withDescription("Single file upload")
                .withLongOpt("uploadSingle").create(UPLOAD_SINGLE);

        Option recipientId = OptionBuilder
                .hasArg()
                .withDescription("RecipientId for single upload")
                .withLongOpt("recipientId").create(RECIPIENT_ID);

        Option uploadPath = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("Outbox path")
                .withLongOpt("outboxPath").create(OUTBOX);

        Option downloadPath = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("Inbox path")
                .withLongOpt("inboxPath").create(INBOX);

        Option archivePath = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("Archive path")
                .withLongOpt("archivePath").create(ARCHIVE);

        Option participantId = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("ParticipantId for SMP lookup")
                .withLongOpt("participantId").create(PARTICIPANT_ID);

        Option channelId = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("ChannelId for uploading file")
                .withLongOpt("channelId").create(CHANNEL_ID);

        Option senderId = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("Sender's participantId for file upload")
                .withLongOpt("senderId").create(SENDER_ID);

        Option address = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("AccessPoint URI")
                .withLongOpt("address").create(ADDRESS);

        Option filename = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("Filename for single upload")
                .withLongOpt("filename").create(FILENAME);

        Option proxyAddress = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("Proxy address")
                .withLongOpt("proxyAddress").create(PROXY_ADDRESS);

        Option proxyPort = OptionBuilder
                .hasArg()
                .withType(String.class)
                .withDescription("Proxy port")
                .withLongOpt("proxyPort").create(PROXY_PORT);

        options.addOption(username);
        options.addOption(password);
        options.addOption(download);
        options.addOption(upload);
        options.addOption(uploadSingle);
        options.addOption(recipientId);
        options.addOption(production);
        options.addOption(smp);
        options.addOption(uploadPath);
        options.addOption(downloadPath);
        options.addOption(archivePath);
        options.addOption(participantId);
        options.addOption(address);
        options.addOption(channelId);
        options.addOption(senderId);
        options.addOption(filename);
        options.addOption(proxyAddress);
        options.addOption(proxyPort);

    }

    /**
     * Prints the help information on how to use the command line
     */
    public void usage() {
//        HelpFormatter helpFormatter = new HelpFormatter();
//        helpFormatter.printHelp("Ringo client",
//                "Valid options are:", options, "\n");

        StringBuilder sb = new StringBuilder();
        sb.append("\nThe Ringo client allows upload and download of invoices and checking if a given recipient is registered in the PEPPOL network.");
        sb.append("\nAll operations require username (--username option) and password (--password option).");
        sb.append("\nDefault access point address is set to test environment: " + ADDRESS_TEST);
        sb.append("\nTo use production environment (" + ADDRESS_PROD + ") specify --prod (-t) option: ");
        sb.append("\nAccess point address can be overridden by specifying --address (-a) option.");
        sb.append("\nThe client can communicate through a proxy using the --proxyAddress (-q) and --proxyPort (-e) settings. ");
        sb.append("\nNote that both settings are required to enable use of a proxy, and that proxy servers requiring authentication are currently not supported.");

        sb.append("\n\nUPLOAD INVOICES\n");
        sb.append("\nTo upload all files from directory, use --upload (-l). Default directory is 'outbox', but can be overridden by --outboxPath (-o) option.");
        sb.append("\nUploaded files will be moved to archive directory. Default directory is 'archive', but can be overridden by --archivePath (-v) option.");
        sb.append("\nIf default directories or archive directory specified with --archivePath do not exist they will be created.");
        sb.append("\nExample: -u user -p secret --upload --outboxPath /home/upload --archivePath /home/archive");
        sb.append("\nDefault directories: -u user -p secret --upload --outboxPath /home/upload ");
        sb.append("\nIf any file within directory fails to be uploaded it will be skipped.");
        sb.append("\nFailure reason will be stored in outbox path in corresponding file with the sane name and '.err' extension");

        sb.append("\n\nUse --uploadSingle (-n) to upload single file. For this option --filename is mandatory and should point to file to be uploaded.");
        sb.append("\nWhen uploading single file one can specify recipient using --recipientId (-r) and/or sender using --senderId (-x) in format <Numeric ISO6523 code>:<Identifier>");
        sb.append("\nExample: -u user -p secret --uploadSingle --filename /home/upload/invoice.xml --recipientId 9908:976098897");

        sb.append("\n\nDOWNLOAD INVOICES\n");
        sb.append("\nTo download files, use --download option (-d). Default directory is 'inbox', but can be overridden by --inboxPath (-i) option.");
        sb.append("\nIf directory doesn't exist it will be created.");
        sb.append("\nAll messages for given receiver will be stored in a separate directory in inbox path.");
        sb.append("\nThe receiver directory will have the name of peppol participant id and will be created if doesn't exist.");

        sb.append("\nExample: -u user -p secret --download --inboxPath /home/download");
        sb.append("\nDefault directories: -u user -p secret --download");

        sb.append("\n\nSMP LOOKUP\n");
        sb.append("\nTo check whether given participant is registered in the PEPPOL network, use --smp (-s) and --participantId (-z) with value to be checked");
        sb.append("\nExample: -u user -p secret --smp -z 9908:976098897");

        System.out.println(sb.toString());

    }
}
