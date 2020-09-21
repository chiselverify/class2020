import chisel3._

class RegFileVec(addrWidth: Int, dataWidth: Int) extends RegFile(addrWidth, dataWidth) {
    // The register file is represented as a vector of 2^addrwidth registers
    // of which location zero is never accessed
    val rf = Reg(Vec( 1 << addrWidth, UInt(dataWidth.W)))

    // Synchronous reads are the result of storing the read addresses
    // in two registers and using their outputs for addressing
    val addrReg1 = RegNext(io.registerRs1)
    val addrReg2 = RegNext(io.registerRs2)

    //Synchronous write to register file
    when(io.regWrite === 1.U && io.registerRd =/= 0.U){
        rf(io.registerRd) := io.writeData
    }

    // Output data 1
    when(addrReg1 === 0.U){
        io.data1 := 0.U
    } .otherwise {
        io.data1 := rf(addrReg1)
    }
    // Output data 2
    when(addrReg2 === 0.U){
        io.data2 := 0.U
    } .otherwise {
        io.data2 := rf(addrReg2)
    }
}
