# Lab 1: A Simple Tester

In this lab you shall write test code for a 5:1 multiplexer. The interface
of the multiplexer is:

```scala
class Mux5 extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(8.W))
    val b = Input(UInt(8.W))
    val c = Input(UInt(8.W))
    val d = Input(UInt(8.W))
    val e = Input(UInt(8.W))
    val sel = Input(UInt(3.W))
    val y = Output(UInt(8.W))
  })
  .....
}
```

Write the test code without exploring the Mux5 implementation.

## Background Reading

 * Chapter 3 of
*[Digital Design with Chisel](http://www.imm.dtu.dk/~masca/chisel-book.html)*

## Use IntelliJ

With IntelliJ import the lab2 project as follows:

 * Start IntelliJ
 * Click *Import Project*, or on a running IntelliJ: *File - New -
Project from Existing Source...*
 * Navigate to ```.../class2020/lab1``` and select the file ```build.sbt```, press *Open*
 * Make sure to select JDK 1.8 (not Java 11!)
 * Press OK on the next dialog box

