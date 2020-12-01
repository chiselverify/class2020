package assertionTiming

import scala.util.control.Breaks._
import org.scalatest._
import chiseltest._
import chisel3._

/** Checks for a condition to be valid in the circuit at all times, or within the specified amount of clock cycles.
  * If the condition evaluates to false, the circuit simulation stops with an error.
  *
  * @param dut the design under test
  * @param cond optional, condition, assertion fires (simulation fails) when false. Is passed as an anonymous function
  * @param cycles optional, amount of clock cycles for which the assertion is checked, instead of an immediate assertion
  * @param message optional, format string to print when assertion fires
  * @param event optional, a trigger event that will signal the end of the assertion
  * @param signal optional, an unsigned integer which serves as part of the condition
  *
  * This package is part of the special course "Verification of Digital Designs" on DTU, autumn semester 2020.
  *
  * @author Victor Alexander Hansen, s194027@student.dtu.dk
  * @author Niels Frederik Flemming Holm Frandsen, s194053@student.dtu.dk
  */

/** assertNever():
  * Checks for the argument condition to not be true in the number of cycles passed
  */
object assertNever {
    def apply[T <: Module](dut: T, cond: () => Boolean = () => true, cycles: Int = 1, message: String = "Error") = {

        // Assertion for single thread clock cycle 0
        assert(!cond(), message)
        dut.clock.step(1)
        fork {
            for (i <- 1 until cycles) {
                assert(!cond(), message)
                dut.clock.step(1)
            }
        }
    }
}

// Event based version of assertNever
object assertNeverEvent {
    def apply[T <: Module](dut: T, cond: () => Boolean = () => true, event: Boolean = false, message: String = "Error") = {

        // Assertion for single thread clock cycle 0
        assert(!cond(), message)
        dut.clock.step(1)
        fork {
            while (!event) {
                assert(!cond(), message)
                dut.clock.step(1)
            }
        }
    }
}

/** assertAlways():
  * Checks for the argument condition to be true in the number of cycles passed
  */
object assertAlways {
    def apply[T <: Module](dut: T, cond: () => Boolean = () => true, cycles: Int = 1, message: String = "Error") = {

        // Assertion for single thread clock cycle 0
        assert(cond(), message)
        dut.clock.step(1)
        fork {
            for (i <- 1 until cycles) {
                assert(cond(), message)
                dut.clock.step(1)
            }
        }
    }
}

// Event based version of assertAlways
object assertAlwaysEvent {
    def apply[T <: Module](dut: T, cond: () => Boolean = () => true, event: Boolean = false, message: String = "Error") = {

        // Assertion for single thread clock cycle 0
        assert(cond(), message)
        dut.clock.step(1)
        fork {
            while (!event) {
                assert(cond(), message)
                dut.clock.step(1)
            }
        }
    }
}

/** assertEventually():
  * Checks for the argument condition to be true just once within the number of
  * clock cycles passed, a liveness property. Fails if the condition is not true
  * at least once within the window of cycles
  */
object assertEventually {
    def apply[T <: Module](dut: T, cond: () => Boolean = () => true, cycles: Int = 1, message: String = "Error") = {

        var i = 0

        assert(!cond(), message)
        dut.clock.step(1)
        fork {
            while (!cond()) {
                if (i == cycles) {
                    assert(false, message)
                }
                i += 1
                dut.clock.step(1)
            }
        }
    }
}

// Event based version of assertEventually
object assertEventuallyEvent {
    def apply[T <: Module](dut: T, cond: () => Boolean = () => true, event: Boolean = false, message: String = "Error") = {

        fork {
            while (!cond()) {
                if (event) {
                    assert(false, message)
                }
                dut.clock.step(1)
            }
        }
    }
}

/** assertEventuallyAlways():
  * Checks for the argument condition to be true within the number of
  * clock cycles passed, and hold true until the last cycle. Fails if the 
  * condition is not true at least once within the window of cycles, or if
  * condition becomes false after it becomes true.
  */
object assertEventuallyAlways {
    def apply[T <: Module](dut: T, cond: () => Boolean = () => true, cycles: Int = 1, message: String = "Error") = {

        var i = 0
        var k = 0

        fork {
            while (!cond()) {
                if (i == cycles) {
                    assert(false, message)
                }
                i += 1
                dut.clock.step(1)
            }

            for (j <- 0 until cycles - i) {
                assert(cond(), message)
                dut.clock.step(1)
            }
        }
    }
}

// Event based version of assertEventuallyAlways
object assertEventuallyAlwaysEvent {
    def apply[T <: Module](dut: T, cond: () => Boolean = () => true, event: Boolean = false, message: String = "Error") = {

        var i = 1

        fork {
            while (!cond()) {
                if (event) {
                    assert(false, message)
                }
                i += 1
                dut.clock.step(1)
            }

            while (!event) {
                assert(cond(), message)
                dut.clock.step(1)
            }
        }
    }
}

/** assertOneHot():
  * checks if exactly one bit of the expression is high
  * This can be combined with any of the other assertions
  * because it returns a boolean value.
  */
object assertOneHot {
    def apply(signal: UInt = "b0001".U, message: String = "Error") : Boolean = {
        
        var in = signal.litValue
        var i = 0
        
        while ((in > 0)) {
            if ((in & 1) == 1) {
                i = i + 1
            }
            in = in >> 1
            if ((i > 1) == true) {
                assert(false, message)
            }
        }

        return true
    }
}

/** assertOneCycle():
  * asserts the passed condition after stepping x clock cycles after the fork
  */
object assertOnCycle {
    def apply[T <: Module](dut: T, cond: () => Boolean = () => true, cycles: Int = 1, message: String = "Error") = {
        fork {
            dut.clock.step(cycles)
            assert(cond(), message)
        }
    }
}