import chisel3._
import chiseltest._
import org.scalatest._
import scala.util.Random

/**
  * Author: Hans Jakob Damsgaard (s163915@student.dtu.dk)
  */

// How do I extend my tests to other sizes? More classes? For loops in tests?
object Test {
  val width = 32
  val depth = 4
}

class HansTester extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The BubbleFifo"

  // Check that it actually propagates data
  it should "move data from input to output" in {
    val testData = ((BigInt(1) << Test.width) - 1).U
    test(new BubbleFifo(Test.width, Test.depth)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq
        deq.read.poke(false.B)
        enq.write.poke(true.B)
        enq.din.poke(testData)
        dut.clock.step()
        enq.write.poke(false.B)
        do dut.clock.step() while (deq.empty.peek.litValue == 1)
        deq.dout.expect(testData)
      }
    }
  }

  // Check that it satisfies the FIFO order of elements
  it should "satisfy the FIFO invariant" in {
    test(new BubbleFifo(Test.width, Test.depth)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq
        val rng = new Random(76556434)
        val mask = (BigInt(1) << Test.width) - 1
        val nums = Array.fill(Test.depth-1) { BigInt.apply(Test.width, rng) }.map(_ & mask ^ BigInt(1))
        // Fill some elements into the FIFO
        deq.read.poke(false.B)
        enq.write.poke(true.B)
        for (elem <- nums) {
          enq.din.poke(elem.U)
          do dut.clock.step() while (enq.full.peek.litValue == 1)
        }
        enq.din.poke(mask.U)
        dut.clock.step()
        // Check that elements are read out the same order
        deq.read.poke(true.B)
        enq.write.poke(false.B)
        for (elem <- nums) {
          deq.dout.expect(elem.U)
          do dut.clock.step() while (deq.empty.peek.litValue == 1)
        }
        deq.dout.expect(mask.U)
      }
    }
  }

  // Check that reset empties the queue
  it should "be empty after reset" in {
    test(new BubbleFifo(Test.width, Test.depth)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq
        // Clear the FIFO
        dut.reset.poke(true.B)
        enq.write.poke(false.B)
        enq.din.poke(0.U)
        deq.read.poke(false.B)
        dut.clock.step()
        deq.empty.expect(true.B)
        deq.dout.expect(0.U)
        enq.full.expect(false.B)
      }
    }
  }

  // Fill up the FIFO and check that it signals being full correctly
  it should "signal when full and empty" in {
    test(new BubbleFifo(Test.width, Test.depth)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq
        // Fill up the FIFO
        deq.read.poke(false.B)
        enq.write.poke(true.B)
        enq.din.poke(1.U)
        while (deq.dout.peek.litValue == 0 || enq.full.peek.litValue == 0)
          dut.clock.step()
        enq.full.expect(true.B)
        // Empty out the FIFO
        enq.write.poke(false.B)
        deq.read.poke(true.B)
        while (deq.dout.peek.litValue != 0 || deq.empty.peek.litValue == 0)
          dut.clock.step()
        deq.empty.expect(true.B)
      }
    }
  }

  // Check that bubbles are squashed in the FIFO
  it should "squash bubbles" in {
    test(new BubbleFifo(Test.width, Test.depth)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq
        
        /* First find the sequential read out latency of the queue */
        enq.din.poke(1.U)
        enq.write.poke(true.B)
        deq.read.poke(false.B)
        for (i <- 0 until 2)
          do dut.clock.step() while (enq.full.peek.litValue == 1)
        enq.write.poke(false.B)
        while (deq.empty.peek.litValue == 1) dut.clock.step()
        deq.dout.expect(1.U)
        deq.read.poke(true.B)
        var count = 0
        do {
          dut.clock.step()
          count += 1
        } while (deq.empty.peek.litValue == 1)
        deq.dout.expect(1.U)
        dut.clock.step()
        println("Latency is " + count + " cycles")

        /* Now perform the actual test using the result from above */
        // Add one entry to the FIFO
        enq.din.poke(42.U)
        enq.write.poke(true.B)
        deq.read.poke(false.B)
        dut.clock.step()
        enq.write.poke(false.B)
        // Wait some cycles
        dut.clock.step(Test.depth * 2)
        // Add another entry to the FIFO
        enq.din.poke(13.U)
        enq.write.poke(true.B)
        dut.clock.step()
        enq.write.poke(false.B)
        // Wait some more cycles
        dut.clock.step(Test.depth * 2)
        // Bubbles should be squashed by now; read out entries one after another
        deq.dout.expect(42.U)
        deq.read.poke(true.B)
        dut.clock.step(count)
        deq.dout.expect(13.U)
      }
    }
  }

  // Run a concurrent test with separate producer and consumer
  it should "run with concurrent prod. and cons." in {
    test(new BubbleFifo(Test.width, Test.depth)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq
        val rng = new Random(12345678)
        val mask = (BigInt(1) << Test.width) - 1
        val randInputs = Array.fill(128) { BigInt.apply(Test.width, rng) }.map(_ & mask)

        def producer() = {
          val rng = new Random(87654321)
          for (input <- randInputs) {
            // Write a number to the queue in a random cycle
            enq.din.poke(input.U)
            enq.write.poke(false.B)
            do dut.clock.step() while (rng.nextInt(10) < 7)
            while (enq.full.peek.litValue == 1) dut.clock.step()
            enq.write.poke(true.B)
            dut.clock.step()
          }
        }
      
        def consumer() = {
          val rng = new Random(43215678)
          for (input <- randInputs) {
            // Read a number from the queue in a random cycle
            deq.read.poke(false.B)
            do dut.clock.step() while (!rng.nextBoolean())
            while (deq.empty.peek.litValue == 1) dut.clock.step()
            deq.dout.expect(input.U)
            deq.read.poke(true.B)
            dut.clock.step()
          }
        }
        
        // Fork the producer and keep the consumer in this thread
        fork {
          producer()
        }
        consumer()
      }
    }
  }
}
