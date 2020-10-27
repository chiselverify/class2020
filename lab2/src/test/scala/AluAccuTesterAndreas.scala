import chisel3._
import chisel3.iotesters._
import org.scalatest._
import scala.util.Random

class AluAccuTesterAndreas(dut: AluAccuChisel, bitSize: Int) extends PeekPokeTester(dut) {
    val maxValueMask = (1 << bitSize) - 1
    val rng = new Random(643)
    var aluReg = 0
    
    def exeExpected(op: UInt, in: Int) : Int = {
        val result = op match {
            case Types.nop => aluReg
            case Types.add => aluReg + in
            case Types.sub => aluReg - in
            case Types.and => aluReg & in
            case Types.or => aluReg | in
            case Types.xor => aluReg ^ in
            case Types.ld => in
            case Types.shr => aluReg >>> 1
            case _ => throw new Exception("Unknown operation: " + op)
        }
        return result & maxValueMask
    }

    def exeActual(op: UInt, in: Int, run: Boolean) : Int = {
        poke(dut.io.ena, run)
        poke(dut.io.op, op)
        poke(dut.io.din, in)
        step(1)

        return peek(dut.io.accu).intValue()
    }
    
    def testOp(op: UInt, input: Int, run: Boolean) = {
        val beforeExe = peek(dut.io.accu).intValue()

        val expected = exeExpected(op, input)
        val actual = exeActual(op, input, run)

        if (run) {
            assert(expected == actual, "Expected: " + expected + ", Actual: " + actual + ", Op: " + op)
            aluReg = expected
        }
        else {
            val afterExe = peek(dut.io.accu).intValue()
            assert(beforeExe == afterExe, "Accu register changed while not being enabled. Before: " + beforeExe + " Actual: " + afterExe + ", Op: " + op)
        }
    }

    def testRandomOps(count: Int, opTypes: List[UInt]) = {
        var remainingOps = count
        while (remainingOps > 0) {
            val op = opTypes(rng.nextInt(opTypes.size - 1))
            val input = rng.nextInt(maxValueMask)
            val run = rng.nextBoolean()
            testOp(op, input, run)

            if (run) {
                remainingOps = remainingOps - 1
            }
        }
    }
}

class AluAccuTesterAndreasa extends FlatSpec with Matchers {
    def runTest(bitSize: Int, callback: (AluAccuTesterAndreas) => Unit) = {
        chisel3.iotesters.Driver(() => new AluAccuChisel(bitSize)) {
            c => { 
                val tester = new AluAccuTesterAndreas(c, bitSize)
                callback(tester)
                tester 
            }
        }
    }
    val enabledOptions = List(true, false)
    val bitSizes = List(1, 3, 6, 10, 27)
    val opTypes = List(Types.nop, Types.add, Types.sub, Types.and, Types.or, Types.xor, Types.ld, Types.shr)
    val inputValues = List(0, 1, 2, 3, 4)

    for (isEnabled <- enabledOptions) {
        for (bitSize <- bitSizes) {
            val maxValue = (1 << bitSize) - 1
            for (opType <- opTypes) {
                for (input <- inputValues) {
                    //can't give an input that's larger than what the alu can use
                    if (maxValue >= input) {
                        s"enabled: ${isEnabled}, bits: ${bitSize}, op: ${opType}, input: ${input}" should "pass" in { 
                            runTest(bitSize, tester => { tester.testOp(opType, input, isEnabled) }) 
                        }
                    }
                }
                //if the max value hasn't already been tested then make such a test
                if (!inputValues.contains(maxValue))
                {
                    s"enabled: ${isEnabled}, bits: ${bitSize}, op: ${opType}, input: ${maxValue}" should "pass" in { 
                        runTest(bitSize, tester => { tester.testOp(opType, maxValue, isEnabled) }) 
                    }
                }
            }
        }
    }

    val randomOpCounts = List(10, 100, 10000)
    for (bitSize <- bitSizes) {
        for (opsCount <- randomOpCounts) {
            s"${opsCount} random ops with ${bitSize} bit" should "pass" in { runTest(bitSize, tester => { tester.testRandomOps(opsCount, opTypes) })}
        }
    }
}