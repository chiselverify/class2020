/**
 * Author:  Hans Jakob Damsgaard, hansjakobdamsgaard@gmail.com
 * 
 * Purpose: Implementation of a testing framework for AXI4-compliant devices.
 * 
 * Content: A functional AXI master implemented with ChiselTest.
*/

package axi4

import chisel3._
import chisel3.util._
import chiseltest._
import scala.util.Random

/** An AXI4 functional master
 * 
 * @param dut a slave DUT
 * 
 * @note [[initialize]] method must be called before any transactions are started
 * @note [[close]] method must be called after a test to ensure no Chiseltest thread errors
 */
class AXI4FunctionalMaster[T <: Slave](dut: T) {
  /** DUT information */
  private[this] val idW     = dut.idW
  private[this] val addrW   = dut.addrW
  private[this] val dataW   = dut.dataW

  /** Shortcuts to the channel IO */
  private[this] val aw      = dut.io.aw
  private[this] val dw      = dut.io.dw
  private[this] val wr      = dut.io.wr
  private[this] val ar      = dut.io.ar
  private[this] val dr      = dut.io.dr
  private[this] val resetn  = dut.reset
  private[this] val clk     = dut.clock

  /** Threads and transaction state */
  private[this] var isInit  = false
  private[this] var inFlightWrites = Seq[WriteTransaction]()
  private[this] var inFlightReads = Seq[ReadTransaction]()
  private[this] var readValues = Seq[Seq[BigInt]]()
  private[this] var responses = Seq[Response]()
  private[this] var respT = fork { }
  private[this] var lastWAddrT = fork { }
  private[this] var lastWT = fork { }
  private[this] var lastRAddrT = fork { }
  private[this] var lastRT = fork { }
  private[this] val rng = new Random(42)

  /** Check for in-flight operations
   *
   * @return Boolean
   */
  def hasInflightOps() = inFlightReads.length > 0 || inFlightWrites.length > 0

  /** Watch the response channel
   * 
   * @note never call this method explicitly
   */
  private[this] def respHandler() = {
    println("New response handler")

    /** Indicate that interface is ready and wait for response */
    wr.ready.poke(true.B)
    while (!wr.valid.peek.isLit) {
      clk.step()
    }
    responses = responses :+ (new Response(wr.bits.resp.peek, wr.bits.id.peek.litValue))
    wr.ready.poke(false.B)
  }

  /** Handle the write address channel
   * 
   * @note never call this method explicitly
   */
  private[this] def writeAddrHandler(addr: BigInt, id: BigInt, len: Int, size: Int, burst: UInt, lock: Bool, cache: UInt, prot: UInt, qos: UInt, region: UInt): Unit = {
    println(s"New write address handler for addr = $addr")

    /** Write address to slave */
    aw.valid.poke(true.B)
    aw.bits.id.poke(id.U)
    aw.bits.addr.poke(addr.U)
    aw.bits.len.poke(len.U)
    aw.bits.size.poke(size.U)
    aw.bits.burst.poke(burst)
    aw.bits.lock.poke(lock)
    aw.bits.cache.poke(cache)
    aw.bits.prot.poke(prot)
    aw.bits.qos.poke(qos)
    aw.bits.region.poke(region)
    do {
      clk.step()
    } while (!aw.ready.peek.isLit)
    clk.step()
    aw.valid.poke(false.B)

    /** Fork new write handler */
    lastWT = fork {
      lastWT.join()
      writeHandler()
    }
  }

  /** Handle the data write channel
   * 
   * @note never call this method explicitly
   */
  private[this] def writeHandler(): Unit = {
    println("New write handler")

    /** Write data to slave */
    while (!inFlightWrites.head.complete) {
      dw.valid.poke(true.B)
      val (data, strb, last) = inFlightWrites.head.next
      dw.bits.data.poke(data)
      dw.bits.strb.poke(strb)
      dw.bits.last.poke(last)
      do {
        clk.step()
      } while (!dw.ready.peek.isLit)
      clk.step()
    }
    dw.valid.poke(false.B)
    inFlightWrites = inFlightWrites.tail

    /** Fork new response handler thread */
    respT = fork {
      respT.join()
      respHandler()
    }
  }

  /** Handle the read address channel
   * 
   * @note never call this method explicitly
   */
  private[this] def readAddrHandler(addr: BigInt, id: BigInt, len: Int, size: Int, burst: UInt, lock: Bool, cache: UInt, prot: UInt, qos: UInt, region: UInt): Unit = {
    println(s"New read address handler for addr = $addr")

    /** Write address to slave */
    ar.valid.poke(true.B)
    ar.bits.id.poke(id.U)
    ar.bits.addr.poke(addr.U)
    ar.bits.len.poke(len.U)
    ar.bits.size.poke(size.U)
    ar.bits.burst.poke(burst)
    ar.bits.lock.poke(lock)
    ar.bits.cache.poke(cache)
    ar.bits.prot.poke(prot)
    ar.bits.qos.poke(qos)
    ar.bits.region.poke(region)
    do {
      clk.step()
    } while (!ar.ready.peek.isLit)
    clk.step()
    ar.valid.poke(false.B)

    /** Fork new read handler */
    lastRT = fork {
      lastRT.join()
      readHandler()
    }
  }

  /** Handle the data read channel
   * 
   * @note never call this method explicitly
   */
  private[this] def readHandler(): Unit = {
    while (!inFlightReads.head.complete) {
      dr.ready.poke(false.B)
      if (dr.valid.peek.isLit) {
        // Accept read data
        dr.ready.poke(true.B)
        val resp = dr.bits.resp.peek
        if (resp == ResponseEncodings.Decerr || resp == ResponseEncodings.Slverr)
          println(s"[Error] reading failed with response $resp")
        inFlightReads.head.add(dr.bits.data.peek.litValue)
      }
      clk.step()
    }
    readValues = readValues :+ inFlightReads.head.data
    inFlightReads = inFlightReads.tail
  }

  /** Initialize the interface (reset) 
   * 
   * @note MUST be called first to ensure correct operation
   */
  def initialize() = {
    /** Address write */
    aw.bits.id.poke(0.U)
    aw.bits.addr.poke(0.U)
    aw.bits.len.poke(0.U)
    aw.bits.size.poke(0.U)
    aw.bits.burst.poke(BurstEncodings.Fixed)
    aw.bits.lock.poke(LockEncodings.NormalAccess)
    aw.bits.cache.poke(MemoryEncodings.DeviceNonbuf)
    aw.bits.prot.poke(ProtectionEncodings.DataNsecUpriv)
    aw.bits.qos.poke(0.U)
    aw.bits.region.poke(0.U)
    aw.valid.poke(false.B)
    
    /** Data write */
    dw.bits.data.poke(0.U)
    dw.bits.strb.poke(0.U)
    dw.bits.last.poke(false.B)
    dw.valid.poke(false.B)

    /** Write response */
    wr.ready.poke(false.B)

    /** Address read */
    ar.bits.id.poke(0.U)
    ar.bits.addr.poke(0.U)
    ar.bits.len.poke(0.U)
    ar.bits.size.poke(0.U)
    ar.bits.burst.poke(BurstEncodings.Fixed)
    ar.bits.lock.poke(LockEncodings.NormalAccess)
    ar.bits.cache.poke(MemoryEncodings.DeviceNonbuf)
    ar.bits.prot.poke(ProtectionEncodings.DataNsecUpriv)
    ar.bits.qos.poke(0.U)
    ar.bits.region.poke(0.U)
    ar.valid.poke(false.B)

    /** Data read */
    dr.ready.poke(false.B)

    /** Reset slave device controller */
    resetn.poke(false.B)
    clk.step()
    resetn.poke(true.B)

    /** Set initialized flag */
    isInit = true
  }

  /** Close the interface (shutdown)
   * 
   * @note MUST be called after a test to ensure no Chiseltest thread errors
   */
  def close() = {
    /** Join all threads */
    respT.join()
    lastWAddrT.join()
    lastWT.join()
    lastRAddrT.join()
    lastRT.join()

    /** Reset initialized flag */
    isInit = false
  }

  /** Start a write transaction to the given address
   * 
   * @param addr start write address
   * @param data optional list of data to write, defaults to random data
   * @param id optional id, defaults to ID 0
   * @param len optional burst length, defaults to 0 (i.e., 1 beat)
   * @param size optional beat size, defaults to 1 byte
   * @param burst optional burst type, defaults to INCR
   * @param lock optional lock type, defaults to normal access
   * @param cache optional memory attribute signal, defaults to device non-bufferable
   * @param prot optional protection type, defaults to non-secure unprivileged data access
   * @param qos optional QoS, defaults to 0
   * @param region optional region, defaults to 0
   * 
   * @note [[addr]] must fit within the slave DUT's write address width
   * @note entries in [[data]] must fit within the slave DUT's write data width, and the list can have at most [[len]] entries
   * @note [[id]] must fit within DUT's ID width, likewise [[size]] cannot be greater than the DUT's write data width
   * @note [[burst]], [[lock]], [[cache]], and [[prot]] should be a set of those defined in Defs.scala
   */
  def createWriteTrx(
      addr: BigInt, 
      data: Seq[BigInt] = Seq[BigInt](), 
      id: BigInt = 0, 
      len: Int = 0, 
      size: Int = 0, 
      burst: UInt = BurstEncodings.Incr, 
      lock: Bool = LockEncodings.NormalAccess, 
      cache: UInt = MemoryEncodings.DeviceNonbuf, 
      prot: UInt = ProtectionEncodings.DataNsecUpriv, 
      qos: UInt = 0.U, 
      region: UInt = 0.U) = {
    require(isInit, "interface must be initialized before starting write transactions")
    require(log2Up(addr) <= addrW, s"address must fit within DUT's write address width (got $addr)")
    require(log2Up(id) <= idW, s"ID must fit within DUT's ID width (got $id)")

    /** [[len]] and [[size]] checks
     * - [[size]] must be less than or equal to the write data width
     * - [[len]] must be <= 15 for FIXED and WRAP transactions, only INCR can go beyond
     * - Bursts cannot cross 4KB boundaries
     */
    val startAddr = addr
    val numBytes  = 1 << size
    val burstLen  = len + 1
    val alignedAddr = (startAddr / numBytes) * numBytes
    val wrapBoundary = (startAddr / (numBytes * burstLen)) * (numBytes * burstLen)
    require(numBytes <= dataW, s"size must be less than or equal to the write data width")
    burst match {
      case BurstEncodings.Fixed =>
        require(burstLen <= 16, s"len for FIXED transactions must be less than or equal to 15 (got $len)")
        require(((startAddr + numBytes) >> 12) == (startAddr >> 12), "burst cannot cross 4KB boundary")
      case BurstEncodings.Incr =>
        require(burstLen <= 256, s"len for INCR transactions must be less than or equal to 255 (got $len)")
        require(((startAddr + numBytes * burstLen) >> 12) == (startAddr >> 12), "burst cannot cross 4KB boundary")
      case BurstEncodings.Wrap =>
        require(burstLen <= 16, s"len for WRAP transactions must be less than or equal to 15 (got $len)")
        require((startAddr >> 12) == (wrapBoundary >> 12), "burst cannot cross 4KB boundary")
      case _ => throw new IllegalArgumentException("invalid burst type entered")
    }

    /** Select data */
    val tdata = if (data != Nil) {
      require(data.length == burstLen, "given data length should match burst length")
      data
    } else
      Seq.fill(burstLen) { BigInt(numBytes, rng) }

    /** Create and queue new write transaction */
    inFlightWrites = inFlightWrites :+ (new WriteTransaction(addr, data, dataW, size, burst))

    /** Write address to slave */
    lastWAddrT = fork {
      lastWAddrT.join()
      writeAddrHandler(addr, id, len, size, burst, lock, cache, prot, qos, region)
    }
  }

  /** Start a write transaction to the given address
   * 
   * @param addr start read address
   * @param id optional id, defaults to ID 0
   * @param len optional burst length, defaults to 0 (i.e., 1 beat)
   * @param size optional beat size, defaults to 1 byte
   * @param burst optional burst type, defaults to INCR
   * @param lock optional lock type, defaults to normal access
   * @param cache optional memory attribute signal, defaults to device non-bufferable
   * @param prot optional protection type, defaults to non-secure unprivileged data access
   * @param qos optional QoS, defaults to 0
   * @param region optional region, defaults to 0
   * 
   * @note [[addr]] must fit within the slave DUT's write address width
   * @note [[id]] must fit within DUT's ID width, likewise [[size]] cannot be greater than the DUT's write data width
   * @note [[burst]], [[lock]], [[cache]], and [[prot]] should be a set of those defined in Defs.scala
   */
  def createReadTrx(
      addr: BigInt, 
      id: BigInt = 0, 
      len: Int = 0, 
      size: Int = 0, 
      burst: UInt = BurstEncodings.Incr, 
      lock: Bool = LockEncodings.NormalAccess, 
      cache: UInt = MemoryEncodings.DeviceNonbuf, 
      prot: UInt = ProtectionEncodings.DataNsecUpriv, 
      qos: UInt = 0.U, 
      region: UInt = 0.U) = {
    require(isInit, "interface must be initialized before starting write transactions")
    require(log2Up(addr) <= addrW, s"address must fit within DUT's write address width (got $addr)")
    require(log2Up(id) <= idW, s"ID must fit within DUT's ID width (got $id)")

    /** [[len]] and [[size]] checks
     * - [[size]] must be less than or equal to the write data width
     * - [[len]] must be <= 15 for FIXED and WRAP transactions, only INCR can go beyond
     * - Bursts cannot cross 4KB boundaries
     */
    val startAddr = addr
    val numBytes  = 1 << size
    val burstLen  = len + 1
    val alignedAddr = (startAddr / numBytes) * numBytes
    val wrapBoundary = (startAddr / (numBytes * burstLen)) * (numBytes * burstLen)
    require(numBytes <= dataW, s"size must be less than or equal to the write data width")
    burst match {
      case BurstEncodings.Fixed =>
        require(burstLen <= 16, s"len for FIXED transactions must be less than or equal to 15 (got $len)")
        require(((startAddr + numBytes) >> 12) == (startAddr >> 12), "burst cannot cross 4KB boundary")
      case BurstEncodings.Incr =>
        require(burstLen <= 256, s"len for INCR transactions must be less than or equal to 255 (got $len)")
        require(((startAddr + numBytes * burstLen) >> 12) == (startAddr >> 12), "burst cannot cross 4KB boundary")
      case BurstEncodings.Wrap =>
        require(burstLen <= 16, s"len for WRAP transactions must be less than or equal to 15 (got $len)")
        require((startAddr >> 12) == (wrapBoundary >> 12), "burst cannot cross 4KB boundary")
      case _ => throw new IllegalArgumentException("invalid burst type entered")
    }

    /** Create and queue new read transaction */
    inFlightReads = inFlightReads :+ (new ReadTransaction(len))

    /** Write address to slave */
    lastRAddrT = fork {
      lastRAddrT.join()
      readAddrHandler(addr, id, len, size, burst, lock, cache, prot, qos, region)
    }
  }

  /** Check for write response 
   * 
   * @note write responses are continuously stored in an internal queue by a second thread
   * @note reading is destructive; i.e., the response being checked is removed from the queue
  */
  def checkResponse() = {
    require(isInit, "interface must be initialized before starting write transactions")
    responses match {
      case r :: tail => 
        responses = tail
        Some(r)
      case _ => None
    }
  }

  /** Check for read data
   * 
   * @note read values are continuously stored in an internal queue by a second thread spawned when creating a new read transaction
   * @note reading is destructive; i.e., the data being returned is removed from the queue
   */
  def checkReadData() = {
    require(isInit, "interface must be initialized before starting write transactions")
    readValues match {
      case v :: tail =>
        readValues = tail
        Some(v)
      case _ => None
    }
  }
}
