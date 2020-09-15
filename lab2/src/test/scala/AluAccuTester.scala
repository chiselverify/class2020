import chisel3._
import chisel3.iotesters._

import scala.util.Random

import org.scalatest._

// Select the operand size of the AluAccu under test and the number
// of randomly generated inputs to test for
object Test {
    val size = 128
    val numtests = 1024
}

class AluAccuTester(dut: AluAccuChisel) extends PeekPokeTester(dut) {
    // This function runs a simple reset sequence to ensure the accumulator
    // is zeroed out before the tests
    def reset() = { // ld 0
        poke(dut.io.op, 6)
        poke(dut.io.din, 0)
        poke(dut.io.ena, true)
        step(1)
        expect(dut.io.accu, 0)
    }

    // This function runs through the generated inputs and operations and
    // ensures their outputs are as expected
    def test(inputs: Array[BigInt], ops: Array[Int], results: Array[BigInt]) = {
        assert(inputs.length == ops.length)
        assert(ops.length == results.length, "test: all arrays must be the same length!")
        for (i <- 0 until results.length) {
            poke(dut.io.op, ops(i))
            poke(dut.io.din, inputs(i))
            step(1)
            expect(dut.io.accu, results(i).asUInt(Test.size.W), "Op " + ops(i) + " failed")
        }
    }

    // Depending on the operation to be performed, given as input, returns a
    // function - (BigInt, BigInt) => BigInt - corresponding to that of the AluAccu
    def func(op: Int) = {
        op match {
            case 0 => (a: BigInt, _: BigInt) => a
            case 1 => (a: BigInt, b: BigInt) => a + b
            case 2 => (a: BigInt, b: BigInt) => a - b
            case 3 => (a: BigInt, b: BigInt) => a & b
            case 4 => (a: BigInt, b: BigInt) => a | b
            case 5 => (a: BigInt, b: BigInt) => a ^ b
            case 6 => (_: BigInt, b: BigInt) => b
            case 7 => (a: BigInt, _: BigInt) => a >> 1
            case _ => (_: BigInt, _: BigInt) => BigInt(0)
        }
    }

    // Generate a bunch of random numbers
    val rng = new Random(12345678)
    val mask = (BigInt(1) << Test.size) - 1
    val inputs = Array.fill(Test.numtests)(BigInt.apply(Test.size, rng)).map(_ & mask)
    val ops = Array.fill(Test.numtests)(rng.nextInt()).map(_ & 0x7)
    val results = inputs.zip(ops).foldLeft(Array[BigInt](0)) {
        (acc, tup) => 
        tup match {
            case (i, op) => acc :+ (func(op)(acc.last, i) & mask)
        }
    }.drop(1)

    // Reset the accumulator
    reset()

    // Run through all the operations
    test(inputs, ops, results)
}

object AluAccuTester extends App {
    chisel3.iotesters.Driver(() => new AluAccuChisel(Test.size)) {
        c => new AluAccuTester(c)
    }
}

class AluAccuSTester extends FlatSpec with Matchers {
    Test.size + "-bit tester" should "pass" in {
        chisel3.iotesters.Driver(() => new AluAccuChisel(Test.size)) {
            c => new AluAccuTester(c)
        } should be (true)
    } 
}