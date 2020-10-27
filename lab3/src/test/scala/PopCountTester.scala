import chisel3._
import chisel3.iotesters._
import org.scalatest._
import scala.util.Random

// Tests different bit widths
object Testset {
    val width = List(8, 12, 16)
    val numTest = 256
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
        poke(dut.io.dinValid, true.B)
        while (peek(dut.io.popCntValid) == BigInt(0)){
            step(1)
        }
        poke(dut.io.dinValid, false.B)
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
        poke(dut.io.dinValid, false.B)
        poke(dut.io.popCntReady, true.B)
        expect(dut.io.popCnt, size)
        step(1)

        poke(dut.io.din, 0)
        poke(dut.io.dinValid, true.B)
        while (peek(dut.io.popCntValid) == BigInt(0)){
            step(1)
        }
        poke(dut.io.dinValid, false.B)
        poke(dut.io.popCntReady, true.B)
        expect(dut.io.popCnt, BigInt(0))
        step(1)
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
            while (peek(dut.io.popCntValid) == BigInt(0)){
                step(1)
            }
            poke(dut.io.dinValid, false.B)
            poke(dut.io.popCntReady, true.B)
            expect(dut.io.popCnt, count(inputs(i)))
            step(1)
        }
    }

    // Test of boolean signals of the popcounter
    def handShakeTest() = {

        // dinValid test
        // Input 1 is poked to an invalid handshake, then dinValid is poked to high,
        // making the handshake valid
        poke(dut.io.din, 1)
        poke(dut.io.dinValid, false.B)
        poke(dut.io.popCntReady, false.B)
        step(1)
        poke(dut.io.din, 3)
        poke(dut.io.dinValid, true.B)
        while (peek(dut.io.popCntValid) == BigInt(0)) {
            step(1)
        }
        expect(dut.io.popCnt, 2)
        poke(dut.io.popCntReady, true.B)
        step(1)

        // dinReady test
        // Input 1 is poked with valid handshake, then input 3 is poked, but
        // since handshake is now invalid, expected output is 1
        poke(dut.io.din, 1)
        poke(dut.io.dinValid, true.B)
        poke(dut.io.popCntReady, false.B)
        step(1)
        expect(dut.io.dinReady, false.B)
        poke(dut.io.din, 3)
        while (peek(dut.io.popCntValid) == BigInt(0)) {
            step(1)
        }
        expect(dut.io.popCnt, 1)
        poke(dut.io.popCntReady, true.B)
        step(1)
        
        // popCntValid test
        // If popCntReady is high before popCntValid, the state should not 
        // return to idle
        poke(dut.io.din, 1)
        poke(dut.io.dinValid, true.B)
        poke(dut.io.popCntReady, false.B)
        step(1)
        poke(dut.io.popCntReady, true.B)
        step(1)
        expect(dut.io.dinReady, false.B)
        while (peek(dut.io.popCntValid) == BigInt(0)) {
            step(1)
        }
        poke(dut.io.popCntReady, true.B)
        step(1)
        expect(dut.io.dinReady, true.B)

        // popCntReady test
        poke(dut.io.din, 1)
        poke(dut.io.dinValid, true.B)
        poke(dut.io.popCntReady, false.B)
        step(1)
        while (peek(dut.io.popCntValid) == BigInt(0)) {
            step(1)
        }
        expect(dut.io.dinReady, false.B)
        poke(dut.io.popCntReady, true.B)
        step(1)
        expect(dut.io.dinReady, true.B)
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