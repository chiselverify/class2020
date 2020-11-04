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

/** AXI4 burst encodings */
object BurstEncodings {
  val fixed             = "b00".U
  val incr              = "b01".U
  val wrap              = "b10".U
}

/** AXI lock encodings */
object LockEncodings {
  val normal_access     = false.B
  val exclusive_access  = true.B
}

/** AXI4 memory encodings */
object MemoryEncodings {
  val device_nonbuf     = "b0000".U
  val device_buf        = "b0001".U
  val normal_nonbuf     = "b0010".U 
  val normal_buf        = "b0011".U
  val wt_noalloc        = "b0110".U
  val wt_readalloc      = "b0110".U
  val wt_writealloc     = "b1110".U
  val wt_rwalloc        = "b1110".U
  val wb_noalloc        = "b0111".U
  val wb_readalloc      = "b0111".U
  val wb_writealloc     = "b1111".U
  val wb_rwalloc        = "b1111".U
}

/** AXI4 protection encodings */
object ProtectionEncodings {
  val data_sec_upriv    = "b000".U
  val data_sec_priv     = "b001".U
  val data_nsec_upriv   = "b010".U
  val data_nsec_priv    = "b011".U
  val instr_sec_upriv   = "b100".U
  val instr_sec_priv    = "b101".U
  val instr_nsec_upriv  = "b110".U
  val instr_nsec_priv   = "b111".U
}

/** AXI4 response encodings */
object ResponseEncodings {
  val okay              = "b00".U
  val exokay            = "b01".U
  val slverr            = "b10".U
  val decerr            = "b11".U
}

// TODO: Figure out whether this signal works when its width is inferred
/** User signal trait */
trait UserSignal {
  val user = Output(UInt())
}

/** Region signal trait */
trait RegionSignal {
  val region = Output(UInt(4.W))
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
}
case class AXI4WA(idW: Int, addrW: Int) extends AXI4WABase(idW, addrW)
case class AXI4WAUser(idW: Int, addrW: Int) extends AXI4WABase(idW, addrW) with UserSignal
case class AXI4WARegion(idW: Int, addrW: Int) extends AXI4WABase(idW, addrW) with RegionSignal
case class AXI4WAFull(idW: Int, addrW: Int) extends AXI4WABase(idW, addrW) with UserSignal with RegionSignal

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
case class AXI4WD(dataW: Int) extends AXI4WDBase(dataW)
case class AXI4WDUser(dataW: Int) extends AXI4WDBase(dataW) with UserSignal
case class AXI4WDRegion(dataW: Int) extends AXI4WDBase(dataW) with RegionSignal
case class AXI4WDFull(dataW: Int) extends AXI4WDBase(dataW) with UserSignal with RegionSignal

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
case class AXI4WR(idW: Int) extends AXI4WRBase(idW)
case class AXI4WRUser(idW: Int) extends AXI4WRBase(idW) with UserSignal
case class AXI4WRRegion(idW: Int) extends AXI4WRBase(idW) with RegionSignal
case class AXI4WRFull(idW: Int) extends AXI4WRBase(idW) with UserSignal with RegionSignal

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
}
case class AXI4RA(idW: Int, addrW: Int) extends AXI4RABase(idW, addrW)
case class AXI4RAUser(idW: Int, addrW: Int) extends AXI4RABase(idW, addrW) with UserSignal
case class AXI4RARegion(idW: Int, addrW: Int) extends AXI4RABase(idW, addrW) with RegionSignal
case class AXI4RAFull(idW: Int, addrW: Int) extends AXI4RABase(idW, addrW) with UserSignal with RegionSignal

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
case class AXI4RD(idW: Int, dataW: Int) extends AXI4RDBase(idW, dataW)
case class AXI4RDUser(idW: Int, dataW: Int) extends AXI4RDBase(idW, dataW) with UserSignal
case class AXI4RDRegion(idW: Int, dataW: Int) extends AXI4RDBase(idW, dataW) with RegionSignal
case class AXI4RDFull(idW: Int, dataW: Int) extends AXI4RDBase(idW, dataW) with UserSignal with RegionSignal

// TODO: Finish the stuff below here

/** AXI4 master interface
 * 
 * Components meant to use this interface should extend it
 */
abstract class AXI4MasterBase(idW: Int, addrW: Int, wdataW: Int, rdataW: Int) extends Bundle {
  /** Fields implementing each of the AXI channels
   * 
   * [[aw]] is the write address channel
   * [[dw]] is the write data channel
   * [[wr]] is the write response channel
   * [[ar]] is the read address channel
   * [[dr]] is the read data channel
   */
  val aw = Decoupled(new AXI4WA(idW, addrW))
  val dw = Decoupled(new AXI4WD(wdataW))
  val wr = Decoupled(new AXI4WR(idW))
  val ar = Decoupled(new AXI4RA(idW, addrW))
  val dr = Decoupled(new AXI4RD(idW, rdataW))

  /** Status fields indicating support for USER and REGION signals */
  val has_user = false
  val has_region = false
}
case class AXI4Master(idW: Int, addrW: Int, dataW: Int) extends AXI4MasterBase(idW, addrW, dataW, dataW)
// TODO: Add other extensions

/** AXI4 slave interface
 * 
 * Components meant to use this interface should extend it
 */
abstract class AXI4SlaveInterface extends Bundle {  
  /** Fields implementing each of the AXI channels
   * 
   * [[aw]] is the write address channel
   * [[dw]] is the write data channel
   * [[wr]] is the write response channel
   * [[ar]] is the read address channel
   * [[dr]] is the read data channel
   */
  val aw = Flipped(Decoupled(new AXI4WA(idW, addrW)))
  val dw = Flipped(Decoupled(new AXI4WD(wdataW)))
  val wr = Flipped(Decoupled(new AXI4WR(idW)))
  val ar = Flipped(Decoupled(new AXI4RA(idW, addrW)))
  val dr = Flipped(Decoupled(new AXI4RD(idW, rdataW)))

  /** Status fields indicating support for USER and REGION signals */
  val has_user = false
  val has_region = false
}
case class AXI4Slave(idW: Int, addrW: Int, dataW: Int) extends AXI4SlaveInterface(idw, addrW, dataW, dataW)
// TODO: Add other extensions
