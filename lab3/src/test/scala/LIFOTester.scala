
import java.math.BigInteger
import org.scalatest._
import chisel3._
import chisel3.fromBigIntToLiteral
import chisel3.util._
import chisel3.iotesters._
import scala.util.Random.nextInt
import scala.math.BigInt
import scala.math.Numeric.BigIntIsIntegral.abs

class LIFOTester (dut: LIFO) extends PeekPokeTester(dut){
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,4)
  step(1)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,5)
  step(1)
  poke(dut.io.deq.pop,1)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,6)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,1)
  poke(dut.io.enq.din,7)
  step(1)
  poke(dut.io.deq.pop,0)
  poke(dut.io.enq.push,0)
  poke(dut.io.deq.pop, 1)
  
  
  
  
  
  
  println("Pop 1 is: " + peek(dut.io.deq.dout))
  step(1)
  poke(dut.io.enq.push,0)
  poke(dut.io.deq.pop, 1)
  println("Pop 1 is: " + peek(dut.io.deq.dout))
  step(1)
  poke(dut.io.enq.push,0)
  poke(dut.io.deq.pop, 1)
  println("Pop 1 is: " + peek(dut.io.deq.dout))
  step(1)
  poke(dut.io.enq.push,0)
  poke(dut.io.deq.pop, 1)
  println("Pop 1 is: " + peek(dut.io.deq.dout))
}

object LIFOTester extends App {
  chisel3.iotesters.Driver(() => new LIFO(16,32)) { c =>
    new LIFOTester (c)
  }
}

//class LIFOSTester extends FlatSpec with Matchers {
//  "Tester" should "pass" in {
//    chisel3.iotesters.Driver(() => new LIFO(16, 32)) {
//      c => new LIFOTester(c)
//    } should be (true)
//  }
//}

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
