import org.scalatest._
import assertionTiming._
import chisel3._
import chiseltest._

class concurrentAssertionTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The mainClass"

// assertAlways
  it should "test assertAlways" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        assertAlways(dut)
      }
    }
  }

  /*it should "test that assertAlways fails when poking false" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        val t = assertAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
      
        dut.clock.step(10)
        dut.io.s.poke(false.B)
        dut.clock.step(1)
      }
    }
  }

  it should "test time depency of clock cycles" in {
    test(new mainClass()) {
      dut => {
        
        // Test of assertAlways
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        assertAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
      
        dut.clock.step(21)
        dut.io.s.poke(false.B)
        dut.clock.step(1)

        // Test of assertNever
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        assertNever(dut, () => dut.io.c.peek.litValue == 4, "Error", 40)
      
        dut.clock.step(41)
        dut.io.s.poke(true.B)
        dut.clock.step(1)
      }
    }
  }

  it should "test time depency of clock cycles and what happens, when you change input on clock" in {
    test(new mainClass()) {
      dut => {
        
        // Test of assertAlways
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        assertAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
      
        dut.clock.step(20)
        dut.io.s.poke(false.B)
        dut.clock.step(1)

        // Test of assertNever
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        assertNever(dut, () => dut.io.c.peek.litValue == 4, "Error", 40)
      
        dut.clock.step(40)
        dut.io.s.poke(true.B)
        dut.clock.step(1)
      }
    }
  }

// assertNever
  it should "test assertNever" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        assertNever(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
      }
    }
  }

  it should "test assertNever fails" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertNever(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
      
        dut.clock.step(15)
        dut.io.s.poke(true.B)
        dut.clock.step(1)
      }
    }
  }

  //assertEventually
  it should "test assertEventually passes, once true regardless of what happens next" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertEventually(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)

        dut.clock.step(5)
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        dut.io.s.poke(false.B)
        t.join
      }
    }
  }*/
  
  it should "test assertEventually passes when truecondition is assertet on last possible cycle" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertEventually(dut, () => dut.io.c.peek.litValue == 4, 5, "Error")

        dut.clock.step(4)
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        dut.io.s.poke(false.B)
        t.join
      }
    }
  }

  /*it should "test assertEventually fails when exceeding clock cycles" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertEventually(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)

        t.join
      }
    }
  }*/

  it should "test if the dut steps correctly" in {
    test(new dummyDUT()) {
      dut => {
        
        dut.io.a.poke(false.B)
        dut.io.b.poke(false.B)
        dut.clock.step(1)
        val c = assertEventually(dut, () => dut.io.c.peek.litValue == 4, 7, "Error1")
        val d = assertEventually(dut, () => dut.io.d.peek.litValue == 4, 3, "Error2")
        dut.clock.step(2)
        dut.io.b.poke(true.B)
        dut.clock.step(4)
        dut.io.a.poke(true.B)
        c.join
        d.join
      }
    }
  }

  it should "test if signals are ignored after becoming true" in {
    test(new dummyDUT()) {
      dut => {
        
        dut.io.a.poke(false.B)
        dut.io.b.poke(false.B)
        dut.clock.step(1)
        val c = assertEventually(dut, () => dut.io.c.peek.litValue == 4, 7, "Error1")
        val d = assertEventually(dut, () => dut.io.d.peek.litValue == 4, 3, "Error2")
        dut.clock.step(1)
        dut.io.b.poke(true.B)
        dut.clock.step(1)
        dut.io.b.poke(false.B)
        dut.clock.step(3)
        dut.io.a.poke(true.B)
        dut.clock.step(1)
        dut.io.a.poke(false.B)
        c.join
        d.join
      }
    }
  }

  it should "test if signals are ignored after cycle window" in {
    test(new dummyDUT()) {
      dut => {
        
        dut.io.a.poke(false.B)
        dut.io.b.poke(false.B)
        dut.clock.step(1)
        val c = assertEventually(dut, () => dut.io.c.peek.litValue == 4, 7, "Error1")
        val d = assertEventually(dut, () => dut.io.d.peek.litValue == 4, 3, "Error2")
        dut.clock.step(2)
        dut.io.b.poke(true.B)
        dut.clock.step(4)
        dut.io.a.poke(true.B)
        dut.clock.step(1)
        dut.io.a.poke(false.B)
        c.join
        d.join
      }
    }
  }

  /*// assertEventuallyAlways
  it should "test assertEventuallyAlways pass" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertEventuallyAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)

        dut.clock.step(1)
        dut.io.s.poke(true.B)

        t.join
      }
    }
  }

  it should "fail, if cond does not hold true once true" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertEventuallyAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)

        dut.clock.step(5)
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        dut.io.s.poke(false.B)

        t.join
      }
    }
  }*/
}