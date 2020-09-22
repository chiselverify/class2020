# Lab 4

## ChiselTest

Write several ChiselTest tests for the BubbleFifo.
Have ate least on concurrent test (maybe testing the throughput, or startup time).


The interface is:

```scala
class WriterIO(size: Int) extends Bundle {
  val write = Input(Bool())
  val full = Output(Bool())
  val din = Input(UInt(size.W))
}

class ReaderIO(size: Int) extends Bundle {
  val read = Input(Bool())
  val empty = Output(Bool())
  val dout = Output(UInt(size.W))
}

class BubbleFifo(size: Int, depth: Int) extends Module {
  val io = IO(new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  })
}
```

Write the test code without exploring the implementation too much.


