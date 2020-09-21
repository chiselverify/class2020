import chisel3._

class RegFileVec(addrWidth: Int, dataWidth: Int) extends RegFile(addrWidth, dataWidth) {
    // The register file is represented as a vector of 2^addrwidth registers
    // of which location zero is never accessed
    val rf = Reg(Vec(1 << addrWidth, UInt(dataWidth.W)))

    // Synchronous reads are the result of storing the read addresses
    // in two registers and using their outputs for addressing
    val addrReg1 = RegNext(io.registerRs1)
    val addrReg2 = RegNext(io.registerRs2)

    // Store the data on writeData when regWrite is asserted
    when (io.regWrite && io.registerRd =/= 0.U) {
        rf(io.registerRd) := io.writeData
    }

    // Output data based on the read addresses
    io.data1 := Mux(addrReg1 === 0.U, 0.U, rf(addrReg1))
    io.data2 := Mux(addrReg2 === 0.U, 0.U, rf(addrReg2))
}
