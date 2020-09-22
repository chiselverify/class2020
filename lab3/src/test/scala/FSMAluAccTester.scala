import chisel3._
import chisel3.iotesters._
import org.scalatest._
import fsmd.FSMDatapath

class FSMAluAccTester(dut: fsmd.FSMDatapath)extends PeekPokeTester(dut) {
val bitsize = 16


  //resetsequence
  poke(dut.io.reset, 1)
  step(1)
  expect(dut.io.output1, 0)
  poke(dut.io.reset, 0)

  //overflow test
  if(bitsize==32) {
    poke(dut.io.op, 0) //multiplication op
    poke(dut.io.b, 2) //a*2
    poke(dut.io.a, 1073741824) //(0111 1111 1111 1111 1111 1111 1111 1111)/2
    step(2)
    expect(dut.io.output1, -2147483648) //b"1000 0000 0000 0000 0000 0000 0000 0000"
    step(1)
  }else{
    poke(dut.io.op, 0) //multiplication op
    poke(dut.io.b, 2) //a*2
    poke(dut.io.a, 16384) //(0111 1111 1111 1111)/2
    step(2)
    expect(dut.io.output1, -32768) //b"1000 0000 0000 0000 0000 0000 0000 0000"
    step(1)
  }


  //plz work
  poke(dut.io.a, 4)
  poke(dut.io.b, 5)
  step(5)
  expect(dut.io.output1, 20)


  //test reset
  poke(dut.io.reset, 1)
  step(1)
  expect(dut.io.output1, 0)
  poke(dut.io.reset, 0)

  //Random number function test
  if(bitsize==32){
  for(w <- 0 until 10) {
    val r = new scala.util.Random
    val random1 = r.nextInt(46340) //sqrt(0x7FFF FFFF) to make sure we don't exceed the range of signed 32 bits
    val random2 = r.nextInt(46339)
    val expectedval = random1 * random2
    poke(dut.io.a, random1)
    poke(dut.io.b, random2)
    step(random2)
    expect(dut.io.output1, expectedval)
    step(1)
  }
  }else{
      for(w <- 0 until 10) {
        val r = new scala.util.Random
        val random1 = r.nextInt(181) //sqrt(0x7FFF) to make sure we don't exceed the range of signed 16 bits
        val random2 = r.nextInt(181)
        val expectedval = random1 * random2
        poke(dut.io.a, random1)
        poke(dut.io.b, random2)
        step(random2)
        expect(dut.io.output1, expectedval)
        step(1)
    }
  }

}

object FSMAluAccTester extends App {
  chisel3.iotesters.Driver (() => new fsmd.FSMDatapath(32)) { c =>
    new FSMAluAccTester (c)
  }
}

class FSMAluAccTesterScala extends FlatSpec with Matchers {
  "Tester" should "pass" in {
    chisel3.iotesters.Driver(() => new fsmd.FSMDatapath(32)) {
      c => new FSMAluAccTester(c)
    } should be (true)
  }
}