# Verification using SystemVerilog and UVM
The intention of the project is to verify and assess the coverage parameters of the designs in the course using SystemVerilg and compare them with those done using Chisel.

## Documentation
The tests will be done using a UVM framework in SystemVerilog. The coverage parameters will be noted and pushed towards ideal values. The values will then be compared with those obtained using Chisel for the same modules.
 ### The following modules will be verfified:
 1. Mux5: -- A Simple 5 input mux
 ` ` ` Verilog
 module Mux5( a , b, c, d, e, sel, y);\n
 	input logic [7 : 0] a;\n
  	input logic [7 : 0] b;\n
  	input logic [7 : 0] c;
  	input logic [7 : 0] d;
  	input logic [7 : 0] e;
  	input logic [2 : 0] sel;
  	output logic [7 : 0] y ;
  	...
 end module: Mux5;
 ` ` `
 2. ALU with an accumulator:
 ` ` ` Verilog
 module AluAccu #(parameter DATA_WIDTH = 8;)(op , din, ena, accu);
 	input logic [2 : 0] op;
  	input logic [DATA_WIDTH - 1 : 0] ;
  	input logic enable;
  	output logic [DATA_WIDTH - 1 : 0] accu;
  	...
 end module: Mux5;
 ` ` `
 3. LIFOQueue:
 ` ` ` Verilog
 module LIFOQueue #(parameter DATA_WIDTH = 8;)(push, din, full,pop,dout, empty);
  	input logic push;
  	input logic [DATA_WIDTH - 1 : 0] din;
  	output logic full;
	input logic pop;
  	output logic [DATA_WIDTH - 1 : 0] dout;
   	output logic empty;
  	...
 end module: LIFOQueue;
 ` ` `
 4. Multiplier:
 ` ` ` Verilog
 module Multiplier #(parameter DATA_WIDTH = 8;)(input1, input2, start, output, done);
  	input logic start;
  	input logic [DATA_WIDTH - 1 : 0] input1;
   	input logic [DATA_WIDTH - 1 : 0] input2;
  	output logic done;
  	output logic [DATA_WIDTH - 1 : 0] output;
  	...
 end module: LIFOQueue;
 ` ` `
 5. BubbleFIFO:
  ` ` ` Verilog
 module BubbleFIFO #(parameter DATA_WIDTH = 8;)(write, din, busy, read, dout, notReady);
  	input logic start;
  	input logic [DATA_WIDTH - 1 : 0] din;
   	output logic busy;
  	input logic read;
  	output logic [DATA_WIDTH - 1 : 0] dout;
  	output logic notReady;
  	...
 end module: LIFOQueue;
 ` ` `
 ## Litterature
SystemVerilog for Hardware Description - Vaibbhav Taraate
