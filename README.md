# Postcode checker and importer

## Task 1

Implemented using Groovy which uses 2.3.11 and requires Java 8; see nhs.PostCodeChecker & nhs.PostCodeCheckerTest

#### How to build

`./gradlew clean test assembleDist`

Which:
* Builds the code
* Runs the test against the example scenarios described task gist
* Builds a jar and distribution tar/zip (used in task 2 & 3)

#### Analysis of Ommissions

The supplied Regex will not cover (based on the wiki article):
* Crown dependencies (does support GY)
* Overseas territories
* British Forces Post Office
* National Health Services pseudo-postcodes

## failed_validation_0

Implemented in nhs.CsvProcessor generates the following file outputs:
* failed_validation_0.csv & succeeded_validation_0.csv (unsorted)
* failed_validation_1.csv & succeeded_validation_1.csv (post-sorted)
* failed_validation_2.csv & succeeded_validation_3.csv (pre-sorted)
In addition it outputs the following information to the console:
```
<++++++++++++++++++++++++++++++++++++++++++++++++++++++>
Time spent on valid 2468ms, time spent on failed 181ms
=> Total time when unsorted 5823ms
<++++++++++++++++++++++++++++++++++++++++++++++++++++++>
Processing Time 5520ms
Writing and Sorting Time 13447ms
=> Total time when post sorted 18979ms
<++++++++++++++++++++++++++++++++++++++++++++++++++++++>
Processing Time 9269ms
Writing Time 2029ms
=> Total time when pre sorted 11320ms
<++++++++++++++++++++++++++++++++++++++++++++++++++++++>
```

#### How to run

Either unzip/untar the released bundle at:

Then run the command:
`./postcode/bin/postcode [csv_file_to_import]`

Or follow the steps in Task 1 to build then you can run directly from the checked out code:
`./gradlew run -Pargs="[csv_file_to_import]"`

## Task 3

Performance was measured by taking time snapshots using the following closure at strategic points in the code:
```groovy 
    static def withTimed(Closure closure) {
        Instant start = Instant.now()
        closure.call()
        Instant end = Instant.now()
        Duration.between(start, end).toMillis()
    }
```

#### Performance Analysis / Improvements
Its fairly obvious from the timings is that the sorting is the most expensive operation.
Given that the raw unsort processing takes roughly 5s and 22s to sort from an in-memory this is the area I opted to tackle.

By far and away the cheapest option to resolve the sorting (on \*nix machines) is to take the unsorted failed_validation_0.csv and apply the following command:
```bash
sort -t ',' -k 1 -n failed_validation_0.csv > failed_validation_0_sorted.csv
```

However performance of the groovy implementation can be increased by using TreeMap (Java standard Collection) which sorts as the items are added.

The difference between the two approaches can be seen in the console output above from task 2; a saving of roughly 7s.

Looking forward if the dataset was to grow larger that memory could become an issue (depending on target platform) - as everything has to be held in memory in this naive implementation. In addition TreeMap may not be the optimal solution for sorting if the dataset grows.

At that point it would possible be necessary to pipe the output into fixed size buckets (individual files). 
Then sort the buckets and then recombine the buckets together in a stream map / reduce manner.
This approach would allow the buckets to be streamed and sorted against each other as they have been sorted individually into a single file.
Thus allowing a greater size of data.
