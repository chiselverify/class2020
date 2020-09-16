import chisel3._
import chisel3.iotesters._
import chisel3.util._
import scala.util._
import org.scalatest._

//The code below is using iotesters
class AluAccuTest(dut: AluAccuChisel) extends PeekPokeTester(dut) {
  val i = 0
  val j = 0
  var temp = BigInt(0)
  val size = 16
  val MaxUInt = (1 << 16) - 1
  val test = 256
  //Generating random numbers
  var rgen = new Random(1472)
  var r = rgen.nextInt(100)

  //Corner cases
  val corner = Array(MaxUInt, 0)

  //Resets accumulator to zero
  def reset() {
    poke(dut.io.op, 6)
    poke(dut.io.din, 0)
    step(1)
    expect(dut.io.accu, 0)
  }

  //Tests the chosen corner values
  def cornerTest() = {
    reset()
    step(1)
    poke(dut.io.ena, true)
    for (i <- 0 to 1) {
      for (j <- 0 to 7) {
        j match {
          case 0 => {
            poke(dut.io.op, 0)
            expect(dut.io.accu, peek(dut.io.accu))
          }
          case 1 => {
            poke(dut.io.op, 6)
            poke(dut.io.din, corner(i))
            step(1)
            poke(dut.io.op, 1)
            poke(dut.io.din, r)
            step(1)
            if ((corner(i) + r) > MaxUInt){
              expect(dut.io.accu, (corner(i) + r) - (1 << 16))
            } else {
              expect(dut.io.accu, corner(i) + r)
            }
          }
          case 2 => {
            poke(dut.io.op, 6)
            poke(dut.io.din, corner(i))
            step(1)
            poke(dut.io.op, 2)
            poke(dut.io.din, r)
            step(1)
            if ((corner(i) - r) < 0) {
              expect(dut.io.accu, (1 << 16) - (corner(i) + r))
            } else {
              expect(dut.io.accu, corner(i) - r)
            }
          }
          case 3 => {
            poke(dut.io.op, 6)
            poke(dut.io.din, corner(i))
            step(1)
            poke(dut.io.op, 3)
            poke(dut.io.din, r)
            step(1)
            expect(dut.io.accu, corner(i) & r)
          }
          case 4 => {
            poke(dut.io.op, 6)
            poke(dut.io.din, corner(i))
            step(1)
            poke(dut.io.op, 4)
            poke(dut.io.din, r)
            step(1)
            expect(dut.io.accu, corner(i) | r)
          }
          case 5 => {
            poke(dut.io.op, 6)
            poke(dut.io.din, corner(i))
            step(1)
            poke(dut.io.op, 5)
            poke(dut.io.din, r)
            step(1)
            expect(dut.io.accu, corner(i) ^ r)
          }
          case 6 => {
            poke(dut.io.op, 6)
            poke(dut.io.din, corner(i))
            step(1)
            expect(dut.io.accu, corner(i))
          }
          case 7 => {
            poke(dut.io.op, 6)
            poke(dut.io.din, corner(i))
            step(1)
            poke(dut.io.op, 7)
            step(1)
            expect(dut.io.accu, corner(i) >> 1)
          }
          case _ => {
            expect(dut.io.accu, 0)
          }
        }
      }
    }
  }

  poke(dut.io.ena, 1)
  reset()

  //Testing add function
  for (i <- 0 to test) {
    poke(dut.io.op, 1)
    poke(dut.io.din, i)
    step(1)
    expect(dut.io.accu, i*(i+1)/2)
  }

  //Testing subtract function
  poke(dut.io.op, 6)
  poke(dut.io.din, test*(test+1)/2)
  for (i <- 0.U to test) {
    poke(dut.io.op, 2)
    poke(dut.io.din, i)
    println(peek(dut.io.din).toString)
    step(1)
    expect(dut.io.accu, (test*(test+1)/2)-i*(i+1)/2)
  }
  //Testing AND function
  poke(dut.io.op, 6)
  poke(dut.io.din, "b0101010101".U)
  for (i <- 0 to test) {
    temp = peek(dut.io.accu)
    poke(dut.io.op, 3)
    poke(dut.io.din, i)
    step(1)
    expect(dut.io.accu, i & temp)
  }

  //Testing OR function
  poke(dut.io.op, 6)
  poke(dut.io.din, "b0101010101".U)
  for (i <- 0 to test) {
    temp = peek(dut.io.accu)
    poke(dut.io.op, 4)
    poke(dut.io.din, i)
    step(1)
    expect(dut.io.accu, i | temp)
  }

  //Testing XOR function
  poke(dut.io.op, 6)
  poke(dut.io.din, "b0101010101".U)
  for (i <- 0 to test) {
    temp = peek(dut.io.accu)
    poke(dut.io.op, 5)
    poke(dut.io.din, i)
    step(1)
    expect(dut.io.accu, i ^ temp)
  }

  //Testing SHIFT function
  for (i <- 0 to test) {
    poke(dut.io.op, 6)
    poke(dut.io.din, i)
    step(1)
    poke(dut.io.op, 7)
    step(1)
    expect(dut.io.accu, i >> 1)
  }

  cornerTest()
}

object AluAccuTest extends App {
  chisel3.iotesters.Driver(() => new AluAccuChisel(16))
  { c => new AluAccuTest(c)}
}

class AluAccuScalaTest extends FlatSpec with Matchers {
  "ALU tester" should "pass" in {
    chisel3.iotesters.Driver(() => new AluAccuChisel(16)) {
      c => new AluAccuTest(c)
    } should be(true)
  }
}