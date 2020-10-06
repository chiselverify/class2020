//Kishan Tester for LIFO Queue

import java.math.BigInteger
import org.scalatest._
import chisel3._
import chisel3.fromBigIntToLiteral
import chisel3.util._
import chisel3.iotesters._
import scala.util.Random.nextInt
import scala.math.BigInt
import scala.math.Numeric.BigIntIsIntegral.abs

//Test Paramter Object

object TestVec{
    println("Size of LIFO Queue: ")
    val size: Int = scala.io.StdIn.readLine.toInt
    println("Bit Width: ")
    val bwidth: Int = scala.io.StdIn.readLine.toInt
}

//Full and Empty Test

class FullEmptyTester(dut: LIFO) extends PeekPokeTester(dut){
  for ( i <- 0 to (TestVec.size - 1)){
    poke(dut.io.flush,0)
    poke(dut.io.enq.push,1)
    poke(dut.io.deq.pop,0)
    poke(dut.io.enq.din,i)
    print(i.toString + "\n")
    step(1)
    println("Output is: " + peek(dut.io.deq.dout))
  }
  expect(dut.io.enq.full,1)
  println("Full is: " + peek(dut.io.enq.full))
  for (i <- 0 to (TestVec.size - 1)){
    poke(dut.io.enq.push,0)
    poke(dut.io.deq.pop,1)
    println("Output is: " + peek(dut.io.deq.dout))
    expect(dut.io.deq.dout,TestVec.size - i - 1)
    step(1)
  }
  expect(dut.io.deq.empty,1)
}

//Full and Empty Tester

object FullEmptyTester extends App {
  chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) { c =>
    new FullEmptyTester (c)
  }
}

class FullEmptyTesters extends FlatSpec with Matchers {
  TestVec.size + "-bit " + TestVec.bwidth + " width Full and Empty Test "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new FullEmptyTester(c)
    } should be (true)
  }
}

//Push and Pop Stream

class LIFOTester (dut: LIFO) extends PeekPokeTester(dut){
  // Poke stream LIFOQueue() <-- 4,5,6,7
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,4)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,5)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,6)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,7)
  step(1)
  //End of Push stream-- contents of LIFO- (4,5,6,7<--ptr,.....)
  //Starting to poke values from the LIFO
  //Expect value 7-Poke operation

  poke(dut.io.enq.push,0)
  poke(dut.io.deq.pop, 1)
  expect(dut.io.deq.dout,7)
  println("Pop 1 is: " + peek(dut.io.deq.dout))
  step(1)

  //Contents of LIFO queue- (4,5,6<--ptr,...)
  //Expect value 6

  poke(dut.io.enq.push,0)
  poke(dut.io.deq.pop, 1)
  expect(dut.io.deq.dout,6)
  println("Pop 2 is: " + peek(dut.io.deq.dout))
  step(1)

  //Contents of LIFO queue - (4,5<--ptr,...)

  poke(dut.io.enq.push,0)
  poke(dut.io.deq.pop, 1)
  expect(dut.io.deq.dout,5)
  println("Pop 3 is: " + peek(dut.io.deq.dout))
  step(1)

  //Contens of LIFO queue-- (4<--ptr,....)

  poke(dut.io.enq.push,0)
  poke(dut.io.deq.pop, 1)
  expect(dut.io.deq.dout,4)
  println("Pop 4 is: " + peek(dut.io.deq.dout))
  step(1)

  //Contens of LIFO queue-- (<--ptr,....)

  poke(dut.io.enq.push,0)
  poke(dut.io.deq.pop, 1)
  println("Empty Pop 5 is: " + peek(dut.io.deq.dout))
  expect(dut.io.deq.dout,0)
  println("Empty is: " + peek(dut.io.deq.empty))
  expect(dut.io.deq.empty,1)
  step(1)
  println("Emtpy Pop 6 is: " + peek(dut.io.deq.dout))
  expect(dut.io.deq.dout,0)
}

//Functionality Tester
object LIFOTester extends App {
  chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) { c =>
    new LIFOTester (c)
  }
}

class LIFOSTester extends FlatSpec with Matchers {
  TestVec.size + "-bit " + TestVec.bwidth + " width LIFO Queue test "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new LIFOTester(c)
    } should be (true)
  }
}


//Flush and reset test

class FlushResetTester(dut : LIFO) extends PeekPokeTester(dut){
  // Poke stream LIFOQueue() <-- 4,5,6,7
  println("---Flush Test--- \n")
  poke(dut.io.flush,0)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,4)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,5)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,6)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,7)
  step(1)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,0)
  println("Flush output value: " + peek(dut.io.deq.dout))
  expect(dut.io.deq.dout,7)
  step(1)
  //Contents of LIFO Queue (4,5,6,7<--ptr...)

  poke(dut.io.flush,1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,0)
  step(1)
  poke(dut.io.flush,0)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,0)
  println("Flush output value: " + peek(dut.io.deq.dout))
  println("Flush empty, empty signal is: " + peek(dut.io.deq.empty))
  expect(dut.io.deq.dout,0)
  expect(dut.io.deq.empty,1)
  step(1)
  expect(dut.io.deq.dout,0)
  expect(dut.io.deq.empty,1)
  println("Flush empty, empty signal is: " + peek(dut.io.deq.dout))
}



//Flush and Reset Test

object FlushResetTester extends App {
  chisel3.iotesters.Driver(() => new LIFO(4,16)) { c =>
    new FlushResetTester (c)
  }
}

class FlushResetTesters extends FlatSpec with Matchers {
  TestVec.size + "-bit " + TestVec.bwidth + " width Flush and Reset Test "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new FlushResetTester(c)
    } should be (true)
  }
}
//Throughput Test
class ThroughputTest(dut : LIFO) extends PeekPokeTester(dut){
  // Poke stream LIFOQueue() <-- 4,5,6,7
  poke(dut.io.flush,0)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,4)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,5)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,6)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,7)
  step(1)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,0)
  println("Flush output value: " + peek(dut.io.deq.dout))
  expect(dut.io.deq.dout,7)
  step(1)
  //Contents of LIFO Queue (4,5,6,7<--ptr...)
  //Beginning of Flush

  poke(dut.io.flush,0)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,10)
  expect(dut.io.deq.dout,6)
  step(1)
  poke(dut.io.flush,0)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,9)
  expect(dut.io.deq.dout,10)
  step(1)
  poke(dut.io.flush,0)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,8)
  expect(dut.io.deq.dout,9)
  step(1)
  poke(dut.io.flush,0)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,7)
  expect(dut.io.deq.dout,8)
}



//Flush and Reset Test

object ThroughputTest extends App {
  chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) { c =>
    new ThroughputTest (c)
  }
}

class ThroughputTests extends FlatSpec with Matchers {
  TestVec.size + "-bit " + TestVec.bwidth + " width Throughput Test "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new ThroughputTest(c)
    } should be (true)
  }
}
//All Tests: (1) Full and Empty Test (2)  Functionality Tester (3) Flush and Reset Test

class AllTest extends FlatSpec with Matchers {
  TestVec.size + "-bit " + TestVec.bwidth + " Full and Empty Test "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new FullEmptyTester(c)
    } should be (true)
  }
  TestVec.size + "-bit " + TestVec.bwidth + " LIFOSTester(Functionality Test) "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new LIFOTester(c)
    } should be (true)
  }
  TestVec.size + "-bit " + TestVec.bwidth + " Flush and Reset Test "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new FlushResetTester(c)
    } should be (true)
  }
  TestVec.size + "-bit " + TestVec.bwidth + " width Throughput Test "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new ThroughputTest(c)
    } should be (true)
  }
}
//
//class ProdIO(dataWidth: Int) extends Bundle {
//  val push = Input(Bool())
//  val din = Input(UInt(dataWidth.W))
//  val full = Output(Bool())
//}
//
//class ConsIO(dataWidth: Int) extends Bundle {
//  val pop = Input(Bool())
//  val dout = Output(UInt(dataWidth.W))
//  val empty = Output(Bool())
//}
//
//abstract class Queue(size: Int, dataWidth: Int) extends Module {
//  val io = IO(new Bundle {
//    val enq = new ProdIO(dataWidth)
//    val deq = new ConsIO(dataWidth)
//  })
//}
