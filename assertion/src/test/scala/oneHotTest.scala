import org.scalatest._
import assertionTiming._
import chisel3._
import chiseltest._

// Obsolete test class, needs revising
class oneHotTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "assertOneHot"

  it should "test decoding" in {
    test(new mainOneHot()) {
      dut => {
        dut.io.dec.poke(true.B)
        dut.io.data.poke("b01".U)
        dut.clock.step(1)
        assertOneHot("b0010".U)
      }
    }
  }
it should "test decoding and FAIL" in {
    test(new mainOneHot()) {
      dut => {
        dut.io.dec.poke(true.B)
        dut.io.data.poke("b01".U)
        dut.clock.step(1)
        assertOneHot("b0110".U, "Decoding yields " + dut.io.dout.peek)
      }
    }
  }


  it should "test encoding" in {
    test(new mainOneHot()) {
      dut => {
        dut.io.enc.poke(true.B)
        dut.clock.step(1)
        dut.io.data.poke("b0100".U)
        dut.clock.step(1)
        dut.io.dout.expect("b10".U)
        //assertOneHot("b10".U)
      }
    }
  }
}

