package nhs

class PostCodeChecker {

    def static SINGLE_DIGIT_CODES = 'BR|FY|HA|HD|HG|HR|HS|HX|JE|LD|SM|SR|WC|WN|ZE'
    def static DOUBLE_DIGIT_CODES = 'AB|LL|SO'
    def static JUNK = '(?<junk>[^a-zA-Z\\d:]+)'
    def static INVALID = '(?<invalid>[^A-PR-UWYZ]+)'
    def static NO_SPACE = '(?<nospace>[^\\s]+)'
    def static NOT_DOUBLE_DIGIT = "(?<double>(${DOUBLE_DIGIT_CODES})[0-9]\\s.+)"
    def static NOT_SINGLE_DIGIT = "(?<single>(${SINGLE_DIGIT_CODES})[0-9][0-9]\\s.+)"
    def static INWARD_LENGTH = '(?<inward>[A-Z][0-9]+\\s[0-9][ABD-HJLNP-UW-Z])'
    def static INVALID_FIRST = '(?<first>([^A-PR-UWYZ][0-9][A-HJKPSTU0-9]?\\s.+|[^A-PR-UWYZ][A-HK-Y][0-9][ABEHMNPRVWXY0-9]?\\s.+))'
    def static INVALID_SECOND = '(?<second>[A-PR-UWYZ][^A-HK-Y0-9]([0-9][ABEHMNPRVWXY0-9]?\\s.+))'
    def static INVALID_THIRD = '(?<third>[A-PR-UWYZ][0-9][^A-HJKPSTUW0-9\\s]\\s.+)'
    def static INVALID_FOURTH = '(?<fourth>[A-PR-UWYZ][A-HK-Y][0-9][^ABEHMNPRVWXY0-9\\s]\\s.+)'
    def static INVALID_CHECKS = [JUNK, INVALID, NO_SPACE, NOT_DOUBLE_DIGIT, NOT_SINGLE_DIGIT, INWARD_LENGTH,
                                 INVALID_FIRST, INVALID_SECOND, INVALID_THIRD, INVALID_FOURTH]

    def static VALID_POSTCODE_PATTERN = ~/(GIR\s0AA)|((([A-PR-UWYZ][0-9]{1,2})|(([A-PR-UWYZ][A-HK-Y][0-9](?<!(${SINGLE_DIGIT_CODES})[0-9])[0-9])|([A-PR-UWYZ][A-HK-Y](?<!${DOUBLE_DIGIT_CODES})[0-9])|(WC[0-9][A-Z])|(([A-PR-UWYZ][0-9][A-HJKPSTUW])|([A-PR-UWYZ][A-HK-Y][0-9][ABEHMNPRVWXY]))))\s[0-9][ABD-HJLNP-UW-Z]{2})/
    def static INVALID_POSTCODE_PATTERN = ~/${INVALID_CHECKS.join('|')}/

    static Result check(String postcode) {
        def validPostcode = VALID_POSTCODE_PATTERN.matcher(postcode)
        if (!validPostcode.matches()) {
            def invalidResult = INVALID_POSTCODE_PATTERN.matcher(postcode)
            if (invalidResult.matches()) {
                if (invalidResult.group('junk')) {
                    new Result('Junk')
                } else if (invalidResult.group('invalid')){
                    new Result('Invalid')
                } else if (invalidResult.group('nospace')) {
                    new Result('No space')
                } else if (invalidResult.group('single')) {
                    new Result('Area with only single digit districts')
                } else if (invalidResult.group('double')) {
                    new Result('Area with only double digit districts')
                } else if (invalidResult.group('inward')) {
                    new Result('Incorrect inward code length')
                } else if (invalidResult.group('first')) {
                    new Result("'${postcode[0]}' in first position")
                } else if (invalidResult.group('second')) {
                    new Result("'${postcode[1]}' in second position")
                } else if (invalidResult.group('third')) {
                    new Result("'${postcode[2]}' in third position with 'A9A' structure")
                } else if (invalidResult.group('fourth')) {
                    new Result("'${postcode[3]}' in fourth position with 'AA9A' structure")
                }
            } else  {
                new Result('UNKNOWN_INVALID')
            }
        } else {
            Result.SUCCESS
        }
    }
}
