/**
 * Author:  Hans Jakob Damsgaard, hansjakobdamsgaard@gmail.com
 * 
 * Purpose: Implementation of a testing framework for AXI4-compliant devices.
 * 
 * Content: A tester for an AXI4 interfaced Vivado BRAM IP.
*/

import axi4._
import chisel3._
import chiseltest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.VerilatorBackendAnnotation
import org.scalatest._

class VivadoAXIMemoryTester extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "AXI4 BRAM"
  
  it should "initialize" in {
    test(new VivadoAXIMemory()).withAnnotations(Seq(VerilatorBackendAnnotation)) {
      dut =>
        val master = new AXI4FunctionalMaster(dut)
        master.initialize()
        master.close()
    }
  }

  it should "write and read manually" in {
    test(new VivadoAXIMemory()).withAnnotations(Seq(VerilatorBackendAnnotation)) {
      dut =>
        val master = new AXI4FunctionalMaster(dut)
        master.initialize()

        def printCheck() = {
          println("AWREADY = " + dut.io.aw.ready.peek.litToBoolean)
          println("WREADY = " + dut.io.dw.ready.peek.litToBoolean)
          println("BVALID = " + dut.io.wr.valid.peek.litToBoolean)
          println("ARREADY = " + dut.io.ar.ready.peek.litToBoolean)
          println("RVALID = " + dut.io.dr.valid.peek.litToBoolean)
        }

        // Set some initial values on the necessary signals
        dut.io.dw.bits.data.poke(42.U)
        dut.io.dw.bits.strb.poke("b1111".U)
        dut.io.dw.bits.last.poke(true.B)
        printCheck()

        // Write address
        dut.io.aw.valid.poke(true.B)
        do {
          dut.clock.step()
        } while (!dut.io.aw.ready.peek.litToBoolean)
        printCheck()
        dut.clock.step()
        dut.io.aw.valid.poke(false.B)

        // Write some data
        dut.io.dw.valid.poke(true.B)
        do {
          dut.clock.step()
        } while (!dut.io.dw.ready.peek.litToBoolean) 
        printCheck()
        dut.clock.step()
        dut.io.dw.valid.poke(false.B)

        // Fetch response
        dut.io.wr.ready.poke(true.B)
        while (!dut.io.wr.valid.peek.litToBoolean) {
          dut.clock.step()
        }
        printCheck()
        val r = dut.io.wr.bits.resp.peek.litValue
        println(s"Got response $r")

        // Read address
        dut.io.ar.valid.poke(true.B)
        do { 
          dut.clock.step()
        } while (!dut.io.ar.ready.peek.litToBoolean)
        printCheck()
        dut.clock.step()
        dut.io.ar.valid.poke(false.B)

        // Read some data
        dut.io.dr.ready.poke(true.B)
        while (!dut.io.dr.valid.peek.litToBoolean) {
          dut.clock.step()
        }
        printCheck()
        dut.io.dr.bits.data.expect(42.U)

        master.close()
    }
  }
}
