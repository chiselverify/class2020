import chisel3._
import chiseltest._
import org.scalatest._
import scala.collection.mutable.Queue 

class AndreasTester extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The BubbleFifo"

  it should "not be possible to make a fifo with a negative depth" in {
    a [Exception] should be thrownBy { new BubbleFifo(32, -1) } 
    a [Exception] should be thrownBy { new BubbleFifo(32, -7) } 
  }
  it should "not be possible to make a fifo with depth 0" in {
    a [Exception] should be thrownBy { new BubbleFifo(32, 0) } 
  }

  it should "not be possible to make a fifo with a negative size" in {
    a [Exception] should be thrownBy { new BubbleFifo(-1, 5) }
    a [Exception] should be thrownBy { new BubbleFifo(-7, 5) }
  }
  it should "not be possible to make a fifo with size 0" in {
    a [Exception] should be thrownBy {new BubbleFifo(0, 5) } 
  }

    it should "be empty on initialization" in {
    test(new BubbleFifo(32, 5)) {
      dut => {
        for (i <- 0 until 100) {
          dut.clock.step()
          dut.io.deq.empty.expect(true.B, "Value was added to the queue without setting write.")
        }
      }
    }
  }

  it should "only queue values when writing" in {
    test(new BubbleFifo(32, 5)) {
      dut => {
        dut.io.enq.din.poke(37.U)
        dut.io.enq.write.poke(false.B)
        dut.io.deq.read.poke(true.B)

        for (i <- 0 until 100) {
          dut.clock.step()
          dut.io.deq.empty.expect(true.B, "Value was added to the queue without setting write.")
        }
      }
    }
  }

  it should "only queue one value" in {
    test(new BubbleFifo(32, 5)) {
      dut => {
        //enqueue 37
        dut.io.enq.din.poke(37.U)
        dut.io.enq.write.poke(true.B)
        dut.io.deq.read.poke(true.B)
        dut.clock.step()
        dut.io.enq.write.poke(false.B)

        //count number of enqueued values
        var dequedItems = 0
        for (i <- 0 until 100) {
          dut.clock.step()
          if (dut.io.deq.empty.peek().litValue().intValue() == 0) {
            dequedItems += 1
            dut.io.deq.dout.expect(37.U, "Queue gave an incorrect value.")
          }
        }

        assert(dequedItems == 1, "Expected 1 value to be queued but " + dequedItems + " values were queued.")
      }
    }
  }

  it should "signal when it's full and empty" in {
    test(new BubbleFifo(32, 1)) {
      dut => {
        dut.io.enq.din.poke(37.U)
        dut.io.enq.write.poke(true.B)
        dut.io.enq.busy.expect(false.B)
        dut.io.deq.empty.expect(true.B)

        dut.clock.step()
        dut.io.enq.busy.expect(true.B)
        dut.io.deq.empty.expect(false.B)
        dut.clock.step()
        dut.io.enq.busy.expect(true.B)
        dut.io.deq.empty.expect(false.B)

        dut.io.deq.read.poke(true.B)
        dut.io.deq.dout.expect(37.U)
        dut.clock.step()
        dut.io.enq.busy.expect(false.B)
        dut.io.deq.empty.expect(true.B)

      }
    }
  }

  def randomEnqueue(queue: BubbleFifo, expectedOut: Queue[Int], count: Int) : Unit = {
    val rng = new scala.util.Random(23)

    var enqueuedCount = 0
    while(enqueuedCount != count) {
      val toEnqueue = rng.nextInt(1000000000)
      queue.io.enq.din.poke(toEnqueue.U)

      val write = rng.nextBoolean()
      queue.io.enq.write.poke(write.B)

      if(write &&
         queue.io.enq.busy.peek().litValue().intValue() == 0) {
          expectedOut.enqueue(toEnqueue)
          enqueuedCount += 1
      }

      queue.clock.step()
    }

    queue.io.enq.write.poke(false.B)
  }

  def randomDequeue(queue: BubbleFifo, expectedOut: Queue[Int], count: Int) : Unit = {
    val rng = new scala.util.Random(37)

    var dequeuedCount = 0
    while(dequeuedCount != count) {
      val read = rng.nextBoolean()
      queue.io.deq.read.poke(read.B)

      if(read && queue.io.deq.empty.peek().litValue().intValue() == 0) {
        assert(!expectedOut.isEmpty, "Queue contains item but none was put into the queue.")

        val expected = expectedOut.dequeue();
        val actual = queue.io.deq.dout.peek().litValue().intValue()

        assert(expected == actual, s"Expected ${expected} but got ${actual}.")
        dequeuedCount += 1
      }

      queue.clock.step()
    }

    assert(expectedOut.isEmpty, "Stopped dequeueing but there should still be items in the queue.")

    queue.io.deq.read.poke(true.B)
    for (i <- 0 until 100) {
      queue.clock.step()
      queue.io.deq.empty.expect(true.B, "Expected queue to be empty but it was not.")
    }
  }

  it should "correctly queue 10 elements with queue size of 1" in {
    test(new BubbleFifo(32, 1)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10) }
        val deq = fork { randomDequeue(dut, queue, 10) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "correctly queue 10 elements with queue size of 2" in {
    test(new BubbleFifo(32, 2)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10) }
        val deq = fork { randomDequeue(dut, queue, 10) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "correctly queue 10 elements with queue size of 7" in {
    test(new BubbleFifo(32, 7)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10) }
        val deq = fork { randomDequeue(dut, queue, 10) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "correctly queue 10 elements with queue size of 100" in {
    test(new BubbleFifo(32, 100)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10) }
        val deq = fork { randomDequeue(dut, queue, 10) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "fail because it expects 10 values but only dequeues 9 with queue size of 1" in {
    test(new BubbleFifo(32, 1)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10) }
        a [Exception] should be thrownBy { randomDequeue(dut, queue, 9) }

        enq.join()
      }
    }
  }

  it should "fail because it expects 10 values but only dequeues 9 with queue size of 2" in {
    test(new BubbleFifo(32, 2)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10) }
        a [Exception] should be thrownBy { randomDequeue(dut, queue, 9) }

        enq.join()
      }
    }
  }

  it should "fail because it expects 10 values but only dequeues 9 with queue size of 7" in {
    test(new BubbleFifo(32, 7)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10) }
        a [Exception] should be thrownBy { randomDequeue(dut, queue, 9) }

        enq.join()
      }
    }
  }

  it should "fail because it expects 10 values but only dequeues 9 with queue size of 100" in {
    test(new BubbleFifo(32, 100)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10) }
        a [Exception] should be thrownBy { randomDequeue(dut, queue, 9) }

        enq.join()
      }
    }
  }

  it should "correctly queue 1000 elements with queue size of 1" in {
    test(new BubbleFifo(32, 1)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 1000) }
        val deq = fork { randomDequeue(dut, queue, 1000) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "correctly queue 1000 elements with queue size of 2" in {
    test(new BubbleFifo(32, 2)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 1000) }
        val deq = fork { randomDequeue(dut, queue, 1000) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "correctly queue 1000 elements with queue size of 7" in {
    test(new BubbleFifo(32, 7)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 1000) }
        val deq = fork { randomDequeue(dut, queue, 1000) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "correctly queue 1000 elements with queue size of 100" in {
    test(new BubbleFifo(32, 100)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 1000) }
        val deq = fork { randomDequeue(dut, queue, 1000) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "correctly queue 10000 elements with queue size of 1" in {
    test(new BubbleFifo(32, 1)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10000) }
        val deq = fork { randomDequeue(dut, queue, 10000) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "correctly queue 10000 elements with queue size of 2" in {
    test(new BubbleFifo(32, 2)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10000) }
        val deq = fork { randomDequeue(dut, queue, 10000) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "correctly queue 10000 elements with queue size of 7" in {
    test(new BubbleFifo(32, 7)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10000) }
        val deq = fork { randomDequeue(dut, queue, 10000) }

        enq.join()
        deq.join()
      }
    }
  }

  it should "correctly queue 10000 elements with queue size of 100" in {
    test(new BubbleFifo(32, 100)) {
      dut => {
        val queue = Queue[Int]()

        val enq = fork { randomEnqueue(dut, queue, 10000) }
        val deq = fork { randomDequeue(dut, queue, 10000) }

        enq.join()
        deq.join()
      }
    }
  }
}
