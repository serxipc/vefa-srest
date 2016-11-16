#!/usr/bin/env bash

#
# This is a sample showing how you can upload an invoice to the ringo server
#
#!/bin/sh

DIRECTORY=`dirname $0`
echo "Sending $DIRECTORY/sample-invoice.xml"
cd $DIRECTORY

RECEIVER="9908:810418052"

while getopts "r:" opt; do

	case $opt in
		r)
			RECEIVER=$OPTARG
			echo "Sending to $RECEIVER"
		;;
	esac
done

curl -i -u sr:ringo1 \
  -H 'Accept: application/xml'  \
  -F 'file=@sample-invoice.xml' \
  -F 'SenderID=9908:976098897' \
  -F RecipientID="${RECEIVER}" \
  -F 'ChannelID=PEPPOL' \
  -F 'ProcessID=urn:www.cenbii.eu:profile:bii04:ver2.0' \
  -F 'DocumentID=urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1' \
    http://localhost:8080/vefa-srest/outbox