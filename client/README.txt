*** Prerequisites ***

1.  Make sure Java 1.5 or later is installed
2a. On Linux/Mac Java is detected automatically in most configurations
2b. On Windows Java must be added to your path, see http://java.com/en/download/help/path.xml
3.  You must create three folders, and the client must have read/write permissions in them:
    - inbox, the directory you download incoming invoices to
    - outbox, the directory you place outgoing invoices in for upload
    - archive, the directory outgoing invoices are moved to after they have been successfully uploaded

*** Getting started ***

Download EHF invoices from the access point:

    Windows: bin/download.bat your_username your_password inboxPath

    Linux/Mac: bin/download.sh your_username your_password inboxPath

    E.g: bin/download.sh me@company.com secret /tmp/inbox

Upload all XML files found in the given folder to the access point:

    Windows: bin/upload.bat your_username your_password outboxPath archivePath channel

    Linux/Mac: bin/upload.sh your_username your_password outboxPath archivePath channel

    E.g: bin/upload.sh me@company.com secret /tmp/outbox /tmp/archive ChannelTEST

    NB: This will upload all XML files located in the given outbox folder.

SMP lookup (checks whether a participant is registered in the PEPPOL network):

    Windows: bin/smp.bat your_username your_password participantId

    Linux/Mac: bin/smp.sh your_username your_password participantId

    E.g: bin/smp.sh me@company.com secret 9908:976098897

*** Troubleshooting ***

Mac:

If you get this message running any of the shell scripts in the bin folder:

    WARNING: The locate database (/var/db/locate.database) does not exist.
    To create the database, run the following command:

        sudo launchctl load -w /System/Library/LaunchDaemons/com.apple.locate.plist

    Please be aware that the database can take some time to generate; once
    the database has been created, this message will no longer appear.

    ### ERROR ###
    Can't find Java 1.5 (or higher) on this computer
    The safest way to tell me where Java is installed is to define JAVA_HOME

Please execute the given command and wait for locate to finish, this will take 3-5 minutes. If you are in a real hurry, please define JAVA_HOME so the script doesn't have to look for your Java installation.