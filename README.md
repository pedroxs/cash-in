# Cash-In

Validate transaction velocity

### Requirements
- JDK 17

### How to run
From the root project folder within the shell run:
```shell
./gradlew bootRun
```
This will start the application on port 8080
For this demo the username and password for the API is
`user` `password`

The embedded database can be accessed from the following url:
http://localhost:8080/h2-console/

For the JDBC URL use: `jdbc:h2:mem:cashin;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE` 
Username is `sa` and password is blank

### Loading transactions from file

Use this bash script [load-file.sh](src/test/resources/load-file.sh)
or run this integration test [LoadIntegrationTests](src/test/java/com/example/cashin/LoadIntegrationTests.java)
to load a file into the system. Each line of the file must be in the following format:

```json
{"id":"29255","customer_id":"494","load_amount":"$4601.23","time":"2000-02-12T13:45:18Z"}
```

An example input file is located [here](src/test/resources/input.txt) and
the accompanying output file is [here](src/test/resources/output.txt)

