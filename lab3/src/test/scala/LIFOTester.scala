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
    println("Number of stages in LIFO Queue:(above 1 please) ")
    val size: Int = scala.io.StdIn.readLine.toInt
    println("Bit Width of Queue: ")
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
  TestVec.size + " - stage " + TestVec.bwidth + " bit width Full and Empty Test "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new FullEmptyTester(c)
    } should be (true)
  }
}

//Push and Pop Stream

class LIFOTester (dut: LIFO) extends PeekPokeTester(dut){
  // Poke stream LIFOQueue() <-- 0,1,2...
  for(i <- 0 until TestVec.size - 1) {
    poke(dut.io.deq.pop, 0)
    poke(dut.io.enq.push, 1)
    poke(dut.io.enq.din, i)
    step(1)
  }
  //--Full Queue--
  for(i <- TestVec.size - 1 until 0 ) {
      poke(dut.io.enq.push, 0)
      poke(dut.io.deq.pop, 1)
      //println("Pop 1 full is: " + peek(dut.io.enq.full))
      expect(dut.io.deq.dout, i)
      step(1)
    }
}

//Functionality Tester
object LIFOTester extends App {
  chisel3.iotesters.Driver(() => new LIFO(4,TestVec.bwidth)) { c =>
    new LIFOTester (c)
  }
}

class LIFOSTester extends FlatSpec with Matchers {
  TestVec.size + " - stage " + TestVec.bwidth + " bit width functionality Tester "  should "pass" in {
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
  expect(dut.io.deq.dout,0)
  expect(dut.io.deq.empty,1)
  step(1)
  expect(dut.io.deq.dout,0)
  expect(dut.io.deq.empty,1)
  step(1)
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
  reset(1)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,0)
  expect(dut.io.deq.dout,0)
  expect(dut.io.deq.empty,1)
  step(1)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,0)
  expect(dut.io.deq.dout,0)
  expect(dut.io.deq.empty,1)
}



//Flush and Reset Test

object FlushResetTester extends App {
  chisel3.iotesters.Driver(() => new LIFO(4,16)) { c =>
    new FlushResetTester (c)
  }
}

class FlushResetTesters extends FlatSpec with Matchers {
  " 4 - stage " + TestVec.bwidth + " bit width Flush and Reset Test "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(4,TestVec.bwidth)) {
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

object ThroughputTests extends App {
  chisel3.iotesters.Driver(() => new LIFO(4,TestVec.bwidth)) { c =>
    new ThroughputTest (c)
  }
}

class ThroughputTests extends FlatSpec with Matchers {
  TestVec.size + " - stage " + TestVec.bwidth + " bit width Full and Empty Test "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(4,32)) {
      c => new ThroughputTest(c)
    } should be (true)
  }
}
//All Tests: (1) Full and Empty Test (2)  Functionality Tester (3) Flush and Reset Test

class AllTests extends FlatSpec with Matchers {
  TestVec.size + " - stage " + TestVec.bwidth + " bit width Full and Empty Test1 "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new FullEmptyTester(c)
    } should be (true)
  }
  TestVec.size + " - stage " + TestVec.bwidth + " bit width Functionality Test1 "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new LIFOTester(c)
    } should be (true)
  }
  TestVec.size + " - stage " + TestVec.bwidth + " bit width Flush Reset Test1 "  should "pass" in {
    chisel3.iotesters.Driver(() => new LIFO(TestVec.size,TestVec.bwidth)) {
      c => new FlushResetTester(c)
    } should be (true)
  }
  TestVec.size + " - stage " + TestVec.bwidth + " bit width Throughput Test 1"  should "pass" in {
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
