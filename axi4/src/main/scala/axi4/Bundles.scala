/**
 * Author:  Hans Jakob Damsgaard, hansjakobdamsgaard@gmail.com
 * 
 * Purpose: Implementation of a testing framework for AXI4-compliant devices.
 * 
 * Content: Bundle definitions for AXI4.
*/

package axi4

import chisel3._
import chisel3.util.isPow2

/** AXI4 write address
 * 
 * @param addrW the width of the AWADDR signal in bits
 * @param idW the width of the AWID signal in bits
 * @param userW the width of the AWUSER signal in bits
 */
class WA(val addrW: Int, val idW: Int, val userW: Int) extends Bundle {
  require(addrW > 0, "the address width must be a positive integer")
  require(idW >= 0, "the id width must be a non-negative integer")
  require(userW >= 0, "the user with must be a non-negative integer")
  val id      = UInt(idW.W)
  val addr    = UInt(addrW.W)
  val len     = UInt(8.W)
  val size    = UInt(3.W)
  val burst   = UInt(2.W)
  val lock    = Bool()
  val cache   = UInt(4.W)
  val prot    = UInt(3.W)
  val qos     = UInt(4.W)
  val region  = UInt(4.W)
  val user    = UInt(userW.W)
}
object WA {
  /** Alternative constructor
   *
   * @param addrW the width of the AWADDR signal in bits
   * @param idW the width of the AWID signal in bits, defaults to 0
   * @param userW the width of the AWUSER signal in bits, defaults to 0
   * @return
   */
  def apply(addrW: Int, idW: Int = 0, userW: Int = 0) = new WA(addrW, idW, userW)
}

/** AXI4 write data
 * 
 * @param dataW the width of the WDATA signal in bits
 * @param userW the width of the WUSER signal in bits
 */
class WD(val dataW: Int, val userW: Int) extends Bundle {
  require(dataW > 0, "the data width must be a positive integer")
  require(isPow2(dataW / 8), "the data width must be a power of 2 multiple of bytes")
  require(userW >= 0, "the user with must be a non-negative integer")
  val data    = UInt(dataW.W)
  val strb    = UInt((dataW/8).W)
  val last    = Bool()
  val user    = UInt(userW.W)
}
object WD {
  /** Alternative constructor
   *
   * @param dataW the width of the WDATA signal in bits
   * @param userW the width of the WUSER signal in bits, defaults to 0
   * @return an unitialized WD object
   */
  def apply(dataW: Int, userW: Int = 0) = new WD(dataW, userW)
}

/** AXI4 write response
 * 
 * @param idW the width of the BID signal in bits
 * @param userW the width of the BUSER signal in bits
 */
class WR(val idW: Int, val userW: Int) extends Bundle {
  require(idW >= 0, "the id width must be a non-negative integer")
  require(userW >= 0, "the user with must be a non-negative integer")
  val id      = UInt(idW.W)
  val resp    = UInt(2.W)
  val user    = UInt(userW.W)
}
object WR {
  /** Alternative constructor
   * 
   * @param idW the width of the BID signal in bits, defaults to 0
   * @param userW the width of the BUSER signal in bits, defaults to 0
   * @return an unitialized WR object
   */
  def apply(idW: Int = 0, userW: Int = 0) = new WR(idW, userW)
}

/** AXI4 read address
 * 
 * @param addrW the width of the ARADDR signal in bits
 * @param idW the width of the ARID signal in bits
 * @param userW the width of the ARUSER signal in bits
 */
class RA(val addrW: Int, val idW: Int, val userW: Int) extends Bundle {
  require(addrW > 0, "the address width must be a positive integer")
  require(idW >= 0, "the id width must be a non-negative integer")
  require(userW >= 0, "the user with must be a non-negative integer")
  val id      = UInt(idW.W)
  val addr    = UInt(addrW.W)
  val len     = UInt(8.W)
  val size    = UInt(3.W)
  val burst   = UInt(2.W)
  val lock    = Bool()
  val cache   = UInt(4.W)
  val prot    = UInt(3.W)
  val qos     = UInt(4.W)
  val region  = UInt(4.W)
  val user    = UInt(userW.W)
}
object RA {
  /** Alternative constructor
   *
   * @param addrW the width of the ARADDR signal in bits
   * @param idW the width of the ARID signal in bits, defaults to 0
   * @param userW the width of the ARUSER signal in bits, defaults to 0
   * @return an unitialized RA object
   */
  def apply(addrW: Int, idW: Int = 0, userW: Int = 0) = new RA(addrW, idW, userW)
}

/** AXI4 read data
 * 
 * @param dataW the width of the RDATA signal in bits
 * @param idW the width of the RID signal in bits
 * @param userW the width of the RUSER signal in bits
 */
class RD(val dataW: Int, val idW: Int, val userW: Int) extends Bundle {
  require(isPow2(dataW / 8), "the data width must be a power of 2 multiple of bytes")
  require(idW >= 0, "the id width must be a non-negative integer")
  require(userW >= 0, "the user with must be a non-negative integer")
  val id      = UInt(idW.W)
  val data    = UInt(dataW.W)
  val resp    = UInt(2.W)
  val last    = Bool()
  val user    = UInt(userW.W)
}
object RD {
  /** Alternative constructor
   *
   * @param dataW the width of the RDATA signal in bits
   * @param idW the width of the RID signal in bits, defaults to 0
   * @param userW the width of the RUSER signal in bits, defaults to 0
   * @return an uninitialized RD object
   */
  def apply(dataW: Int, idW: Int = 0, userW: Int = 0) = new RD(dataW, idW, userW)
}
