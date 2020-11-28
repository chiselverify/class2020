/*import org.scalatest._
import assertionTiming._
import chisel3._
import chiseltest._

// Obsolete test class, needs revising
class oneHotTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "assertOneHot"

  it should "test count of bitset in binary" in {
    test(new mainOneHot()) {
      dut => {
        dut.io.s.poke("b10".U)
        dut.clock.step(1)
        assertOneHot("b1000".U, "Error")
        dut.io.c.expect("b0100".U)
      }
    }
  }

  it should "test count of bitset in binary concurrently" in {
    test(new mainOneHot()) {
      dut => {
        dut.io.s.poke("b10".U)
        dut.clock.step(1)
        assertAlways(dut, () => assertOneHot(dut.io.c.peek), 10)
        dut.clock.step(2)
        dut.io.s.poke("b11".U)
        dut.clock.step(2)
        dut.io.s.poke("b00".U)
      }
    }
  }
}*/

