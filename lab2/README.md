# Lab 2

## ScalaTest

Write a ScalaTest suit that tests some integer operations.
E.g., that 3 + 2 = 5, and some more.


## Test an ALU with an accumulator register

The interface is:

```scala
abstract class AluAccu(size: Int) extends Module {
  val io = IO(new Bundle {
    val op = Input(UInt(3.W))
    val din = Input(UInt(size.W))
    val ena = Input(Bool())
    val accu = Output(UInt(size.W))
  })
}
}
```

Write the test code without exploring the implementation too much.


