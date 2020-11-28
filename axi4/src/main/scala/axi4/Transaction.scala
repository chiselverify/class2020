/**
 * Author:  Hans Jakob Damsgaard, hansjakobdamsgaard@gmail.com
 * 
 * Purpose: Implementation of a testing framework for AXI4-compliant devices.
 * 
 * Content: Transaction classes for functional masters and slaves.
*/

package axi4

import chisel3._

/** Transaction superclass */
trait Transaction {
  def complete: Boolean
}

/** Write transaction
 *
 * @param addr start write address
 * @param data list of data to write
 * @param dataW slave's data bus width
 * @param id optional id, defaults to ID 0
 * @param len optional burst length, defaults to 0 (i.e., 1 beat)
 * @param size optional beat size, defaults to 1 byte
 * @param burst optional burst type, defaults to FIXED
 * @param lock optional lock type, defaults to normal access
 * @param cache optional memory attribute signal, defaults to device non-bufferable
 * @param prot optional protection type, defaults to non-secure unprivileged data access
 * @param qos optional QoS, defaults to 0
 * @param region optional region, defaults to 0
 */
class WriteTransaction(
  val addr: BigInt, 
  val data: Seq[BigInt], 
  dataW: Int,
  val id: BigInt = 0, 
  val len: Int = 0, 
  val size: Int = 0, 
  val burst: UInt = BurstEncodings.Fixed, 
  val lock: Bool = LockEncodings.NormalAccess, 
  val cache: UInt = MemoryEncodings.DeviceNonbuf, 
  val prot: UInt = ProtectionEncodings.DataNsecUpriv, 
  val qos: UInt = 0.U, 
  val region: UInt = 0.U) extends Transaction {
  private[this] val numBytes = 1 << size
  private[this] val dtsize = numBytes * data.length
  private[this] val lowerBoundary = (addr / dtsize) * dtsize
  private[this] val upperBoundary = lowerBoundary + dtsize
  private[this] val alignedAddress = ((addr / numBytes) * numBytes)
  private[this] var aligned = addr == alignedAddress
  private[this] var address = addr
  private[this] var count = 0

  private[this] var _addrSent = false
  private[this] var _dataSent = false

  /** Getter and setter for [[addrSent]] */
  def addrSent = _addrSent
  def addrSent_=(newValue: Boolean): Unit = _addrSent = newValue

  /** Getter and setter for [[dataSent]] */
  def dataSent = _dataSent
  def dataSent_=(newValue: Boolean): Unit = _dataSent = newValue

  /** Get next (data, strb, last) tuple
   * 
   * @return (data, strb, last) tuple
   * 
   * @note has side effect on internal index count
   */
  def next() = {
    /** Strobe calculation */
    val offset = (address / dataW) * dataW
    val lowerByteLane = address - offset
    val upperByteLane = if (aligned) lowerByteLane + numBytes-1 else alignedAddress + numBytes-1 - offset
    def within(x: Int) = x >= 0 && x <= (upperByteLane - lowerByteLane)
    val strb = ("b"+(0 until (dataW/8)).foldRight("") { (elem, acc) => if (within(elem)) acc + "1" else acc + "0" }).asUInt

    /** Update address */
    if (burst != BurstEncodings.Fixed) {
      if (aligned) {
        address += numBytes
        if (burst == BurstEncodings.Wrap) {
          if (address >= upperBoundary) {
            address = lowerBoundary
          }
        }
      } else {
        address += numBytes
        aligned = true
      }
    }
    count += 1

    /** Return data to write */
    (data(count-1).U, strb, complete.B)
  }
  def complete = data.length == count
}

/** Read transaction 
 * 
 * @param addr start read address
 * @param id optional id, defaults to ID 0
 * @param len optional burst length, defaults to 0 (i.e., 1 beat)
 * @param size optional beat size, defaults to 1 byte
 * @param burst optional burst type, defaults to FIXED
 * @param lock optional lock type, defaults to normal access
 * @param cache optional memory attribute signal, defaults to device non-bufferable
 * @param prot optional protection type, defaults to non-secure unprivileged data access
 * @param qos optional QoS, defaults to 0
 * @param region optional region, defaults to 0
 */
class ReadTransaction(
  val addr: BigInt, 
  val id: BigInt = 0, 
  val len: Int = 0, 
  val size: Int = 0, 
  val burst: UInt = BurstEncodings.Fixed, 
  val lock: Bool = LockEncodings.NormalAccess, 
  val cache: UInt = MemoryEncodings.DeviceNonbuf, 
  val prot: UInt = ProtectionEncodings.DataNsecUpriv, 
  val qos: UInt = 0.U, 
  val region: UInt = 0.U) extends Transaction {
  var data = Seq[BigInt]()

  private[this] var _addrSent = false

  /** Getter and setter for [[addrSent]] */
  def addrSent = _addrSent
  def addrSent_=(newValue: Boolean): Unit = _addrSent = newValue

  /** Add element to data sequence
   *
   * @param v value to add
   * 
   * @note has side effect on internal data sequence
   */
  def add(v: BigInt) = {
    data = data :+ v
  }
  def complete = data.length == (len + 1)
}

/** Transaction response
 *
 * @param resp transaction response
 * @param id optional id
 */
case class Response(val resp: UInt, val id: BigInt = 0)
