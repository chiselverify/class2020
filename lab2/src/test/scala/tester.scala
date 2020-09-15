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
object Test{
  val size=32
}
class tester (dut: AluAccuChisel) extends PeekPokeTester(dut){
  reset = 0
  val r= scala.util.Random
  val l=List(Types.nop,Types.add,Types.sub,Types.and,Types.or,Types.xor,Types.ld,Types.shr)
  poke(dut.io.ena, 1)
  for (i <- 0 until 1024){
      val j: Int = r.nextInt(7)
      poke(dut.io.din,i)
      poke(dut.io.op,l(j))
      val output = peek(dut.io.accu)//fromBigIntToLiteral(peek(dut.io.accu)).asUInt(64.W)
      println("accu0 before step is " + peek(dut.io.accu).toString)
      println("dut.io.op is: " + j.toString)
      //val k = i
      step(1)
      println("accu1 is dut.io.o/p after step is: " + peek(dut.io.accu).toString)
      println("o/p dut.io.o/p is: " + output.toString)
      println("i is: " + i.toString)
      val s:BigInt = j  match{
        case 0 => output
        case 1 => output + (i)
        case 2 => output - i //output.asUInt(64.W) - i.asUInt(64.W)
        case 3 => output & i
        case 4 => output | i
        case 5 => output ^ (i)
        case 6 => i
        case 7 => output >> 1
      }
      println("s string is"+ s.toString)
      //val t= (1.U(65.W) << 63) - 1.U(65.W)
      val t=scala.math.pow(2,Test.size).toLong
      println("t is: " + t.toString)
      //println(x.toString)
//      val p=  0 - abs(s).U(65.W).toFloat//.U(64.W) + 1.U(64.W)
//      //val m = BigInt.apply(0.U(65.W) - p)
//      println("s string is"+ s.toString)
//      println("s bytearray is"+ p.toString)
//      expect(dut.io.accu,s)
      if (s < 0) {
        println("s is: " + s.toString)
        val r = t - abs(s)
        //val e:UInt = 0.U - r
        println("r is: " + r.toString)
        expect(dut.io.accu,r)
      }
      else{
        if (s >= t){
          val q = s - t
          println("q is: " + q.toString)
          expect(dut.io.accu,q)
        }
        else{
          println("s is: " + s.toString)
          val q = peek(dut.io.accu)
          println("q is: " + q.toString+"\n")
          expect(dut.io.accu,s)
          //println("dut.io.o/p is: " + peek(dut.io.accu).toString)

        }
      }
  }
}

object tester extends App {
  chisel3.iotesters.Driver (() => new AluAccuChisel(Test.size)) { c =>
    new tester (c)
  }
}
//class WaveformCounterSpec extends FlatSpec with Matchers {
//  "Tester" should "pass" in {
//    chisel3.iotesters.Driver.execute(Array("--generate-vcd-output","on"),() => new AluAccuChisel(64)){ c =>
//      new tester(c)
//    }
//  }
//}




