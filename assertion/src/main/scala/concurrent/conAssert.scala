package concurrent

object conAssert {
  /** Checks for a condition to be valid in the circuit at all times, or within
    * the specified amount of clock cycles. If the condition evaluates to false,
    * the circuit simulation stops with an error.
    *
    * @param cond condition, assertion fires (simulation fails) when false
    * @param message optional format string to print when assertion fires
    * @param cycles optional amount of clock cycles for which the assertion is 
    * checked, instead of an immediate assertion
    *
    * This object is part of the special course "Verification of Digital Designs"
    * on DTU, autumn semester 2020.
    *
    * @author Victor Alexander Hansen, s194027@student.dtu.dk
    * @author Niels Frederik Flemming Holm Frandsen, s194053@student.dtu.dk
    */
  def apply(cond: Any, message: String, cycles: Int) {

  // The use of fork{} enables the assertion to run concurrently
  //fork {

    for (i <- 0 until cycles) {
      // Den helt generelle assertion
      if (cond == true){
        
      } else {
        System.out.println(message) //Printer uanset
        throw new Exception("This is not a runTime haha")
      }
    }
  }
}
  //}

// Notes for possible future investigation:
// Chain assertions
// Decoupled: ready-valid handshake




  /*def testfunktion () = {
  fork {
    for (i <- 0 until cycles) {}
    // Need some sort of break
  }
  for (i <- 0 until cycles) {
  // Den helt generelle assertion
    when (cond){
      io.testp := true.B
    } .otherwise {
      System.out.println(message) //Printer uanset
      io.testp := false.B
    }
  }*/






object Main extends App {}

/* Legacy, this stuff works
class conAssert(cond: Bool, cycles: Int, message: String) extends Module{
  val io = IO(new Bundle {
    val sigA = Input(Bool())
    val testp = Output(Bool())
  })
  io.testp := false.B
  // The use of fork{} enables the assertion to run concurrently
  //fork {
    when (io.sigA) {
      for (i <- 0 until cycles) {
        // Den helt generelle assertion
        when (cond){
          io.testp := true.B
        } .otherwise {
          println(message) //Printer uanset
          io.testp := false.B
        }
      }
    }
  //}
}
*/
//kage