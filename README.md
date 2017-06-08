# Postcode checker and importer

#### Task 1

Implemented a 

See nhs.PostCodeChecker & nhs.PostCodeCheckerTest

#### Task 2

See nhs.CsvProcessor


#### Task 3

Performance was measured by taking time

Improvements:



#### How to build

`./gradlew clean test assembleDist`

Produces a deployable tar/zip in the build/dist folder

#### How to run

Either unzip/untar the released bundle at:

Then run the command:
`./postcode/bin/postcode [csv_file_to_import]`

Or you can run directly from the checked out code:
`./gradlew run -Dargs="[csv_file_to_import]"`
