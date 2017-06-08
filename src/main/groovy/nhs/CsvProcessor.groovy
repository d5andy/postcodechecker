package nhs

import java.time.Duration
import java.time.Instant

/**
 * sort -t ',' -k 1 -n failed_validation.csv > sorted.txt
 */
class CsvProcessor {

    static void main(String[] args) {
        def file = args ? args.first() : '/Users/david.sanderson/Downloads/import_data.csv'

        println '<++++++++++++++++++++++++++++++++++++++++++++++++++++++>'
        println '=> Total time when unsorted ' + withTimed {
            new CsvProcessor().processUnsorted(file) //3844
        } + 'ms'
        println '<++++++++++++++++++++++++++++++++++++++++++++++++++++++>'
        println '=> Total time when post sorted ' + withTimed {
            new CsvProcessor().processPostSorted(file) //12554

        } + 'ms'
        println '<++++++++++++++++++++++++++++++++++++++++++++++++++++++>'
        println '=> Total time when pre sorted ' + withTimed {
            new CsvProcessor().processPreSorted(file) //12568
        } + 'ms'
        println '<++++++++++++++++++++++++++++++++++++++++++++++++++++++>'
    }

    void processUnsorted(String file) {
        def validFile = new File('succeeded_validation_0.csv')
        validFile.delete()
        def validWriter = validFile.newWriter()

        def failedFile = new File('failed_validation_0.csv')
        failedFile.delete()
        def failedWriter = failedFile.newWriter()

        long validTime = 0
        long failedTime = 0

        new File(file).eachLine(1) { line, counter ->
            if (counter == 1) return
            def postcode = line.substring(line.indexOf(',') + 1, line.length())
            def result = null
            def time =+ withTimed {
                result = PostCodeChecker.check(postcode)
            }

            if (result.good) {
                validTime += time
                validWriter << line + '\n'
            } else {
                failedTime += time
                failedWriter << line + ',' + result.reason + '\n'
            }
        }

        println 'Time spent on valid ' + validTime + 'ms, time spent on failed ' + failedTime + 'ms'
    }

    void processPostSorted(String file) {

        def validFile = new File('succeeded_validation_1.csv')
        validFile.delete()

        def failedFile = new File('failed_validation_1.csv')
        failedFile.delete()

        List<List<String>> validList = []
        List<List<String>> failedList = []

        long procMillis = withTimed {
            new File(file).eachLine(1) { line, counter ->
                if (counter == 1) return
                def row = line.split(',')
                def result = PostCodeChecker.check(row.last())
                if (result.good) {
                    validList << row
                } else {
                    failedList << (row + result.reason)
                }
            }
        }
        println 'Processing Time ' + procMillis + 'ms'

        long writingMillis = withTimed {
            validFile.withWriter { writer ->
                validList.sort { a, b -> a.first().toInteger() <=> b.first().toInteger()} .each { line ->
                    writer << line.join(',') + '\n'
                }
            }

            failedFile.withWriter { writer ->
                failedList.sort { a, b -> a.first().toInteger() <=> b.first().toInteger()}.each { line ->
                    writer << line.join(',') + '\n'
                }
            }
        }
        println 'Writing and Sorting Time ' + writingMillis + 'ms'
    }

    void processPreSorted(String file) {

        def validFile = new File('succeeded_validation_2.csv')
        validFile.delete()

        def failedFile = new File('failed_validation_2.csv')
        failedFile.delete()

        def validList = new TreeMap()
        def failedList = new TreeMap()

        long millis = withTimed {
            new File(file).eachLine(1) { line, counter ->
                if (counter == 1) return
                List row = line.split(',')
                def result = PostCodeChecker.check(row.last())
                if (result.good) {
                    validList.put(row.first() as Integer, row)
                } else {
                    failedList.put(row.first() as Integer, (row << result.reason))
                }
            }
        }

        println 'Processing Time ' + millis + 'ms'

        long writingMillis = withTimed {
            validFile.withWriter { writer ->
                validList.each { line ->
                    writer << line.value.join(',') + '\n'
                }
            }

            failedFile.withWriter { writer ->
                failedList.each { line ->
                    writer << line.value.join(',') + '\n'
                }
            }
        }
        println 'Writing Time ' + writingMillis + 'ms'

    }

    static def withTimed(Closure closure) {
        Instant start = Instant.now()
        closure.call()
        Instant end = Instant.now()
        Duration.between(start, end).toMillis()
    }

}
