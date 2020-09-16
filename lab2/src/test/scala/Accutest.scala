import chisel3._
import chisel3.iotesters._
import Types._
import org.scalatest._

class AccuTest(dut: AluAccuChisel)extends PeekPokeTester(dut) {

  poke(dut.io.ena, 1)

  /*
      poke(dut.io.ena, 1)
      poke(dut.io.din, w)
      poke(dut.io.op, nop)
    step(1)
      expect(dut.io.accu, 0)
      poke(dut.io.din, w)
      poke(dut.io.op, add)
      step(1)
      poke(dut.io.din, 6)
      poke(dut.io.op, add)
    }
    step(1)
      expect(dut.io.accu, 10) */



  var expectval = 0

   /* poke(dut.io.ena, 1)
  for(w <- 1 until 10){
    poke(dut.io.din, w)
    poke(dut.io.op, add)
    expectval = expectval + w
    step(1)
    expect(dut.io.accu, expectval)
  } */


  for(w <- 0 until 10) {
    val r = new scala.util.Random
    val random = r.nextInt(1000000)

    poke(dut.io.din, random)
    poke(dut.io.op, add)
    expectval = expectval + random
    println(random.toString)
    step(1)
    expect(dut.io.accu, expectval)
  }




}

object AccuTest extends App {
  chisel3.iotesters.Driver (() => new AluAccuChisel(32)) { c => new AccuTest(c)

  }
}

class AccuScalaTest extends FlatSpec with Matchers {
  "Tester" should "pass" in {
    chisel3.iotesters.Driver(() => new AluAccuChisel(32)) {
      c => new AccuTest(c)
    } should be (true)
  }
}