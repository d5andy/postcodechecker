package nhs

import spock.lang.Specification
import spock.lang.Unroll

class PostCodeCheckerTest extends Specification {

    @Unroll
    def 'for "#postcode" post code checker returns correct reason : "#reason"'() {

        when:
            def result = PostCodeChecker.check(postcode)

        then:
            result.reason == reason

        where:
            postcode   | reason
            '$%± ()()' | "Junk"
            'XX XXX'   | "Invalid"
            'A1 9A'    | "Incorrect inward code length"
            'LS44PL'   | "No space"
            'Q1A 9AA'  | "'Q' in first position"
            'V1A 9AA'  | "'V' in first position"
            'X1A 9BB'  | "'X' in first position"
            'LI10 3QP' | "'I' in second position"
            'LJ10 3QP' | "'J' in second position"
            'LZ10 3QP' | "'Z' in second position"
            'A9Q 9AA'  | "'Q' in third position with 'A9A' structure"
            'AA9C 9AA' | "'C' in fourth position with 'AA9A' structure"
            'FY10 4PL' | "Area with only single digit districts"
            'SO1 4QQ'  | "Area with only double digit districts"
            'EC1A 1BB' | "None"
            'W1A 0AX'  | "None"
            'M1 1AE'   | "None"
            'B33 8TH'  | "None"
            'CR2 6XH'  | "None"
            'DN55 1PT' | "None"
            'GIR 0AA'  | "None"
            'SO10 9AA' | "None"
            'FY9 9AA'  | "None"
            'WC1A 9AA' | "None"
    }
}
