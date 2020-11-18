import chisel3._
import chiseltest._
import org.scalatest._

class timescopeTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The mainClass"

  it should "test timescope anomaly" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(4.U)
        dut.clock.step(1)
        fork {
            assert(dut.io.c.peek.litValue() == 4)
            dut.clock.step(1)
        }
        dut.clock.step(1)
        dut.io.s.poke(4.U)
      }
    }
  }
}