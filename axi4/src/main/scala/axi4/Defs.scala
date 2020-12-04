/**
 * Author:  Hans Jakob Damsgaard, hansjakobdamsgaard@gmail.com
 * 
 * Purpose: Implementation of a testing framework for AXI4-compliant devices.
 * 
 * Content: Interface definitions for AXI4.
*/

package axi4

import chisel3._
import chisel3.util._

/** AXI4 write address interface
 * 
 * @param addrW the width of the AWADDR signal in bits
 * @param idW the width of the AWID signal in bits, defaults to 0
 * @param userW the width of the AWUSER signal in bits, defaults to 0
 */
class WA(val addrW: Int, val idW: Int = 0, val userW: Int = 0) extends Bundle {
  require(addrW > 0, "the address width must be a positive integer")
  require(idW >= 0, "the id width must be a non-negative integer")
  require(userW >= 0, "the user with must be a non-negative integer")
  val id      = Output(UInt(idW.W))
  val addr    = Output(UInt(addrW.W))
  val len     = Output(UInt(8.W))
  val size    = Output(UInt(3.W))
  val burst   = Output(UInt(2.W))
  val lock    = Output(Bool())
  val cache   = Output(UInt(4.W))
  val prot    = Output(UInt(3.W))
  val qos     = Output(UInt(4.W))
  val region  = Output(UInt(4.W))
  val user    = Output(UInt(userW.W))
}

/** AXI4 write data interface
 * 
 * @param dataW the width of the WDATA signal in bits
 * @param userW the width of the WUSER signal in bits, defaults to 0
 */
class WD(val dataW: Int, val userW: Int = 0) extends Bundle {
  require(dataW > 0, "the data width must be a positive integer")
  require(isPow2(dataW / 8), "the data width must be a power of 2 multiple of bytes")
  require(userW >= 0, "the user with must be a non-negative integer")
  val data    = Output(UInt(dataW.W))
  val strb    = Output(UInt((dataW/8).W))
  val last    = Output(Bool())
  val user    = Output(UInt(userW.W))
}

/** AXI4 write response interface
 * 
 * @param idW the width of the BID signal in bits, defaults to 0
 * @param userW the width of the BUSER signal in bits, defaults to 0
 */
class WR(val idW: Int = 0, val userW: Int = 0) extends Bundle {
  require(idW >= 0, "the id width must be a non-negative integer")
  require(userW >= 0, "the user with must be a non-negative integer")
  val id      = Input(UInt(idW.W))
  val resp    = Input(UInt(2.W))
  val user    = Input(UInt(userW.W))
}

/** AXI4 read address interface
 * 
 * @param addrW the width of the ARADDR signal in bits
 * @param idW the width of the ARID signal in bits, defaults to 0
 * @param userW the width of the ARUSER signal in bits, defaults to 0
 */
class RA(val addrW: Int, val idW: Int = 0, val userW: Int = 0) extends Bundle {
  require(addrW > 0, "the address width must be a positive integer")
  require(idW >= 0, "the id width must be a non-negative integer")
  require(userW >= 0, "the user with must be a non-negative integer")
  val id      = Output(UInt(idW.W))
  val addr    = Output(UInt(addrW.W))
  val len     = Output(UInt(8.W))
  val size    = Output(UInt(3.W))
  val burst   = Output(UInt(2.W))
  val lock    = Output(Bool())
  val cache   = Output(UInt(4.W))
  val prot    = Output(UInt(3.W))
  val qos     = Output(UInt(4.W))
  val region  = Output(UInt(4.W))
  val user    = Output(UInt(userW.W))
}

/** AXI4 read data interface
 * 
 * @param dataW the width of the RDATA signal in bits
 * @param idW the width of the RID signal in bits, defaults to 0
 * @param userW the width of the RUSER signal in bits, defaults to 0
 */
class RD(val dataW: Int, val idW: Int = 0, val userW: Int = 0) extends Bundle {
  require(isPow2(dataW / 8), "the data width must be a power of 2 multiple of bytes")
  require(idW >= 0, "the id width must be a non-negative integer")
  require(userW >= 0, "the user with must be a non-negative integer")
  val id      = Input(UInt(idW.W))
  val data    = Input(UInt(dataW.W))
  val resp    = Input(UInt(2.W))
  val last    = Input(Bool())
  val user    = Input(UInt(userW.W))
}

/** AXI4 master interface
 * 
 * @param addrW the width of the address signals in bits
 * @param dataW the width of the data read/write signals in bits
 * @param idW the width of the ID signals in bits, defaults to 0
 * @param userW the width of the user signals in bits, defaults to 0
 */
class MasterInterface(val addrW: Int, val dataW: Int, val idW: Int = 0, val userW: Int = 0) extends Bundle {
  /** Fields implementing each of the AXI channels
   * 
   * [[aw]] is the write address channel
   * [[dw]] is the write data channel
   * [[wr]] is the write response channel
   * [[ar]] is the read address channel
   * [[dr]] is the read data channel
   */
  val aw = Decoupled(new WA(addrW, idW, userW))
  val dw = Decoupled(new WD(dataW, userW))
  val wr = Flipped(Decoupled(Flipped(new WR(idW, userW))))
  val ar = Decoupled(new RA(addrW, idW, userW))
  val dr = Flipped(Decoupled(Flipped(new RD(dataW, idW, userW))))
}

/** AXI4 slave interface
 * 
 * @param addrW the width of the address signals in bits
 * @param dataW the width of the data read/write signals in bits
 * @param idW the width of the ID signals in bits, defaults to 0
 * @param userW the width of the user signals in bits, defaults to 0
 */
class SlaveInterface(val addrW: Int, val dataW: Int, val idW: Int = 0, val userW: Int = 0) extends Bundle {  
  /** Fields implementing each of the AXI channels
   * 
   * [[aw]] is the write address channel
   * [[dw]] is the write data channel
   * [[wr]] is the write response channel
   * [[ar]] is the read address channel
   * [[dr]] is the read data channel
   */
  val aw = Flipped(Decoupled(new WA(addrW, idW, userW)))
  val dw = Flipped(Decoupled(new WD(dataW, userW)))
  val wr = Flipped(Flipped(Decoupled(Flipped(new WR(idW, userW)))))
  val ar = Flipped(Decoupled(new RA(addrW, idW, userW)))
  val dr = Flipped(Flipped(Decoupled(Flipped(new RD(dataW, idW, userW)))))
}
