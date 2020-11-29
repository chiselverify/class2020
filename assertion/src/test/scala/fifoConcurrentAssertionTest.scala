import org.scalatest._
import assertionTiming._
import chisel3._
import chiseltest._
import scala.util._


  class fifoConcurrentAssertionTest extends FlatSpec with ChiselScalatestTester with Matchers {
    behavior of "The fifoConcurrentAssertionTest"

    it should "test if move data from input to output" in {
      test(new BubbleFifo(32,8)) {
        dut => {
          val randNum = new Random(65541891).nextInt(429467295)
          val enq = dut.io.enq
          val deq = dut.io.deq

          enq.write.poke(true.B)
          deq.read.poke(false.B)
          enq.din.poke(randNum.U)
          System.out.println(randNum)
          dut.clock.step(1)
          enq.write.poke(false.B)
          // Somehow doesn't work?
          assert(deq.dout.peek.litValue == 0)
          /*assertAlwaysEvent(dut, () => deq.dout.peek.litValue == 0, 
            deq.read == true.B, "Error, output is " + deq.dout.peek.litValue)*/

          while (deq.notReady.peek.litValue == 1) {
            System.out.println("dout peek is " + deq.dout.peek.litValue)
            dut.clock.step(1)
          }

          deq.dout.expect(randNum.U)
          dut.clock.step(1)
          deq.read.poke(true.B)
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

it should "writes and reads data concurrently" in {
    test(new BubbleFifo(32, 4)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq
        val randArray = Array.fill(4) {new Random(65541891).nextInt(429467295)}
        
        // Function fills dataReg with a queue of random numbers
        def sender() {
          for (i <- 0 until 4) {
            enq.write.poke(true.B)
            enq.din.poke(randArray(i).U)
            dut.clock.step(1)
            enq.write.poke(false.B)
            while (enq.busy.peek.litValue == 1){
              dut.clock.step(1)
            }
          }
        }

        // Function reads the queue of random numbers
        def receiver() {
          for (i <- 0 until 4) {
            deq.read.poke(false.B)
            while(deq.notReady.peek.litValue == 1){
              dut.clock.step(1)
            }
            deq.dout.expect(randArray(i).U)
            deq.read.poke(true.B)
            dut.clock.step(1)
          }
        }

        fork {
          assertEventuallyAlwaysEvent(dut, () => enq.busy == true.B, deq.notReady == true.B)
          sender()
        }
        assertEventuallyAlwaysEvent(dut, () => deq.notReady == true.B, enq.busy == true.B)
        receiver()
      }
    }
  }



  }


