# Lab 3: Design and Verification Examples

You will design and verify small examples in pairs of two.

## ALU with an Accumulator

 * Design engineer: Martin
 * Verification engineer: Hans, Kishan, Niels, Victor

### Specification

The design is an ALU with following operations: nop, +, -, &, |, ^, b, >>

The result of the ALU is stored in the accumulator (a register).
The accumulator has an enable input.

One input to the ALU is the accumulator, the other input is the component input.

The bit width of the design is configurable

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

### Test plan

 * Test that the accumulator is 0 after the reset
 * Test all ALU functions
 * Use edge cases as inputs, such as 0, -1, max. positive, min. negative
   * Compare use all combinations of edge cases with all operations
 * Use random inputs
 * Test that enable works
 * Test different bit width, at least 16 and 32 bits


## Design 1 - Iterative Multiplier(FSM + Datapath)

 * Design engineer: Kishan
 * Verification engineer: Niels

### Specification
  Configurable bit width multplier.
  The Product and the Multiplier are obtained as clocked outputs.
  The multiplier takes 'multiplier' number of clock cycles to produce the appropriate product.
  
  Interface:
  ```scala
  abstract class FSMDatapath(size: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(size.W))
    val b = Input(UInt(size.W))
    val op = Input(Bool()) // 0 for multiplication
    val output1 = Output(UInt((2*size).W))//product
    val output2 = Output(UInt((2*size).W))//b
    val done = Output(Bool())
  })
  ```
### Test plan

test for overflow
test the reset
function test with random number inputs

Update: use donesignal to step instead of i-equivalent input.

## Design 2 - Register file

 * Design engineer: Victor
 * Verification engineer: Hans

### Specification
  Configurable bit width.
  The register file has synchronous read/write.
  Register location zero is a constant zero.
  Read through write outputs the written data.
  
  Interface:
  ```scala
  abstract class RegFile(addrWidth: Int, dataWidth: Int) extends Module {
    val io = IO(new Bundle {
      val regWrite = Input(Bool())
      val registerRs1 = Input(UInt(addrWidth.W))
      val data1 = Output(UInt(dataWidth.W))
      val registerRs2 = Input(UInt(addrWidth.W))
      val data2 = Output(UInt(dataWidth.W))
      val registerRd = Input(UInt(addrWidth.W))
      val writeData = Input(UInt(dataWidth.W))
    })
  }
  ```

### Test plan
 * Test writing to location zero doesn't change the register file
 * Test reading and writing from all other register locations
 * Test reading from the port being currently written to

## Design 3

 * Design engineer: Niels
 * Verification engineer: Victor

### Specification
Popcounter with FSM, takes a bit string and counts how many 1's it contains.

### Test plan
 * Test functionality of the circuit
 * Test expected behaviour from boolean signals
 * Test corner cases and random inputs

## Design 4 - LIFO queue

 * Design engineer: Hans
 * Verification engineer: Kishan

### Specification
  Hardware implementation of a stack.
  Configurable bit width and queue size.
  Queue ignores pop when empty and push when full.

  Interface:
  ```scala
  class ProdIO(dataWidth: Int) extends Bundle {
    val push = Input(Bool())
    val din = Input(UInt(dataWidth.W))
    val full = Output(Bool())
  }

  class ConsIO(dataWidth: Int) extends Bundle {
    val pop = Input(Bool())
    val dout = Output(UInt(dataWidth.W))
    val empty = Output(Bool())
  }

  abstract class Queue(size: Int, dataWidth: Int) extends Module {
    val io = IO(new Bundle {
        val flush = Input(Bool())
        val enq = new ProdIO(dataWidth)
        val deq = new ConsIO(dataWidth)
    })
  }
  ```
### Test plan
* Test Empty and Full signals
* Test functionality of Flush Signal
* Test functionality of LIFO
* Test Writing and Reading at the same cycle

## Design 3 - Moving average

 * Design engineer: Andreas
 * Verification engineer: Andreas

### Specification
Moving average over a queue with a constant power of 2 size.
Possible to specify both queue length and bit sise of each item.
An item is an unsigned integer and the average value is computed as such.
It takes 1 cycle for the queue to update the average after a new item
was inserted.

```scala
  class MovingAveragePow2(averageLength: Int, bitCount: Int) extends Module {
    val io = IO(new Bundle{
      val din = Input(UInt(bitCount.W))
      val write = Input(Bool())
      val avg = Output(UInt(bitCount.W))
    })
  }
```

### Test plan
 * Test that the module can only be initialized with valid values
 * Test that the average is correct given a variety of inputs
 * Test that coner cases for the average value






