# Overview of REST functionality

Below are brief documentation of all REST resources and their supported actions.

## General information

- All REST functions needs basic authentication
- Make sure your HTTP client library always send Authorization header (force preemptive auth)
- Make sure your HTTPS client supports new certificates (high security, no-SSL, TLS v1.2+ etc).

## Method overview

```
verb    uri       produces                 description
        [optional 2nd line with information about input parameters that it consumes]
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
GET  /outbox                               APPLICATION_XML         Retrieves the queued messages in the outbox
GET  /outbox /{message_no}/                APPLICATION_XML         Retreives the message header for the supplied message number.
GET  /outbox /{message_no}/xml-document    APPLICATION_XML         Retrieves the PEPPOL XML Document in XML format, without the header stuff
POST /outbox                               APPLICATION_XML         Sends a new message to specified receiver
      MULTIPART_FORM_DATA
```

Represents the "messages" resource, which allows clients to GET MessageMetaData from both inbox and outbox
```
GET   /messages                                         APPLICATION_XML        Retrieves messages from /inbox and /outbox
GET   /messages/{message_no}/                           APPLICATION_XML        Retreives the message header for the supplied message number
GET   /messages/{message_no}/xml-document               APPLICATION_XML        Retrieves the PEPPOL XML Document in XML format, without the header stuff.
GET   /messages/{message_no}/xml-document-decorated     APPLICATION_XML        Retrieves the PEPPOL XML Document in XML format, with added stylesheet (intended for web viewing on our site).
GET   /messages/count                                   TEXT_PLAIN             Returns number of messages in inbox
GET   /messages/{message_no}/rem                        APPLICATION_XML        Returns REM evidence, if available (TODO)
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


### Registration

This is an example showing how to register a new account. I.e entries will be created in the database tables `customer` and `account`:

 1. Create a file in JSON syntax holding the data to be registered:
    ``` 
    {
      "name" : "Difi",
      "address1" : "Business Address 1",
      "address2" : "Business Address 2",
      "zip" : "0494",
      "city" : "Oslo",
      "country" : "Norway",
      "contactPerson" : "The Boss",
      "email" : "boss@business.com",
      "phone" : "111222333",
      "username" : "newbusiness",
      "password" : "topsecret123",
      "orgNo" : "NO991825827",
      "registerSmp" : false
    }
    ```
    Note! The organisation number must be valid and previously unknown in the system and *must* be prefixed with the country code or any other prefix which is required by 
    whatever Issuing Agency Scheme is in charge.
 
 1. Execute the registration by performing an http POST using any tool you like. Here is how you would do it using `curl`, given the data being present in 
    a file named `/tmp/register.json`:
    ```
    curl -i -H "Content-Type: text/plain" -H "Accept: application/json" -X POST \ 
        --data @/tmp/register.json http://localhost:8080/vefa-srest/register/
    ```
 
 1. Upon successful completion you should see something like this:
    ```
    HTTP/1.1 200 
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Wed, 12 Oct 2016 15:16:06 GMT
    ```
    
### Sending a file

Assuming that you wish to send an invoice located in `invoice.xml` in your current directory:

 1. Send it:
    ```
    curl -i -u sr:ringo1 \
      -H 'Accept: application/xml'  \
      -F 'file=@invoice.xml' \
      -F 'SenderID=9908:976098897' \
      -F 'RecipientID=9908:810418052' \
      -F 'ChannelID=PEPPOL' \
      -F 'ProcessID=urn:www.cenbii.eu:profile:bii04:ver2.0' \
      -F 'DocumentID=urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1' \
        http://localhost:8080/vefa-srest/outbox
    ```
    *NOTE* The uploaded file is not parsed. The metadata used for transmission is taken from the supplied headers.
 
 1. You should receive a response looking something like this:
    ```
     HTTP/1.1 100 Continue
     
     HTTP/1.1 201 
     Location: https://localhost:8080/vefa-srest/outbox/97
     Content-Type: application/xml;charset=UTF-8
     Content-Length: 1035
     Date: Fri, 04 Nov 2016 07:51:51 GMT
     
     <outbox-post-response version="1.0">
     <message>
             <self>https://localhost:8080/vefa-srest/outbox/97</self>
             <xml-document>https://localhost:8080/vefa-srest/messages/97/xml-document</xml-document>
             <message-meta-data>
               <msg-no>97</msg-no>
               <direction>OUT</direction>
               <received>2016-11-04T08:51:51.778+01:00</received>
               <peppol-header>
                 <sender>9908:976098897</sender>
                 <receiver>9908:810418052</receiver>
                 <channel>PEPPOL</channel>
                 <document-type>INVOICE</document-type>
                 <document-id>urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1</document-id>
                 <process-name>UNKNOWN</process-name>
                 <process-id>urn:www.cenbii.eu:profile:bii04:ver2.0</process-id>
               </peppol-header>
             </message-meta-data>
     </message>
     </outbox-post-response>
    ```