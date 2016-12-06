#!/usr/bin/env bash

#
# This is a sample showing how you can upload an invoice to the ringo server
#
#!/bin/sh

DIRECTORY=`dirname $0`
cd $DIRECTORY

RECEIVER="9908:810418052"
SAMPLE_INVOICE="sample-invoice.xml"

PEPPOL_BIS_INVOICE="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1"
       EHF_INVOICE="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1"

DOC_TYPE_ID="$EHF_INVOICE"

while getopts "d:r:f:" opt; do

	case $opt in
	    d)  if [[ "$OPTARG" == "BIS" ]]; then
	            DOC_TYPE_ID="$PEPPOL_BIS_INVOICE"
           fi
           ;;
		r)
			RECEIVER=$OPTARG
			echo "Sending to $RECEIVER"
		;;

		f)
		    SAMPLE_INVOICE=$OPTARG
       ;;
	esac
done

if [ ! -r "$SAMPLE_INVOICE" ]; then
    echo "$SAMPLE_INVOICE not found"
    exit 4
fi

echo "Sending $SAMPLE_INVOICE"
echo "Using doc type id $DOC_TYPE_ID"

curl -i -u sr:ringo1 \
  -H 'Accept: application/xml'  \
  -F file=@$SAMPLE_INVOICE \
  -F 'SenderID=9908:976098897' \
  -F RecipientID="${RECEIVER}" \
  -F 'ChannelID=PEPPOL' \
  -F 'ProcessID=urn:www.cenbii.eu:profile:bii04:ver2.0' \
  -F DocumentID=$DOC_TYPE_ID \
    http://localhost:8080/vefa-srest/outbox
