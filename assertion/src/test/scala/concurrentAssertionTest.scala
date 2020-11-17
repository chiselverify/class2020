import org.scalatest._
import gtfo._
import chisel3._
import chiseltest._

class concurrentAssertionTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The mainClass"

  it should "test" in {
    test(new mainClass()) {
      dut => {
        def writer() {
            assertAlways((dut.io.c.peek.litValue().intValue() == 1), "Error", 20)
        }
        def eliminater() {
            
            dut.clock.step(1)
            dut.io.s.poke(0.U)
            
            
        }

        dut.io.s.poke(4.U)
        dut.clock.step(1)
        assertOneHot(4.U, "Error", 20)
        dut.clock.step(1)
        dut.io.s.poke(0.U)
        /*fork {
            writer()
        }
        eliminater()*/
      }
    }
  }
}