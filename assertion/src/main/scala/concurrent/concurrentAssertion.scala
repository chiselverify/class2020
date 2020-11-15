package concurrent

import scala.util.control.Breaks._

/** 
 * Funktioner:
 * assert_never - a window in which an event is not expected
 * assert_always - a window where an event is expected
 * assert_eventually - a window bounding a check for a liveness property violation
 * assert_eventually_always - like the beforementioned assertion, except that once the property occurs, 
 * it must remain valid until the ending event-trigger or the end of the simulation occurs
 * assert_one_hot - checks for one hot encoding violations
*/

object assertNever {
    def apply(cond: Boolean, message: String, cycles: Int) {

        for (i <- 0 until cycles) {
            assert(!cond, "Error")
        }
    }
}

object assertAlways {
    def apply(cond: Boolean, message: String, cycles: Int) {

        for (i <- 0 until cycles) {
            assert(cond, "Error")
        }
    }
}

// assert_eventually - a window bounding a check for a liveness property violation
object assertEventually {
    def apply(cond: Boolean, message: String, cycles: Int) {

        for (i <- 0 until cycles) {
            if (cond) {
                System.out.println("Hurra")
                break
            } else {
                // Exception
                assert(false)
            }
        }
    }
}

// assert_eventually_always - like the beforementioned assertion, except that once the property occurs, 
// it must remain valid until the ending event-trigger or the end of the simulation occurs
object assertEventuallyAlways {
    def apply(cond: Boolean, message: String, cycles: Int) {

        var k = 0
        for (i <- 0 until cycles) {
            if (cond) {
                System.out.println("Hurra")
                break
            } else {
                // Exception
                assert(false)
            }
            k += 1
        }

        for (j <- 0 until cycles - k) {
            assert(cond, "Error")
        }
    }
}

// assert_one_hot - checks for one hot encoding violations
object assertOneHot {
    def apply(cond: Boolean, message: String, cycles: Int) {

        // Wtf
    }
}