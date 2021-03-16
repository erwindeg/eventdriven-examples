#!/bin/bash
echo "start"
set -o xtrace
while true
do
   curl --header "Content-Type: application/json" \
  --request POST \
  --data '{
  "userId": "user2",
  "fromCurrency": "EUR",
  "toCurrency": "BTC",
  "amount": "0.001"
}'   http://localhost:8080/quote
done
