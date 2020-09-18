//Kishan's Tester

import java.math.BigInteger
import Types._
import org.scalatest._
import chisel3._
import chisel3.fromBigIntToLiteral
import chisel3.util._
import chisel3.iotesters._
import scala.util.Random.nextInt
import scala.math.BigInt
import scala.math.Numeric.BigIntIsIntegral.abs

object Test2{
  println("Bit Size: ")
  val size:Int = scala.io.StdIn.readLine.toInt
  println("Number of Tests: ")
  val n_tests:Int= scala.io.StdIn.readLine.toInt
  def maxval(): Int = {
    if (size>31){Int.MaxValue}
    else{(1 << size) - 1 }
  }
}


//enable and reset test

class ena_tester (dut: AluAccuChisel) extends PeekPokeTester(dut){
  //enable test
  var i=0
  poke(dut.io.ena,1)
  poke(dut.io.op,1)
  poke(dut.io.din,2)
  step(1)
  //enable=0
  poke(dut.io.ena,0)
  step(1)
  if(expect(dut.io.accu,2)){
    i+=1
  }
  //enable=1 test
  poke(dut.io.ena,1)
  step(1)
  if(expect(dut.io.accu,4)){
    i+=1
  }
  if(i==2) println("Enable test Passed")
  step(1)
  reset(1)
  if(expect(dut.io.accu,0)){
    i+=1
  }
  if(i==3) println("Reset test Passed")
}

object ena_tester extends App {
  chisel3.iotesters.Driver (() => new AluAccuChisel(Test2.size)) { c =>
    new ena_tester (c)
  }
}

//complete tester
class Tester (dut: AluAccuChisel) extends PeekPokeTester(dut){
  //Random variable generation for inputs
  var count = 0
  val r= scala.util.Random
  var din=scala.util.Random
  val l=List(Types.nop,Types.add,Types.sub,Types.and,Types.or,Types.xor,Types.ld,Types.shr)
  //Always enabled
  poke(dut.io.ena, 1)
  reset(1)
  reset(0)
  for (i <- 0 until Test2.n_tests){
    println("Test " + i.toString)
    //Maximum value for the respective type of bits
    val max_val:BigInt=(BigInt(1) << Test2.size) - BigInt(1)
    println("max val is is :" + max_val.toString)
//    if(Test2.size > 31) { val o = Int.MaxValue
//    println("Test2>31 path "+ o.toString)}
//    else{val o = (1 << Test2.size) - (1)
//    println("Test<31 path "+o.toString)}
//    println("max input(t) is:  "+o.toString)
    val o = Test2.maxval()
    //Random operation gen
    val op: Int = r.nextInt(7)
    //Random Input gen
    val i_din:BigInt = din.nextInt(o)
    println("Input is: " + i_din.toString)
    poke(dut.io.din,i_din)
    poke(dut.io.op,l(op))


    //Output of previous operation
    val output = peek(dut.io.accu)//fromBigIntToLiteral(peek(dut.io.accu)).asUInt(64.W)
    println("Accumulator value of previous operation is: " + peek(dut.io.accu).toString)
    println("dut.io.op is: " + op.toString)
    step(1)

    //Result of current operation
    println("Accumulator value of current operation is: " + peek(dut.io.accu).toString)
    val s:BigInt = op  match{
      case 0 => output
      case 1 => output + (i_din)
      case 2 => output - i_din //output.asUInt(64.W) - i.asUInt(64.W)
      case 3 => output & i_din
      case 4 => output | i_din
      case 5 => output ^ (i_din)
      case 6 => i_din
      case 7 => output >> 1
    }
    //println("Maximum value is: " + t.toString)
    //val t= (1.U(65.W) << 63) - 1.U(65.W)
    //val t=scala.math.pow(2,Test2.size).toLong
    if (s < 0) {
      println("s is <0 path: "+ s.toString)
      val r = max_val - abs(s) + 1
      println("Calculated value of output is: " + r.toString)
      if(expect(dut.io.accu,r)){
        println("Test " + i.toString +" Passed\n\n")
        count+=1
      }
    }
    else{
      if (s > max_val){
        println("s is >max path: "+ s.toString)
        val q = s - max_val -1
        println("Calculated value of output is: " + q.toString)
        if(expect(dut.io.accu,q)){
          count+=1
          println("Test " + i.toString +" Passed\n\n")
        }
      }
      else{
        println("s is none path: "+ s.toString)
        println("Calculated value of operation is: " + s.toString)
        if(expect(dut.io.accu,s)){
          count+=1
          println("Test " + i.toString +" Passed\n\n")
        }
      }
    }
  }
  if (Test2.n_tests == count) println("All Tests Passed!")
}

object Tester extends App {
  chisel3.iotesters.Driver (() => new AluAccuChisel(Test2.size)) { c =>
    new Tester (c)
  }
}


//Maximum value cases
class MaxCases(dut: AluAccuChisel) extends PeekPokeTester(dut){
  //test counter
  var i=0
  //clearing register
  poke(dut.io.ena,1)
  //poke(dut.io.din,0)
  //poke(dut.io.op,Types.ld)
  reset(1)
  reset(0)
  val l=List(Types.nop,Types.add,Types.sub)
  val maxvalue:BigInt = (BigInt(1) << Test2.size) - 1
  println("maximum val" + maxvalue.toString)
  poke(dut.io.din,maxvalue)
  poke(dut.io.op,Types.sub)
  step(1)
  if(expect(dut.io.accu,1)) i+=1
  reset(1)
  step(1)
  reset(0)
  poke(dut.io.din,1)
  poke(dut.io.op,Types.sub)
  step(1)
  step(1)
  if(expect(dut.io.accu,maxvalue)) i+=1
  step(1)
  poke(dut.io.op,Types.add)
  poke(dut.io.din,maxvalue)
  //println("output val is:" + peek(dut.io.accu).toString)
  if(expect(dut.io.accu,maxvalue - 1)) i+=1
  if(i==3) println("All Tests Passed!")
}
object MaxCases extends App {
  chisel3.iotesters.Driver (() => new AluAccuChisel(Test2.size)) { c =>
    new MaxCases (c)
  }
}

