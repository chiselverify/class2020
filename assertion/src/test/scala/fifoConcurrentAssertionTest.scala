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

    it should "test if assertions listed in test works simple circumstances" in {
      test(new BubbleFifo(32, 8)) {
        dut => {
          val w = 0
          dut.io.deq.read.poke(false.B)
          dut.io.enq.write.poke(true.B)
          dut.io.enq.din.poke(4.U)
          val s = assertEventually(dut, () => dut.io.deq.dout.peek.litValue == 4, 8, "Error")
          val t = assertEventuallyAlways(dut, () => dut.io.deq.dout.peek.litValue == 4, 8, "Error")
          val p = assertOnCycle(dut, () => dut.io.deq.dout.peek.litValue == 4, 8, "Error")
          s.join
          t.join
          p.join
        }
      }
    }
    it should "FAIL: test if assertOnCycle fails" in {
      test(new BubbleFifo(32, 8)) {
        dut => {
          val w = 0
          dut.io.deq.read.poke(false.B)
          dut.io.enq.write.poke(true.B)
          dut.io.enq.din.poke(4.U)
          val p = assertOnCycle(dut, () => dut.io.deq.dout.peek.litValue == 4, 7, "Shoud fail, no probs")
          p.join
        }
      }
    }
    it should "FAIL: test if assertions EventuallyAlways fails under" in {
      test(new BubbleFifo(32, 8)) {
        dut => {
          val w = 0
          dut.io.deq.read.poke(false.B)
          dut.io.enq.write.poke(false.B)
          dut.io.enq.din.poke(4.U)
          val t = assertEventuallyAlways(dut, () => dut.io.deq.notReady.peek.litValue == 1, 20, "Error")
          dut.clock.step(1)
          dut.io.deq.read.poke(true.B)
          dut.io.enq.write.poke(false.B)
          dut.clock.step(8)
          dut.io.deq.read.poke(false.B)
          dut.io.enq.write.poke(false.B)
          t.join
        }
      }
    }





  }


