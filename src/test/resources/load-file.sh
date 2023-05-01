#!/bin/sh

results=out.txt
echo "start" > $results

#while IFS= read -r line; do
#  printf '%s\n' "==> $line"
#  curl --request POST -sL -w '\n' \
#       --url 'http://localhost:8080/load' \
#       --header "Content-Type:application/json" \
#       --user user:password \
#       --data "$line" | tee -a $results
#done < input.txt

#xargs -rd'\n' -I@ -a input.txt printf '%s\n' @
xargs -rd'\n' -I@ -a input.txt curl --request POST -sL -w '\n' --url 'http://localhost:8080/load' --header "Content-Type:application/json" --user user:password --data @ | tee -a $results

#head -n 15 input.txt | xargs -rd'\n' -I@ printf '%s\n' @
#head -n 15 input.txt | xargs -rd'\n' -I@ curl --request POST -sL -w '\n' --url 'http://localhost:8080/load' --header "Content-Type:application/json" --user user:password --data @ | tee -a $results

echo "end" >> $results
