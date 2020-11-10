import chisel3._
import chisel3.util
import chiseltest._
import org.scalatest._

class MovingAveragePow2(averageLength: Int, bitCount: Int) extends Module {
    if (!util.isPow2(averageLength) && averageLength != 1) {
        throw new Exception("The number of items to average over must be a power of 2.")
    }
    if (averageLength <= 0) {
        throw new Exception("The number of items to average over must be at least 1.")
    }
    if (bitCount <= 0) {
        throw new Exception("The bit count of each item must be at least 1.")
    }

    val io = IO(new Bundle{
        val din = Input(UInt(bitCount.W))
        val write = Input(Bool())
        val avg = Output(UInt(bitCount.W))
    })
    val values = RegInit(VecInit(Seq.fill(averageLength)(0.U(bitCount.W))))

    val sumBitCount = bitCount + util.log2Ceil(averageLength)
    val sum = RegInit(0.U(sumBitCount.W))

    when (io.write) {
        sum := sum + (io.din - values(averageLength - 1))

        values(0) := io.din 
        for (i <- 0 until averageLength - 1) {
            values(i + 1) := values(i)
        }
    }

    io.avg := sum(sumBitCount - 1, util.log2Ceil(averageLength))
}