/**
 * Author:  Hans Jakob Damsgaard, hansjakobdamsgaard@gmail.com
 * 
 * Purpose: Implementation of a testing framework for AXI4-compliant devices.
 * 
 * Content: An empty AXI master with relevant interface.
*/

package axi4

import chisel3._

/** AXI4 master
 * 
 * An empty class representing an AXI master
 *
 * @param idW the width of the ID signals in bits
 * @param addrW the width of the address signals in bits
 * @param dataW the width of the data read/write signals in bits
 */
abstract class Master(idW: Int, addrW: Int, dataW: Int) extends Module {
  val io = IO(new AXI4Master(idW, addrW, dataW))
}

/** AXI4 master with user signals
 * 
 * An empty class representing an AXI master with user signals
 *
 * @param idW the width of the ID signals in bits
 * @param addrW the width of the address signals in bits
 * @param dataW the width of the data read/write signals in bits
 */
abstract class MasterUser(idW: Int, addrW: Int, dataW: Int) extends Master(idW, addrW, dataW) {
  override val io = IO(new AXI4MasterUser(idW, addrW, dataW))
}
