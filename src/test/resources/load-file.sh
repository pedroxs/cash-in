#!/bin/bash

usage() {
  echo "Usage: ./load-file [options]"
  echo "  Make HTTP requests reading from file and optionally writing results on file"
  echo "Options:"
  echo "  -u --user      Username to make requests"
  echo "  -p --password  Password to make requests"
  echo "  -i --input     Input file to read data from"
  echo "  -o --output    Output file to store results"
  echo "  -d --dry       Do a dry-run, nothing is executed and prints input file lines only"
  echo "  -t --top       Only run for the top x lines from input file"
  echo "  -v --verbose   Also output results to stdout"
  echo "  -h --help      Prints this help"
  exit 1
}

input=input.txt
output=$(date +output_%F_%H-%M-%S.txt)
dry=0
top=-0
verbose=0
user=user
pass=password

while [[ "$#" -gt 0 ]]; do
    case $1 in
        -u|--user) user="$2"; shift ;;
        -p|--password) pass="$2"; shift ;;
        -i|--input) input="$2"; shift ;;
        -o|--output) output="$2"; shift ;;
        -d|--dry-run) dry=1 ;;
        -t|--top) top="$2"; shift ;;
        -v|--verbose) verbose=1 ;;
        -h|--help) usage ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done

if [[ $dry -eq 1 ]]; then
  echo "input: $input"
  echo "output: $output"
  echo "dry: $dry"
  echo "top: $top"
  echo "verbose: $verbose"
  echo "user: $user"
  echo "pass: $pass"

  head -n "$top" "$input" | xargs -rd'\n' -I@ printf '%s\n' @
  exit
fi

touch "$output"

if [ $verbose -eq 1 ]; then
  head -n "$top" "$input" | xargs -rd'\n' -I@ curl --request POST -sL -w '\n' --url 'http://localhost:8080/load' --header "Content-Type:application/json" --user "$user:$pass" --data @ | tee -a "$output"
  exit
fi

head -n "$top" "$input" | xargs -rd'\n' -I@ curl --request POST -sL -w '\n' --url 'http://localhost:8080/load' --header "Content-Type:application/json" --user "$user:$pass" --data @ >> "$output"
