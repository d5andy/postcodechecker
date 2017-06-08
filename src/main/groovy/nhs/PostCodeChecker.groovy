package nhs

class PostCodeChecker {

    def static single_digit_codes = 'BR|FY|HA|HD|HG|HR|HS|HX|JE|LD|SM|SR|WC|WN|ZE'
    def static double_digit_codes = 'AB|LL|SO'
    def static junk = '(?<junk>[^a-zA-Z\\d:]+)'
    def static invalid = '(?<invalid>[^A-PR-UWYZ]+)'
    def static no_space = '(?<nospace>[^\\s]+)'
    def static not_double_digit = "(?<double>(${double_digit_codes})[0-9]\\s.+)"
    def static not_single_digit = "(?<single>(${single_digit_codes})[0-9][0-9]\\s.+)"
    def static inward_length = '(?<inward>[A-Z][0-9]+\\s[0-9][ABD-HJLNP-UW-Z])'
    def static invalid_first = '(?<first>([^A-PR-UWYZ][0-9][A-HJKPSTU0-9]?\\s.+|[^A-PR-UWYZ][A-HK-Y][0-9][ABEHMNPRVWXY0-9]?\\s.+))'
    def static invalid_second = '(?<second>[A-PR-UWYZ][^A-HK-Y0-9]([0-9][ABEHMNPRVWXY0-9]?\\s.+))'
    def static invalid_third = '(?<third>[A-PR-UWYZ][0-9][^A-HJKPSTUW0-9\\s]\\s.+)'
    def static invalid_fourth = '(?<fourth>[A-PR-UWYZ][A-HK-Y][0-9][^ABEHMNPRVWXY0-9\\s]\\s.+)'
    def static invalid_checks = [junk, invalid, no_space, not_double_digit, not_single_digit, inward_length,
                                 invalid_first, invalid_second, invalid_third, invalid_fourth]

    def static validPostcodePattern = ~/(GIR\s0AA)|((([A-PR-UWYZ][0-9]{1,2})|(([A-PR-UWYZ][A-HK-Y][0-9](?<!(${single_digit_codes})[0-9])[0-9])|([A-PR-UWYZ][A-HK-Y](?<!${double_digit_codes})[0-9])|(WC[0-9][A-Z])|(([A-PR-UWYZ][0-9][A-HJKPSTUW])|([A-PR-UWYZ][A-HK-Y][0-9][ABEHMNPRVWXY]))))\s[0-9][ABD-HJLNP-UW-Z]{2})/
    def static invalidPostCodePattern = ~/${invalid_checks.join('|')}/

    static Result check(String postcode) {
        def matcher = validPostcodePattern.matcher(postcode)
        if (!matcher.matches()) {
            def combineder = invalidPostCodePattern.matcher(postcode)
            if (combineder.matches()) {
                if (combineder.group('junk')) {
                    return new Result('Junk')
                } else if (combineder.group('invalid')){
                    return new Result('Invalid')
                } else if (combineder.group('nospace')) {
                    return new Result('No space')
                } else if (combineder.group('single')) {
                    return new Result('Area with only single digit districts')
                } else if (combineder.group('double')) {
                    return new Result('Area with only double digit districts')
                } else if (combineder.group('inward')) {
                    return new Result('Incorrect inward code length')
                } else if (combineder.group('first')) {
                    return new Result("'${postcode[0]}' in first position")
                } else if (combineder.group('second')) {
                    new Result("'${postcode[1]}' in second position")
                } else if (combineder.group('third')) {
                    new Result("'${postcode[2]}' in third position with 'A9A' structure")
                } else if (combineder.group('fourth')) {
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
