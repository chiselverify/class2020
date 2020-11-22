# Test coverage at FIRRTL level

This project wil make it possible to get accurate test coverage data from chisel code by introducing a way to measure the coverage of FIRRTL code. Coverage is understood as the hardware paths that reaches a register or similar state preserving elements. A mux or similar can block paths. Chisel will have to annotate chisel code in a way that makes it possible to trace specific FIRRTL operations back to the scala source that made them. FIRRTL needs to support loading these new annotations and preserve them through its various optimizations and transformations that it does. Treade will need to support these new annotations and record the annotations as the attached hardware is used. 

## Sources
This project requires changes to FIRRTL, Treadle and Chisel3. As those are all major projects, the source will not be available in this folder. Instead One can Check out my forks to view the changes i've made.

https://github.com/TheAIBot/chisel3

https://github.com/TheAIBot/treadle

https://github.com/TheAIBot/firrtl

## TODO
* Tag FIRRTL code with chisel source code location annotations
* FIRRTL annotation support
* Treadle annotation support
* Implement a way to understand the outputted coverage report

## Text material

https://www.researchgate.net/publication/266883269_Coverage_Analysis_Techniques_for_HDL_Design_Validation
https://devsaurus.github.io/ghdl_gcov/ghdl_gcov.html

https://github.com/freechipsproject/firrtl/blob/master/spec/spec.pdf
