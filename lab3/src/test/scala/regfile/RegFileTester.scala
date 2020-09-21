import chisel3._
import chisel3.iotesters._
import org.scalatest._
import scala.util.Random

// For testing different numbers of entries and data widths in the register file
object Test {
    val addrwidths = List(5, 6, 7, 8)
    val datawidths = List(16, 32, 64, 128)
    val numTests = 128
}

// According to the test plan, the following is to be performed:
// - Writing to location zero doesn't change the register file
// - Test reading and writing from all register locations
// - Test reading from the port currently being written to
class RegFileTester[T <: RegFile](dut: T, size: Int, width: Int) extends PeekPokeTester(dut) {
    def resetTest() = {
        poke(dut.io.regWrite, true)
        poke(dut.io.writeData, 0)
        for (i <- 0 until (1 << size)) {
            poke(dut.io.registerRd, i)
            step(1)
        }
        poke(dut.io.regWrite, false)
        for (i <- 0 until (1 << size)) {
            poke(dut.io.registerRs1, i)
            poke(dut.io.registerRs2, i)
            step(1)
            expect(dut.io.data1, 0)
            expect(dut.io.data2, 0)
        }
    }

    def zeroTest() = {
        poke(dut.io.regWrite, true)
        poke(dut.io.registerRd, 0)
        poke(dut.io.writeData, (BigInt(1) << width) - 1)
        step(1)
        poke(dut.io.regWrite, false)
        poke(dut.io.registerRs1, 0)
        poke(dut.io.registerRs2, 0)
        step(1)
        expect(dut.io.data1, 0)
        expect(dut.io.data2, 0)
    }

    def randomTest() = {
        val rng = new Random(12345678)
        val mask = (BigInt(1) << width) - 1
        val indices = Array.fill(Test.numTests) { rng.nextInt(1 << size) }
        val testData = Array.fill(Test.numTests) { BigInt.apply(width, rng) & mask }
        for (i <- 0 until Test.numTests) {
            val ind = indices(i)
            val data = testData(i)
            poke(dut.io.regWrite, true)
            poke(dut.io.registerRd, ind)
            poke(dut.io.writeData, data)
            step(1)
            poke(dut.io.regWrite, false)
            poke(dut.io.registerRs1, ind)
            poke(dut.io.registerRs2, ind)
            step(1)
            if (ind == 0) {
                expect(dut.io.data1, 0)
                expect(dut.io.data2, 0)
            } else {
                expect(dut.io.data1, data)
                expect(dut.io.data2, data)
            }
        }
    }

    def concurrentTest() = {
        val testData = (BigInt(1) << width) - 1
        poke(dut.io.regWrite, true)
        poke(dut.io.writeData, testData)
        for (i <- 1 until (1 << size)) {
            poke(dut.io.registerRd, i)
            poke(dut.io.registerRs1, i)
            poke(dut.io.registerRs2, i)
            step(1)
            expect(dut.io.data1, testData)
            expect(dut.io.data2, testData)
        }
    }

    // Reset the register file by writing all zeros to it and
    // reading them out again
    resetTest()

    // Test location zero
    zeroTest()

    // Test writing and reading out random values from all register locations
    randomTest()

    // Test reading from the register being currently written to
    concurrentTest()
}

object RegFileTester extends App {
    for (size <- Test.addrwidths) {
        for (width <- Test.datawidths) {
            chisel3.iotesters.Driver(() => new RegFileVec(size, width)) {
                c => new RegFileTester(c, size, width)
            }
        }
    }
}

class RegFileSTester extends FlatSpec with Matchers {
    for (size <- Test.addrwidths) {
        for (width <- Test.datawidths) {
            size+"-bit address, " + width + "-bit data test" should "pass" in {
                chisel3.iotesters.Driver(() => new RegFileVec(size, width)) {
                    c => new RegFileTester(c, size, width)
                } should be (true)
            } 
        }
    }
}
