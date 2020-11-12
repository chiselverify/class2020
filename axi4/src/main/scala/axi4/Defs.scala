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

/** User signal trait */
trait UserSignal {
  val user = Output(UInt())
}

/** AXI4 write address base interface
 * 
 * Defines the mandatory signals in the interface
 * 
 * @param idW the width of the AWID signal in bits
 * @param addrW the width of the AWADDR signal in bits
 */
abstract class AXI4WABase(idW: Int, addrW: Int) extends Bundle {
  require(idW > 0, "the id width must be a positive integer")
  require(addrW > 0, "the address width must be a positive integer")
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
}
class AXI4WA(idW: Int, addrW: Int) extends AXI4WABase(idW, addrW)
class AXI4WAUser(idW: Int, addrW: Int) extends AXI4WA(idW, addrW) with UserSignal

/** AXI4 write data base interface
 * 
 * Defines the mandatory signals in the interface
 * 
 * @param dataW the width of the WDATA signal in bits
 */
abstract class AXI4WDBase(dataW: Int) extends Bundle {
  require(dataW > 0, "the data width must be a positive integer")
  require(isPow2(dataW / 8), "the data width must be a power of 2 multiple of bytes")
  val data    = Output(UInt(dataW.W))
  val strb    = Output(UInt((dataW/8).W))
  val last    = Output(Bool())
}
class AXI4WD(dataW: Int) extends AXI4WDBase(dataW)
class AXI4WDUser(dataW: Int) extends AXI4WD(dataW) with UserSignal

/** AXI4 write response base interface
 * 
 * Defines the mandatory signals in the interface
 * 
 * @param idW the width of the BID signal in bits
 */
abstract class AXI4WRBase(idW: Int) extends Bundle {
  require(idW > 0, "the id width must be a positive integer")
  val id      = Input(UInt(idW.W))
  val resp    = Input(UInt(2.W))
}
class AXI4WR(idW: Int) extends AXI4WRBase(idW)
class AXI4WRUser(idW: Int) extends AXI4WR(idW) with UserSignal

/** AXI4 read address base interface
 * 
 * Defines the mandatory signals in the interface
 * 
 * @param idW the width of the ARID signal in bits
 * @param addrW the width of the ARADDR signal in bits
 */
abstract class AXI4RABase(idW: Int, addrW: Int) extends Bundle {
  require(idW > 0, "the id width must be a positive integer")
  require(addrW > 0, "the address width must be a positive integer")
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
}
class AXI4RA(idW: Int, addrW: Int) extends AXI4RABase(idW, addrW)
class AXI4RAUser(idW: Int, addrW: Int) extends AXI4RA(idW, addrW) with UserSignal

/** AXI4 read data base interface
 * 
 * Defines the mandatory signals in the interface
 * 
 * @param idW the width of the RID signal in bits
 * @param dataW the width of the RDATA signal in bits
 */
abstract class AXI4RDBase(idW: Int, dataW: Int) extends Bundle {
  require(idW > 0, "the id width must be a positive integer")
  require(isPow2(dataW / 8), "the data width must be a power of 2 multiple of bytes")
  val id      = Input(UInt(idW.W))
  val data    = Input(UInt(dataW.W))
  val resp    = Input(UInt(2.W))
  val last    = Input(Bool())
}
class AXI4RD(idW: Int, dataW: Int) extends AXI4RDBase(idW, dataW)
class AXI4RDUser(idW: Int, dataW: Int) extends AXI4RD(idW, dataW) with UserSignal

/** AXI4 master interface
 * 
 * Components meant to use this interface should extend it
 * 
 * @param idW the width of the ID signals in bits
 * @param addrW the width of the address signals in bits
 * @param dataW the width of the data read/write signals in bits
 */
abstract class AXI4MasterBase(idW: Int, addrW: Int, dataW: Int) extends Bundle {
  /** Fields implementing each of the AXI channels
   * 
   * [[aw]] is the write address channel
   * [[dw]] is the write data channel
   * [[wr]] is the write response channel
   * [[ar]] is the read address channel
   * [[dr]] is the read data channel
   */
  val aw = Decoupled(new AXI4WA(idW, addrW))
  val dw = Decoupled(new AXI4WD(dataW))
  val wr = Flipped(Decoupled(Flipped(new AXI4WR(idW))))
  val ar = Decoupled(new AXI4RA(idW, addrW))
  val dr = Flipped(Decoupled(Flipped(new AXI4RD(idW, dataW))))
}
class AXI4Master(idW: Int, addrW: Int, dataW: Int) extends AXI4MasterBase(idW, addrW, dataW)
class AXI4MasterUser(idW: Int, addrW: Int, dataW: Int) extends AXI4Master(idW, addrW, dataW) {
  override val aw = Decoupled(new AXI4WAUser(idW, addrW))
  override val dw = Decoupled(new AXI4WDUser(dataW))
  override val wr = Flipped(Decoupled(Flipped(new AXI4WRUser(idW))))
  override val ar = Decoupled(new AXI4RAUser(idW, addrW))
  override val dr = Flipped(Decoupled(Flipped(new AXI4RDUser(idW, dataW))))
}

/** AXI4 slave interface
 * 
 * Components meant to use this interface should extend it
 * 
 * @param idW the width of the ID signals in bits
 * @param addrW the width of the address signals in bits
 * @param dataW the width of the data read/write signals in bits
 */
abstract class AXI4SlaveBase(idW: Int, addrW: Int, dataW: Int) extends Bundle {  
  /** Fields implementing each of the AXI channels
   * 
   * [[aw]] is the write address channel
   * [[dw]] is the write data channel
   * [[wr]] is the write response channel
   * [[ar]] is the read address channel
   * [[dr]] is the read data channel
   */
  val aw = Flipped(Decoupled(new AXI4WA(idW, addrW)))
  val dw = Flipped(Decoupled(new AXI4WD(dataW)))
  val wr = Flipped(Flipped(Decoupled(Flipped(new AXI4WR(idW)))))
  val ar = Flipped(Decoupled(new AXI4RA(idW, addrW)))
  val dr = Flipped(Flipped(Decoupled(Flipped(new AXI4RD(idW, dataW)))))
}
class AXI4Slave(idW: Int, addrW: Int, dataW: Int) extends AXI4SlaveBase(idW, addrW, dataW)
class AXI4SlaveUser(idW: Int, addrW: Int, dataW: Int) extends AXI4Slave(idW, addrW, dataW) {
  override val aw = Flipped(Decoupled(new AXI4WAUser(idW, addrW)))
  override val dw = Flipped(Decoupled(new AXI4WDUser(dataW)))
  override val wr = Flipped(Flipped(Decoupled(Flipped(new AXI4WRUser(idW)))))
  override val ar = Flipped(Decoupled(new AXI4RAUser(idW, addrW)))
  override val dr = Flipped(Flipped(Decoupled(Flipped(new AXI4RDUser(idW, dataW)))))
}
