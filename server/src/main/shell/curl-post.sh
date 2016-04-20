#!/bin/bash

#
# Example showing how to upload a file using curl(1)
#
curl -F "file=@curl-post.sh;filename=curl-post.sh" \
     -F "RecipientID=9908:976098897" \
     -F "ChannelID=CH1" \
     -w "CODE: %{http_code}" \
    http://localhost:8080/ringo/peppol/account/42/outbox/

