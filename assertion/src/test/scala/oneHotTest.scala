import org.scalatest._
import assertionTiming._
import chisel3._
import chiseltest._

class oneHotTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "assertOneHot"

  it should "test count of bitset in binary" in {
    test(new mainOneHot()) {
      dut => {
        dut.io.s.poke(true.B)
        assertOneHot(100.U, "Error", 10)
        dut.io.c.expect(1.U)
      }
    }
  }
}