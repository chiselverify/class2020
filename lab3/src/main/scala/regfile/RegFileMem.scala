import chisel3._

class RegFileMem(addrWidth: Int, dataWidth: Int) extends RegFile(addrWidth, dataWidth) {
    // Renaming io signals for easier reference
    val rs1 = io.registerRs1
    val rs2 = io.registerRs2
    val rd = io.registerRd

    // The register file is represented as a synchronous read/write memory
    val rf = SyncReadMem(1 << addrWidth, UInt(dataWidth.W))

    // Synchronous reads are the result of storing the output of the memory
    // to a register - flags are used to indicate special cases
    val z1Reg = RegNext(rs1 === 0.U)
    val eq1Reg = RegNext(rs1 === rd && rs1 =/= 0.U)
    val z2Reg = RegNext(rs2 === 0.U)
    val eq2Reg = RegNext(rs2 === rd && rs2 =/= 0.U)
    val wDReg = Reg(UInt(dataWidth.W))

    // Store the data on writeData when regWrite is asserted
    when (io.regWrite && rd =/= 0.U) {
        rf.write(rd, io.writeData)
        wDReg := io.writeData
    }

    // Output data based on the read addresses
    io.data1 := rf.read(rs1)
    when (z1Reg) {
        io.data1 := 0.U
    } .elsewhen (eq1Reg) {
        io.data1 := wDReg
    }
    io.data2 := rf.read(rs2)
    when (z2Reg) {
        io.data2 := 0.U
    } .elsewhen (eq2Reg) {
        io.data2 := wDReg
    }
}