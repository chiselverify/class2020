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
 * @param dataW DUT's data width
 * @param size optional beat size, defaults to 1 byte
 * @param burst optional burst type, defaults to INCR
 */
class WriteTransaction(addr: BigInt, data: Seq[BigInt], dataW: Int, size: Int = 0, burst: UInt = BurstEncodings.Incr) extends Transaction {
  private[this] val numBytes = 1 << size
  private[this] val dtsize = numBytes * data.length
  private[this] val lowerBoundary = (addr / dtsize) * dtsize
  private[this] val upperBoundary = lowerBoundary + dtsize
  private[this] val alignedAddress = ((addr / numBytes) * numBytes)
  private[this] var aligned = addr == alignedAddress
  private[this] var address = addr
  private[this] var count = 0

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
 * @param len burst length
 */
class ReadTransaction(len: Int) extends Transaction {
  var data = Seq[BigInt]()

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
