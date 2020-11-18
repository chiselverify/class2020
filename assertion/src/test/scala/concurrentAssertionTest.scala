import org.scalatest._
import assertionTiming._
import chisel3._
import chiseltest._

class concurrentAssertionTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The mainClass"

  it should "test an assertion to always be true" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(4.U)
        dut.clock.step(1)
        val t = assertAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
        
        timescope {
          dut.clock.step(1)
          dut.io.s.poke(4.U)
          System.out.println("Start")
          dut.clock.step(1)
        }
          
        timescope {
        dut.clock.step(100)
        System.out.println("Slut")
        //dut.io.s.poke(4.U)
        //t.join
        }
      }
    }
  }
}