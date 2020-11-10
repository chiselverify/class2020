import chisel3._
import chiseltest._
import org.scalatest._
import concurrent._

// Test for testing in Assertion with timing project
// Compiles but test does not yet pass
class conAssertTest extends FlatSpec with ChiselScalatestTester with Matchers {
    behavior of "The mainClass"

    it should "test true condition" in {
        test(new mainClass()) {
            dut => {
                dut.io.s.poke(true.B)
                dut.clock.step(1)
                assert(true, "Error")
            }
        }
    }
    /*it should "test false condition" in {
        test(new conAssert(false.B, 10, "Error")) {
            dut => {
                dut.io.sigA.poke(true.B)
                dut.clock.step(1)
                dut.io.testp.expect(false.B)
            }
        }
    }
    it should "fail" in {
        test(new conAssert(true.B, 10, "Error")) {
            dut => {
                dut.io.sigA.poke(false.B)
                dut.clock.step(1)
                dut.io.testp.expect(true.B)
            }
        }
    }
    it should "test changing condition after 4" in {
        test(new conAssert(true.B, 10, "Error")) {
            dut => {
                dut.io.sigA.poke(true.B)
                dut.clock.step(1)
                dut.io.testp.expect(true.B)
                dut.clock.step(4)
                dut.io.sigA.poke(false.B)
                dut.clock.step(1)
                dut.io.testp.expect(true.B)
            }
        }
    }
    it should " fail test changing condition after 1" in {
        test(new conAssert(true.B, 10, "Error")) {
            dut => {
                dut.io.sigA.poke(true.B)
                dut.clock.step(1)
                dut.io.testp.expect(true.B)
                dut.clock.step(1)
                dut.io.sigA.poke(false.B)
                dut.clock.step(1)
                dut.io.testp.expect(true.B)
            }
        }
    }
    it should "test changing condition after 1 and then back" in {
        test(new conAssert(true.B, 10, "Error")) {
            dut => {
                dut.io.sigA.poke(true.B)
                dut.clock.step(1)
                dut.io.testp.expect(true.B)
                dut.clock.step(1)
                dut.io.sigA.poke(false.B)
                dut.clock.step(1)
                dut.io.testp.expect(false.B)
                dut.clock.step(1)
                dut.io.sigA.poke(true.B)
                dut.clock.step(1)
                dut.io.testp.expect(true.B)
            }
        }
    }
    it should "be false due to too many cycles" in {
        test(new conAssert(true.B, 10, "Error")) {
            dut => {
                dut.clock.step(11)
                dut.io.sigA.poke(true.B)
                dut.clock.step(1)
                dut.io.testp.expect(true.B)
            }
        }
    }*/
}