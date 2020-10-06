import chisel3._
import chiseltest._
import org.scalatest._

/**
  * Author: Victor Alexander Hansen (s194027@student.dtu.dk)
  *
  * Testplan:
  * Moves data from input to output
  * Reset should result in an empty register
  * Signals when full
  * Concurrent write and read of data
  * Corner cases
  * Ignoring new input data when full
  */

class VictorTester extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The BubbleFifo"

  it should "move data from input to output" in {
    test(new BubbleFifo(32, 4)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq

        enq.write.poke(true.B)
        deq.read.poke(false.B)
        enq.din.poke(123.U)
        dut.clock.step(1)
        enq.write.poke(false.B)
        
        while (deq.empty.peek.litValue == 1) {
          dut.clock.step(1)
        }
        deq.dout.expect(123.U)
      }
    }
  }

  it should "be empty after reset" in {
    test(new BubbleFifo(32, 4)) {
      dut => {
        dut.reset.poke(true.B)
        dut.clock.step(1)
        dut.reset.poke(false.B)
        dut.clock.step(1)
        dut.io.deq.empty.expect(true.B)
      }
    }
  }

  it should "signal when full" in {
    test(new BubbleFifo(32, 4)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq

        enq.write.poke(true.B)
        deq.read.poke(false.B)
        while (enq.full.peek.litValue == 0){
          dut.io.enq.din.poke(1.U)
          dut.clock.step(1)
        }
        dut.io.enq.full.expect(true.B)
      }
    }
  }

  it should "writes and reads data concurrently" in {
    test(new BubbleFifo(32, 4)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq
        val order = Vector(1.U, 2.U, 3.U, 4.U)
        
        def writer() {
          for (i <- 0 until 4) {
            enq.write.poke(true.B)
            enq.din.poke(order(i))
            dut.clock.step(1)
            enq.write.poke(false.B)
            while (enq.full.peek.litValue == 1){
              dut.clock.step(1)
            }
          }
        }

        def reader() {
          for (i <- 0 until 4) {
            deq.read.poke(false.B)
            while(deq.empty.peek.litValue == 1){
              dut.clock.step(1)
            }
            deq.dout.expect(order(i))
            deq.read.poke(true.B)
            dut.clock.step(1)
          }
        }

        fork {
          writer()
        }
        reader()

      }
    }
  }

  it should "handle corner cases" in {
    // Relevant corner cases
    val corner = List(429467295.U, 0.U)

    test(new BubbleFifo(32, 4)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq

        for (i <- 0 until 2){
          enq.write.poke(true.B)
          deq.read.poke(false.B)
          enq.din.poke(corner(i))
          dut.clock.step(1)
          enq.write.poke(false.B)
          while (deq.empty.peek.litValue == 1) {
            dut.clock.step(1)
          }
          deq.dout.expect(corner(i))

          deq.read.poke(true.B)
          dut.clock.step(1)
          while (enq.full.peek.litValue == 1) {
            dut.clock.step(1)
          }
          deq.dout.expect(0.U)
        }
      }
    }
  }

  it should "ignore new input data when full" in {
    test(new BubbleFifo(32, 4)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq

        enq.write.poke(true.B)
        deq.read.poke(false.B)
        enq.din.poke(4.U)
        dut.clock.step(1)
        enq.din.poke(8.U)
        while (deq.empty.peek.litValue == 1) {
          dut.clock.step(1)
        }
        deq.dout.expect(4.U)
      }
    }
  }
}
