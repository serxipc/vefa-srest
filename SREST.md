# Overview of REST functionality


Documentation overview ...
```
method    uri       produce                 description
          [information about input parameters that it consumes]
```

Start of a resource providing statistics over the inbox, outbox and messages
```
GET   /statistics   APPLICATION_XML         Retrieves statistics overview of the number of inbox, outbox and messages
```

Represents the "inbox" resource, which allows clients to GET MesssageMetaData messages
```
GET   /inbox                                APPLICATION_XML      Retrieves the unread messages from the inbox
GET   /inbox/{message_no}/                  APPLICATION_XML      Retreives the message header for the supplied message number
GET   /inbox/{message_no}/xml-document      APPLICATION_XML      Retrieves the PEPPOL XML Document in XML format (without the header stuff)
POST  /inbox/{message_no}/read              APPLICATION_XML      Marks a specific message as read
GET   /inbox/count                          TEXT_PLAIN           Returns number of messages in inbox
```

Represents the "outbox" resource, which allows clients to POST outboundMesssageMetaData messages destined for a recipient in the
```
@GET  /outbox                               APPLICATION_XML         Retrieves the queued messages in the outbox
@GET  /outbox /{message_no}/                APPLICATION_XML         Retreives the message header for the supplied message number.
@GET  /outbox /{message_no}/xml-document    APPLICATION_XML         Retrieves the PEPPOL XML Document in XML format, without the header stuff
@POST /outbox                               APPLICATION_XML         Sends a new message to specified receiver
      MULTIPART_FORM_DATA
```

Represents the "messages" resource, which allows clients to GET MessageMetaData from both inbox and outbox
```
GET   /messages                                         APPLICATION_XML        Retrieves messages from /inbox and /outbox
GET   /messages/{message_no}/                           APPLICATION_XML        Retreives the message header for the supplied message number
GET   /messages/{message_no}/xml-document               APPLICATION_XML        Retrieves the PEPPOL XML Document in XML format, without the header stuff.
GET   /messages/{message_no}/xml-document-decorated     APPLICATION_XML        Retrieves the PEPPOL XML Document in XML format, with added stylesheet (intended for web viewing on our site).
GET   /messages/count                                   TEXT_PLAIN             Returns number of messages in inbox
```

Represents the "directory" resource, which allows clients to check whether participant is registered in peppol network.
```
GET   /directory/{participantId}/{localName}  APPLICATION_XML   Retrieves acceptable document types for given participant and localName
GET   /directory/{participantId}/             HTTP CODE         Checks whether participant is registered in SMP
      Returns HTTP_OK if exists in some SMP - HTTP_NC (no content) if not registered
```

Exposing resources allowing to send notification emails when something goes wrong in ringo client
```
POST  /notify/batchUploadError      APPLICATION_XML
      @FormParam("commandLine") String commandLine, @FormParam("errorMessage") APPLICATION_FORM_URLENCODED
POST  /notify/downloadError         APPLICATION_XML
      @FormParam("commandLine") String commandLine, @FormParam("errorMessage") APPLICATION_FORM_URLENCODED
```

Represents the "admin" resource, which allows to look up various statuses
```
GET   /admin/status             APPLICATION_XML    Retrieves messages without account_id (messages we do not know who belongs to)
GET   /admin/statistics         APPLICATION_XML    Retrieve Statistics
GET   /admin/sendMonthlyReport  TEXT_PLAIN         Send monthly report (for previous month)
      @QueryParam("year") Integer year, @QueryParam("month") Integer month, @QueryParam("email") String email
```

Register a new user (customer account) in the system.
```
POST  /register     APPLICATION_JSON        Returns RegistrationData in JSON format as TEXT_PLAIN
```
