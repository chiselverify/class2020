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

/** Constant naming according to https://docs.scala-lang.org/style/naming-conventions.html */

/** AXI4 burst encodings */
object BurstEncodings {
  val Fixed             = "b00".U
  val Incr              = "b01".U
  val Wrap              = "b10".U
}

/** AXI lock encodings */
object LockEncodings {
  val NormalAccess     = false.B
  val ExclusiveAccess  = true.B
}

/** AXI4 memory encodings */
object MemoryEncodings {
  val DeviceNonbuf     = "b0000".U
  val DeviceBuf        = "b0001".U
  val NormalNonbuf     = "b0010".U 
  val NormalBuf        = "b0011".U
  val WtNoalloc        = "b0110".U
  val WtReadalloc      = "b0110".U
  val WtWritealloc     = "b1110".U
  val WtRwalloc        = "b1110".U
  val WbNoalloc        = "b0111".U
  val WbReadalloc      = "b0111".U
  val WbWritealloc     = "b1111".U
  val WbRwalloc        = "b1111".U
}

/** AXI4 protection encodings */
object ProtectionEncodings {
  val DataSecUpriv    = "b000".U
  val DataSecPriv     = "b001".U
  val DataNsecUpriv   = "b010".U
  val DataNsecPriv    = "b011".U
  val InstrSecUpriv   = "b100".U
  val InstrSecPriv    = "b101".U
  val InstrNsecUpriv  = "b110".U
  val InstrNsecPriv   = "b111".U
}

/** AXI4 response encodings */
object ResponseEncodings {
  val Okay              = "b00".U
  val Exokay            = "b01".U
  val Slverr            = "b10".U
  val Decerr            = "b11".U
}

/** AXI4 write address base interface
 * 
 * Defines the mandatory signals in the interface
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

/** AXI4 write data base interface
 * 
 * Defines the mandatory signals in the interface
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

/** AXI4 write response base interface
 * 
 * Defines the mandatory signals in the interface
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

/** AXI4 read address base interface
 * 
 * Defines the mandatory signals in the interface
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

/** AXI4 read data base interface
 * 
 * Defines the mandatory signals in the interface
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
 * Components meant to use this interface should extend it
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
 * Components meant to use this interface should extend it
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
