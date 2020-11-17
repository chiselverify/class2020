package gtfo

import scala.util.control.Breaks._
import org.scalatest._
import chisel3._
import chiseltest._

/** Checks for a condition to be valid in the circuit at all times, or within
    * the specified amount of clock cycles. If the condition evaluates to false,
    * the circuit simulation stops with an error. The package contains the following
    * functions:
    *
    * assertNever - checks for the condition to never be true in the number of cycles
    *
    * @param cond condition, assertion fires (simulation fails) when false
    * @param message optional format string to print when assertion fires
    * @param cycles optional amount of clock cycles for which the assertion is 
    * checked, instead of an immediate assertion
    *
    * This object is part of the special course "Verification of Digital Designs"
    * on DTU, autumn semester 2020.
    *
    * @author Victor Alexander Hansen, s194027@student.dtu.dk
    * @author Niels Frederik Flemming Holm Frandsen, s194053@student.dtu.dk
    */

/** 
 * Funktioner:
 * assert_never - a window in which an event is not expected
 * assert_always - a window where an event is expected
 * assert_eventually - a window bounding a check for a liveness property violation
 * assert_eventually_always - like the beforementioned assertion, except that once the property occurs, 
 * it must remain valid until the ending event-trigger or the end of the simulation occurs
 * assert_one_hot - checks for one hot encoding violations
*/

/** assertNever():
  * Checks for the argument condition to not be true in the number of cycles passed
  */
object assertNever {
    def apply(cond: Boolean, message: String, cycles: Int) {

        for (i <- 0 until cycles) {
            assert(!cond, message)
        }
    }
}

/** assertAlways():
  * Checks for the argument condition to be true in the number of cycles passed
  */
object assertAlways {
    def apply(cond: Boolean, message: String, cycles: Int) {
        fork {
            for (i <- 0 until cycles) {
                assert(cond == true, message)

            }
        }
    }
}

// assert_eventually - a window bounding a check for a liveness property violation
/** assertEventually():
  * Checks for the argument condition to not be true in the number of cycles passed
  */
object assertEventually {
    def apply(cond: Boolean, message: String, cycles: Int) {

        for (i <- 0 until cycles) {
            if (cond) {
                break
            } else {
                // Exception
                assert(false, message)
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
                break
            } else {
                // Exception
                assert(false, message)
            }
            k += 1
        }

        for (j <- 0 until cycles - k) {
            assert(cond, message)
        }
    }
}

// assert_one_hot - checks for one hot encoding violations
object assertOneHot {
    def apply(cond: UInt, message: String, cycles: Int) {

        for (i <- 0 until cycles) {
            assert(cond == 1.U << cond/2.U)
        }
    }
}
