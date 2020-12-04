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
 * @param addrW the width of the address signals in bits
 * @param dataW the width of the data read/write signals in bits
 * @param idW the width of the ID signals in bits, defaults to 0
 * @param userW the width of the user signals in bits, defaults to 0
 */
abstract class Master(val addrW: Int, val dataW: Int, val idW: Int = 0, val userW: Int = 0) extends Module {
  val io = IO(new MasterInterface(addrW, dataW, idW, userW))
}
