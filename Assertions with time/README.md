# Assertions with Timing
This project sets out to introduce a way to make assertions which are timedependent, when working with testing in chisel.

## Documentation
Assertions are statements which are expected to be true. These statemants usually take logical expressions as arguments.
If an assertion-functions argument is false, the assertion will return an error. Sometimes assertions will also take timingconstraints as
arguments. There are many ways timing can play a role, but a classical example is, that if a certain signal goes high, then there might be another
which has to go high as well within a time limit. If this doesn't happen the assertion will also throw an error. This is the kind of functionality which this project sets out make part of the chiseltesting vocabulary.

## Litterature

"Writing testbenches using SystemVerilog" - Janick Bergeron
"Principles of function verification" - Andreas Meyer
https://www.scalatest.org/user_guide/using_assertions
https://www.chipverify.com/systemverilog/systemverilog-assertions-time-delay
https://verificationguide.com/systemverilog/systemverilog-assertions/
https://docs.gradle.org/current/samples/sample_building_scala_libraries.html



## TO DO
