import chisel3._
import chisel3.iotesters._
import chisel3.util._
import scala.util._

//The code below is using iotesters
class AluAccuTest(dut: AluAccuChisel) extends PeekPokeTester(dut) {
  var i = 0
  var j = 0
  var temp = 0.U
  val test = 1024
  //Generating random numbers
  var r = new Random(2^32)

  //Corner cases
  val corner = Array(2^16, -(2^16), 0, -1, 256)

  //Resets accumulator to zero
  def reset() {
    poke(dut.io.op, 7)
    poke(dut.io.din, 0)
    step(1)
    expect(dut.io.accu, 0)
  }

  poke(dut.io.ena, 1)
  reset()

  //Testing corner cases
  for (j <- 1 to 6){
    for (i <- 0 until corner.length){
      poke(dut.io.op, j)
      poke(dut.io.din, corner(i))
      step(1)
      poke(dut.io.din, r)
      step(1)
      switch(j) {
        is(1) {
          expect(dut.io.accu, corner(i) + r)
        }
        is(2) {
          when(dut.io.accu - dut.io.din < 0){
            expect((2^32) - (dut.io.din - dut.io.accu))
          } .otherwise {
            expect(dut.io.accu, corner(i) - r)
          }
        }
        is(3) {
          expect(dut.io.accu, corner(i) & r)
        }
        is(4) {
          expect(dut.io.accu, corner(i) | r)
        }
        is(5) {
          expect(dut.io.accu, corner(i) ^ r)
        }
        is(6) {
          expect(dut.io.accu, corner(i) >> 1)
        }
      }
    }
    poke(dut.io.op, 7)
    poke(dut.io.din, 0)
    step(1)
  }

  //Testing add function
  for (i <- 0 to test) {
    poke(dut.io.op, 1)
    poke(dut.io.din, i)
    step(1)
    expect(dut.io.accu, i*(i+1)/2)
  }
  //Testing subtract function
  poke(dut.io.op, 7)
  poke(dut.io.din, test*(test+1)/2)
  for (i <- 0 to test) {
    poke(dut.io.op, 2)
    poke(dut.io.din, i)
    println(peek(dut.io.din).toString)
    step(1)
    expect(dut.io.accu, (test*(test+1)/2)-i*(i+1)/2)
  }
  //Testing AND function
  for (i <- 0 to test) {
    temp = dut.io.accu
    poke(dut.io.op, 3)
    poke(dut.io.din, r)
    step(1)
    expect(dut.io.accu, r & temp)
  }

  //Testing OR function
  for (i <- 0 to test) {
    temp = dut.io.accu
    poke(dut.io.op, 3)
    poke(dut.io.din, r)
    step(1)
    expect(dut.io.accu, r | temp)
  }

  //Testing XOR function
  for (i <- 0 to test) {
    temp = dut.io.accu
    poke(dut.io.op, 3)
    poke(dut.io.din, r)
    step(1)
    expect(dut.io.accu, r ^ temp)
  }
}

object AluAccuTest extends App {
  chisel3.iotesters.Driver(() => new AluAccuChisel(32))
  { c => new AluAccuTest(c)}
}