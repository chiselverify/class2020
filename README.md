# Verification of Digital Designs


This is the repository for the 2020 special course on Verification of Digital Designs at DTU.

It will contain teaching material, such as slides, lab exercises, student presentations, and could also host student projects.

## Books

_Principles of Functional Verification_ by A. Meyer - a good, easy-to-read introduction to functional verification without any code examples. Considers a lot of verification-related topics and practices at a high level with illustrating figures, but with few practical examples and only little actual "_theory_". Available from [Findit](https://findit.dtu.dk/en/catalog/2305333384).

_Writing Testbenches - Functional Verification of HDL Models_ by J. Bergeron - a more in-depth overview of functional verification with examples in both Verilog and VHDL throughout the book. This book is more university textbook-like than the above, and despite the difference in language from the course, it still represents a good source. Available from [Findit](https://findit.dtu.dk/en/catalog/2441585752).

_Fundamentals of Digital Logic with VHDL Desgin_ by S. Brown, Z. Vranesic. Chapter 11 offers techniques for making effective test sets using stuck-at models. These techniques includes identifying input combinations which will test the entire design using logic tables and sensitized paths. The chapter also makes remarks of making designs for testiability and BISTs. Avaible from [Academia](https://www.academia.edu/6406951/Fundamentals_of_Digital_Logic_with_VHDL_Design).

_ASIC/SoC Functional Design Verification_ by A. B. Mehta. The book serves as a guide to technologies and methodologies of functional design verification, including UVM. Available from [Findit](https://findit.dtu.dk/en/catalog/2374482012).

_How to Choose verification methodology_ by R. Purisai. This article starts out by comparing Verification and validation(test). In part 1.3 the author describes various types of testing strategies, and talks about trends within the field of testing. In part 1.4 the author offers some ideas in terms of choosing the right verification methodology.

_SystemVerilog for Verification: A guide to learning the testbench language features - 2008 version_ by Chris Spear
https://link-springer-com.proxy.findit.dtu.dk/book/10.1007%2F978-0-387-76530-3

Slides for Test Driven Development (TDD) presentation - https://docs.google.com/presentation/d/1VsoGNBGK07tDa9BOLWquPULzBzX6JDiC7RQ5Qk_W2d4/edit?usp=sharing

Slides for testing in software - https://docs.google.com/presentation/d/1vtVaw38XyhBOi8_PNTjz58XGI_g-Bg_CBW1UXpour94/edit?usp=sharing

## Projects

### Verification of AMBA AXI Interfaced Components
- by Hans Jakob Damsgaard
- Implementation of interface and transaction specifications for the AMBA AXI4 protocol in Scala to make testing of compliant Chisel components easier.
- found in `./axi4/`

### Assertions with time
- by Niels Frederik Frandsen and Victor Alexander Hansen
- Timedependent assertions, when working with testing in chisel. This type of assertions checks for a condition in a HDL design, which must be terminated within a specific time. This verification involves behaviour over time, as a signal should result in an expected behaviour from the design, or aborted within x clock cycles.
- found in `./assertion/`
### Verification using SystemVerilog and UVM
- by Kishan Suchet Palani
- Verify and note the test coverage parameters obtained from testing the designs(Mux, ALU with an accumulator, LIFO Queue, BubbleFIFO) in SystemVerilog following a UVM framework. 
- Develop test further to get maximum test coverage.
- found in `./VerificationUsingSystemVerilog/`

### FIRRTL Coverage
- by Andreas Gramstrup Correia
- This project wil make it possible to get accurate test coverage data from chisel code by introducing a way to measure the coverage of FIRRTL code.
- found in `./FIRRTLCoverage/`
