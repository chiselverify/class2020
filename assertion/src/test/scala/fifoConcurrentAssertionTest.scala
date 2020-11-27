import org.scalatest._
import assertionTiming._
import chisel3._
import chiseltest._


  class fifoConcurrentAssertionTest extends FlatSpec with ChiselScalatestTester with Matchers {
    behavior of "The fifoConcurrentAssertionTest"

    it should "signal when full" in {
      test(new BubbleFifo(32, 4)) {
        dut => {
          val w = 0
          val r = new scala.util.Random
          val rand = r.nextInt(2147483647)
          dut.io.deq.read.poke(false.B)
          dut.io.enq.write.poke(true.B)
          for (w <- 0 to 4) {
            dut.io.enq.din.poke(rand.U)
            dut.clock.step(1)
          }
          dut.io.enq.busy.expect(true.B)
        }
      }
    }

    it should "test if" in {
      test(new BubbleFifo(32, 8)) {
        dut => {
          val w = 0
          dut.io.deq.read.poke(false.B)
          dut.io.enq.write.poke(true.B)
          dut.io.enq.din.poke(4.U)
          val t = assertEventually(dut, ()=>(dut.io.deq.dout.litValue == 4), 8, "Error")
          t.join
        }
      }
    }






  }


