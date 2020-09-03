# Lab 2: Testing an ALU with an accumulator register

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

Write the test code without exploring the implementation.


