import chisel3._
import chiseltest._
import org.scalatest._

/**
  * Author: Martin Schoeberl (martin@jopdesign.com)
  *
  * Just a template to get started with ChiselTest
  */

class MartinTester extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The BubbleFifo"

  it should "move data from input to output" in {
    test(new BubbleFifo(32, 4)) {
      dut => {
        dut.io.enq.din.poke(123.U)
        dut.clock.step()
        dut.reset.poke(0.B)
        dut.io.deq.notReady.expect(true.B)
      }
    }
  }

  it should "be notReady after reset" in {


  }


  it should "signal when full" in {
    assert(false, "not yet implemented")
  }

  it should "find yourself 3 more test cases to test" in {
    assert(1>2,"finished impl")//throw new Error("Missing tests")
  }

}
