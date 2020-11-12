/**
 * Author:  Hans Jakob Damsgaard, hansjakobdamsgaard@gmail.com
 * 
 * Purpose: Implementation of a testing framework for AXI4-compliant devices.
 * 
 * Content: An empty AXI slave with relevant interface.
*/

package axi4

import chisel3._

/** AXI4 slave
 * 
 * An empty class representing an AXI slave
 *
 * @param idW the width of the ID signals in bits
 * @param addrW the width of the address signals in bits
 * @param dataW the width of the data read/write signals in bits
 */
abstract class Slave(idW: Int, addrW: Int, dataW: Int) extends Module {
  val io = IO(new AXI4Slave(idW, addrW, dataW))
}

/** AXI4 slave with user signals
 * 
 * An empty class representing an AXI slave with user signals
 *
 * @param idW the width of the ID signals in bits
 * @param addrW the width of the address signals in bits
 * @param dataW the width of the data read/write signals in bits
 */
abstract class SlaveUser(idW: Int, addrW: Int, dataW: Int) extends Slave(idW, addrW, dataW) {
  override val io = IO(new AXI4SlaveUser(idW, addrW, dataW))
}
