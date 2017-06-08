package nhs

import groovy.transform.TupleConstructor

@TupleConstructor
class Result {

    static final Result SUCCESS = new Result('None', true)

    String reason
    boolean good = false
}
