/**
 * Author:  Hans Jakob Damsgaard, hansjakobdamsgaard@gmail.com
 * 
 * Purpose: Implementation of a testing framework for AXI4-compliant devices.
 * 
 * Content: Interface definitions for AXI4.
*/

package axi4

import chisel3._
import chisel3.util.Decoupled

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
  val aw = Decoupled(Output(WA(addrW, idW, userW))) //Decoupled(new WA(addrW, idW, userW))
  val dw = Decoupled(Output(WD(dataW, userW))) //Decoupled(new WD(dataW, userW))
  val wr = Flipped(Decoupled(Output(WR(idW, userW)))) //Flipped(Decoupled(Flipped(new WR(idW, userW))))
  val ar = Decoupled(RA(addrW, idW, userW)) //Decoupled(new RA(addrW, idW, userW))
  val dr = Flipped(Decoupled(Output(RD(dataW, idW, userW)))) //Flipped(Decoupled(Flipped(new RD(dataW, idW, userW))))
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
  val aw = Flipped(Decoupled(Output(WA(addrW, idW, userW)))) //Flipped(Decoupled(new WA(addrW, idW, userW)))
  val dw = Flipped(Decoupled(Output(WD(dataW, userW)))) //Flipped(Decoupled(new WD(dataW, userW)))
  val wr = Decoupled(Output(WR(idW, userW))) //Flipped(Flipped(Decoupled(Flipped(new WR(idW, userW)))))
  val ar = Flipped(Decoupled(Output(RA(addrW, idW, userW)))) //Flipped(Decoupled(new RA(addrW, idW, userW)))
  val dr = Decoupled(Output(RD(dataW, idW, userW))) //Flipped(Flipped(Decoupled(Flipped(new RD(dataW, idW, userW)))))
}
