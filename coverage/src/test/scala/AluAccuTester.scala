import chisel3._
import chisel3.iotesters._

import scala.util.Random

import org.scalatest._

// Select the operand size of the AluAccu under test and the number
// of randomly generated inputs to test for
object Test {
    val sizes = List(16, 32, 64, 128)
    val numtests = 1024
}

class AluAccuTester(dut: AluAccuChisel, size: Int) extends PeekPokeTester(dut) {
    // This function runs a simple reset sequence to ensure the accumulator
    // is zeroed out before the tests
    def reset() = { // ld 0
        poke(dut.io.op, 6)
        poke(dut.io.din, 0)
        poke(dut.io.ena, true)
        step(1)
        expect(dut.io.accu, 0)
    }

    // Test the enable functionality of the AluAccu
    def enableTest() = {
        poke(dut.io.op, 6)
        poke(dut.io.din, 1)
        poke(dut.io.ena, true)
        step(1)
        poke(dut.io.op, 1)
        poke(dut.io.din, 2)
        poke(dut.io.ena, false)
        step(1)
        expect(dut.io.accu, 1)
    }

    // Test some corner cases of the accumulator input
    def cornerTest() = {
        val maxUint = (BigInt(1) << size) - 1
        // Relevant edge cases are 0 and max for UInt types
        // Underflow
        poke(dut.io.op, 6)
        poke(dut.io.din, 0)
        poke(dut.io.ena, true)
        step(1)
        poke(dut.io.op, 2)
        poke(dut.io.din, 1)
        step(1)
        expect(dut.io.accu, func(2)(BigInt(0), BigInt(1)) & maxUint)

        // Overflow
        poke(dut.io.op, 1)
        step(1)
        expect(dut.io.accu, 0)
    }

    // This function runs through the generated inputs and operations and
    // ensures their outputs are as expected
    def test(inputs: Array[BigInt], ops: Array[Int], results: Array[BigInt]) = {
        assert(inputs.length == ops.length)
        assert(ops.length == results.length, "test: all arrays must be the same length!")
        poke(dut.io.ena, true)
        for (i <- 0 until results.length) {
            poke(dut.io.op, ops(i))
            poke(dut.io.din, inputs(i))
            step(1)
            expect(dut.io.accu, results(i).asUInt(size.W), "Op " + ops(i) + " failed")
        }
    }

    // Depending on the operation to be performed, given as input, returns a
    // function corresponding to that of the AluAccu
    def func(op: Int): (BigInt, BigInt) => BigInt = {
        op match {
            case 0 => (a, _) => a
            case 1 => (a, b) => a + b
            case 2 => (a, b) => a - b
            case 3 => (a, b) => a & b
            case 4 => (a, b) => a | b
            case 5 => (a, b) => a ^ b
            case 6 => (_, b) => b
            case 7 => (a, _) => a >> 1
            case _ => (_, _) => BigInt(0)
        }
    }

    // Generate a bunch of random numbers
    val rng = new Random(12345678)
    val mask = (BigInt(1) << size) - 1
    val inputs = Array.fill(Test.numtests)(BigInt.apply(size, rng)).map(_ & mask)
    val ops = Array.fill(Test.numtests)(rng.nextInt()).map(_ & 0x7)
    val results = inputs.zip(ops).foldLeft(Array[BigInt](0)) {
        (acc, tup) => 
        tup match {
            case (i, op) => acc :+ (func(op)(acc.last, i) & mask)
        }
    }.drop(1)

    // Reset the accumulator
    reset()

    // Test enable
    enableTest()
    reset()

    // Test corner case inputs
    cornerTest()
    reset()

    // Run through all the operations with random inputs
    test(inputs, ops, results)
}

object AluAccuTester extends App {
    for (size <- Test.sizes) {
        chisel3.iotesters.Driver(() => new AluAccuChisel(size)) {
            c => new AluAccuTester(c, size)
        }
    }
}

class AluAccuSTester extends FlatSpec with Matchers {
    for (size <- Test.sizes) {
        size + "-bit tester" should "pass" in {
            chisel3.iotesters.Driver(() => new AluAccuChisel(size)) {
                c => new AluAccuTester(c, size)
            } should be (true)
        }
    }
}
