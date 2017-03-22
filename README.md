Ringo - the PEPPOL Access Point back end
========================================


[TOC levels=2-5]: # "### Table of contents"
### Table of contents
- [Installation](#installation)
- [Possible problems and how to solve them](#troubleshooting)

This repository contains, "Ringo", a REST-based system for managing the "back-end" for a PEPPOL Access Point running
[Oxalis](https://github.com/difi/oxalis).

Ringo was originally developed by Steinar Overbeck Cook, [SendRegning](http://www.sendregning.no/), which was
taken over by Unit4 in 2012. Unit4 has kindly donated the software to
the Norwegian agency for Public Management and eGovernment (Difi), which now looks after the software.

_Ringo_ was developed to be used with the MySQL database. It has been confirmed to work with H2
and Microsoft SQL Server as well. If you would like to use a different database, please
contribute and send us a pull request.

## Installation

Please refer to the [Installation guide](/INSTALL.md)

## Troubleshooting

When sending messages fail for some reason inspect the error that was logged in the outbound_message_queue_error table.
Typical exception messages show below and steps needed to rectify the issue described.

### Receivers PEPPOL certificate expired
Typical exception and error message shown below.
```
java.lang.RuntimeException: Failed to get valid certificate from Endpoint data
Caused by: java.security.cert.CertificateExpiredException: NotAfter: Fri Mar 17 00:59:59 CET 2017
```
1. When this happens, notify the receiving accesspoint directly. 
1. Contact information can be found in ELMA.
1. Message can be resent when the receiving ap has renewed with PEPPOL and updated their certificate in the SMP.

### Invalid HTTPS certificate
Typical exception will have traces of javax.net.ssl in them and example error message shown below.
```
java.lang.IllegalStateException: Unexpected error during execution of http POST to https://ap.somewhere.no/oxalis/as2
Caused by: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```
1. When this happens, notify the receiving accesspoint directly. 
1. Contact information can be found in ELMA.
1. Message can be resent when the receiving ap has fixed their certificate issues (in this case incomplete chain)

### Expired HTTPS certificate
Typical exception will have traces of javax.net.ssl in them and example error message shown below.
```
java.lang.IllegalStateException: Unexpected error during execution of http POST to https://ap.somewhere.no/oxalis/as2
Caused by: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path validation failed: java.security.cert.CertPathValidatorException: timestamp check failed
Caused by: sun.security.validator.ValidatorException: PKIX path validation failed: java.security.cert.CertPathValidatorException: timestamp check failed
Caused by: java.security.cert.CertPathValidatorException: timestamp check failed
Caused by: java.security.cert.CertificateExpiredException: NotAfter: Mon Oct 24 14:00:00 CEST 2016
```
1. Contact receiving accesspoint

### Access Point not available
Typical exception and error messages shown below.
```
java.lang.IllegalStateException: Unexpected error during execution of http POST to https://ap.somewhere.no/oxalis/as2
Caused by: java.net.SocketException: Connection reset
```
1. Usually happens when receiving access point experience performance issues
1. Resolved by retrying later
1. If problem persist, notify the receiving accesspoint ot see if there are known issues


```
java.lang.IllegalStateException: The Oxalis server does not seem to be running at https://peppol.nets.no/oxalis/as2
```
1. Receiving AccessPoint is down, could be maintenance
1. Resolved by retrying later


### Non-existent receiver / documenttype combination in SMP
Typical exception and error message shown below.
```
java.lang.RuntimeException: Problem with SMP lookup for participant 9908:964966575 and document type urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.cenbii.eu:profile:biixx:ver2.0:extended:urn:www.difi.no:ehf:kreditnota:ver2.0::2.1
Caused by: eu.peppol.util.ConnectionException: Error reading URL data (404)
```
1. Delivery of this message has to be cancelled
1. Optionally contact receivers access point and ask if the receiver will be registered shortly

### Receiving access point unable to receive
Typical exception and error message shown below
```
java.lang.IllegalStateException: AS2 transmission failed : The following headers were received:
date: on, 01 mar 2017 09:50:07 +0100
message-id: 8f58e6d6-e9d0-4b71-9b9c-9b43285f5ec4
subject: AS2 message from OXALIS
content-type: multipart/signed; protocol="application/pkcs7-signature"; micalg=sha-1;	boundary="----=_Part_9_2022012779.1488358207846"
as2-from: APP_00000000000
as2-to: APP_99999999999
disposition-notification-to: not.in.use@unit4.com
disposition-notification-options: signed-receipt-protocol=required,pkcs7-signature; signed-receipt-micalg=required,sha1
as2-version: 1.0
host: ap.somewhere.no
connection: Keep-Alive
user-agent: Apache-HttpClient/4.5.2 (Java/1.8.0_111)
accept-encoding: gzip,deflate
content-length: 37908

The message sent to AS2 System id APP_0000000000 on Wed, 01 Mar 2017 08:49:54 +0000 with subject AS2 MDN as you requested has been received.
It has been processed 
The warning/error message is :
ERROR: Unable to get content of message.can't extract input stream: java.io.IOException: No space left on device
```
1. Manually decode the message to assess if it is any temporarily issue or permanent  
1. In this example it seems to be temporarily ("No space left on device"), safe to resend when problem has been fixed
