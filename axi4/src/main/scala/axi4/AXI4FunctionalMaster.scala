/**
 * Author:  Hans Jakob Damsgaard, hansjakobdamsgaard@gmail.com
 * 
 * Purpose: Implementation of a testing framework for AXI4-compliant devices.
 * 
 * Content: A functional AXI master implemented with ChiselTest.
*/

import axi4._

import chisel3._
import chisel3.util._
import chiseltest._
import scala.util.Random

/** An AXI4 functional master
 * 
 * @param dut a slave DUT
 * 
 * @note [[initialize]] method must be called before any transactions are started
 */
class AXI4FunctionalMaster[T <: Slave](dut: T) {
  /** DUT information */
  private[this] val idW     = dut.idW
  private[this] val addrW   = dut.addrW
  private[this] val dataW   = dut.dataW

  /** Shortcuts to the channel IO */
  private[this] val aw      = dut.io.aw
  private[this] val dw      = dut.io.dw
  private[this] val wr      = dut.io.wr
  private[this] val ar      = dut.io.ar
  private[this] val dr      = dut.io.dr
  private[this] val resetn  = dut.reset
  private[this] val clk     = dut.clock

  /** Interface and transaction state */
  // TODO: Fill in here
  private[this] var init = false

  /** Initialize the interface (reset) 
   * 
   * @note MUST be called first to ensure correct operation
   */
  def initialize() = {
    
    init = true
  }

  /** Start a write transaction to the given address
   * 
   * @param addr start write address
   * 
   * @note [[addr]] must fit within the slave DUT's write address width
   */
  def startWrite(addr: BigInt) = {
    require(init, "interface must be initialized before starting write transactions")
    require(log2Up(addr) <= addrW, s"address must fit within DUT's write address width (got $addr)")
    
  }

  /** Write data to earliest unfinished write transaction
   * 
   * @param data data to write
   * 
   * @note [[data]] must fit within the slave DUT's write data width
   */
  def writeData(data: BigInt) = {
    require(init, "interface must be initialized before starting write transactions")
    require(log2Up(data) <= dataW, s"data must fit within DUT's write data width (got $data)")

  }

  /** Check write response 
   * 
   * @note 
   * - write responses are continuously stored in an internal queue by a second thread
   * - reading is destructive; i.e., the response being checked is removed from the queue
  */
  def checkResponse() = {
    require(init, "interface must be initialized before starting write transactions")

  }

  /** Start a read transaction from the given address
   * 
   * @param addr start read address
   * 
   * @note [[addr]] must fit within the slave DUT's read address width
   */
  def startRead() = {
    require(init, "interface must be initialized before starting write transactions")

  }

  /** Read data from the earliest unfinished read transaction
   * 
   * @note read value is simply returned, NOT validated
   */
  def readData() = {
    require(init, "interface must be initialized before starting write transactions")
    
  }

  /** Read and validate data from the earliest unfinished read transaction
   * 
   * @param data expected data to read
   * 
   * @note [[data]] must fit within the slave DUT's read data width
   */
  def readAndValidateData(data: BigInt) = {
    require(init, "interface must be initialized before starting write transactions")
    require(log2Up(data) <= dataW, s"data must fit within DUT's read data width (got $data)")

  }
}
