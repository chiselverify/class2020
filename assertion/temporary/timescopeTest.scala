import chisel3._
import chiseltest._
import org.scalatest._

class timescopeTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The mainClass"

  it should "test timescope anomaly" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        fork {
          for (i <- 0 until 20) {
            assert(dut.io.c.peek.litValue() == 4, "Error")
            dut.clock.step(1)
          }
        }
        dut.clock.step(10)
        dut.io.s.poke(false.B)
      }
    }
  }
}
