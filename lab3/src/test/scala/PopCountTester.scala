import chisel3._
import chisel3.iotesters._
import org.scalatest._
import scala.util.Random

// Tests different bit widths
object Testset {
    val width = List(8, 10, 16)
    val numTest = 4
}

/*  The test plan:
    - Test functionality of the circuit
    - Test expected behaviour from boolean signals
    - Test corner cases and random inputs
*/

class PopCountTester(dut: PopCounterTop, size: Int) extends PeekPokeTester(dut) {

    // The function pokes a value of zero to data input, and runs a full 
    // cycle to ensure the circuit is reset before test
    def reset() = {
        poke(dut.io.din, 0.U)
        step(1)
        poke(dut.io.dinValid, true)
        while (peek(dut.io.popCntValid) == BigInt(0)){
            step(1)
        }
        poke(dut.io.dinValid, false)
        poke(dut.io.popCntReady, true.B)
        step(1)
        expect(dut.io.popCnt, 0.U)
    }

    // Corner cases for testing
    def cornerCase() = {
        // Corner cases of interest is the max possible int and 0
        val maxInt = (BigInt(1) << size) - 1

        poke(dut.io.din, maxInt)
        step(1)
        poke(dut.io.dinValid, true.B)
        while (peek(dut.io.popCntValid) == BigInt(0)){
            step(1)
        }
        poke(dut.io.dinValid, false)
        poke(dut.io.popCntReady, true.B)
        expect(dut.io.popCnt, size)

        poke(dut.io.din, 0)
        poke(dut.io.dinValid, true.B)
        while (peek(dut.io.popCntValid) == BigInt(0)){
            step(1)
        }
        poke(dut.io.dinValid, false)
        poke(dut.io.popCntReady, true.B)
        expect(dut.io.popCnt, BigInt(0))
    }

    // Generates the expected count from a number
    def count(num: BigInt) = {
        var res = 0
        var numShift = num
        while (numShift != 0) {
            res = if ((numShift & BigInt(1)) == 1) res + 1 else res
            numShift >>= 1
        }
        res
    }

    // Test the PopCounter with a new random number until
    // number of tests is achieved
    def popCountTest(inputs: Array[BigInt]) = {
        for (i <- 0 until Testset.numTest) {
            poke(dut.io.din, inputs(i))
            step(1)
            poke(dut.io.dinValid, true.B)
            println("Input is " + inputs(i).toString)
            while (peek(dut.io.popCntValid) == BigInt(0)){
                step(1)
            }
            poke(dut.io.dinValid, false)
            poke(dut.io.popCntReady, true.B)
            expect(dut.io.popCnt, count(inputs(i)))
        }
    }

    // Test of boolean signals of the popcounter
    def handShakeTest() = {

        poke(dut.io.din, 1)
        step(1)
        for (i <- 0 to 3) {
            poke(dut.io.dinValid, 0)
            poke(dut.io.dinReady, 0)
            poke(dut.io.popCntValid, 0)
            poke(dut.io.popCntReady, 0)
            step(1)
            if (i == 0) {
                poke(dut.io.dinValid, 1)
            } else if (i == 1) {
                poke(dut.io.dinReady, 1)
            } else if (i == 2) {
                poke(dut.io.popCntValid, 1)
            } else {
                poke(dut.io.popCntReady, 1)
            }
            step(size)
            expect(dut.io.popCnt, 0)
        }
        poke(dut.io.dinValid, 1)
        poke(dut.io.dinReady, 1)
        poke(dut.io.popCntValid, 1)
        poke(dut.io.popCntReady, 1)
        step(size)
        expect(dut.io.popCnt, 1)

    }

    val rng = new Random(15164845)
    val randNum = Array.fill(Testset.numTest) {BigInt(rng.nextInt((1 << size) - 1))}

    // Reset of counter
    reset()

    // Test of corner case inputs
    cornerCase()
    reset()

    // Test of popcounter with random inputs
    popCountTest(randNum)
    reset()

    // Test of handshake signals
    handShakeTest()
    reset()
}

object PopCountTester extends App {
    for (size <- Testset.width) {
        chisel3.iotesters.Driver(() => new PopCounterTop(size)) {
            c => new PopCountTester(c, size)
        }
    }
}

class PopCountScTester extends FlatSpec with Matchers {
    for (size <- Testset.width) {
        "Test of " + size + " bits" should "pass" in {
            chisel3.iotesters.Driver(() => new PopCounterTop(size)) {
                c => new PopCountTester(c, size)
            } should be (true)
        }
    }
}