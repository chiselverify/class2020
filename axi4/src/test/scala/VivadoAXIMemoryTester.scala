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
import org.scalatest._

class VivadoAXIMemoryTester extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "AXI4 BRAM"
  
  it should "initialize" in {
    test(new VivadoAXIMemory()) {
      dut =>
        val master = new AXI4FunctionalMaster(dut)
        master.initialize()
        master.close()
    }
  }

  it should "write and read" in {
    test(new VivadoAXIMemory()) {
      dut =>
        val master = new AXI4FunctionalMaster(dut)
        master.initialize()
        master.createWriteTrx(0, Seq[BigInt](42), size = 2)
        var r = master.checkResponse()
        do {
          dut.clock.step()
          r = master.checkResponse()
        } while (r == None)
        var resp = r match {
          case Some(r) => r.resp.litValue
          case _ => 0
        }
        println(s"Got response code $resp")
        master.createReadTrx(0, size = 2)
        var v = master.checkReadData()
        do {
          dut.clock.step()
          v = master.checkReadData()
        } while (v == None)
        var values = v match {
          case Some(v) => v
          case _ => Seq[BigInt](-1)
        }
        println(s"Got read data $values")
        master.close()
    }
  }
}
