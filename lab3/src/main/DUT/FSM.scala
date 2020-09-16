package DUT
class FSM extends Module{
  val io = IO new Bundle{
    val reset = Input(Bool)
    val output = Output(UInt(32.W))
    val curr_state = Input(UInt(2.W))
  }

}