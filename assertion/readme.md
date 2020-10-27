# Assertions with time
This project sets out to introduce a way to make assertions which are timedependent, when working with testing in chisel. This type of assertions check for a condition in a HDL design, which must be terminated within a specific time.

## Documentation
Assertions checks for conditions that should always be true and throws an error message, should the condition be false. This verification involves behaviour over time, as a signal should result in an expected behaviour from the design, or aborted within x clock cycles.
Assertions falls into two categories, immediate assertions and concurrent assertions.

### Immediate assertion
This type of assertion is essentially an if-else statement with an assertion control.

### Concurrent assertion
This type of assertion checks the behaviour of multiple clock cycles. 

## Text material
"Writing Testbenches, functional verification of HDL models", second edition, 2003
by Janick Bergeron
Avaible at [FindIt](https://findit.dtu.dk/en/catalog/2441606068)

"Principles of Verifiable RTL Design", 2000
by Lionel Benning and Harry Foster, Hewlett-Packard Company
Avaible at [FindIt](https://findit.dtu.dk/en/catalog/2441585758)

"Principles of function verification" - Andreas Meyer

https://www.scalatest.org/user_guide/using_assertions

https://verificationguide.com/systemverilog/systemverilog-assertions/ 

https://www.chipverify.com/systemverilog/systemverilog-assertions-time-delay 

https://docs.gradle.org/current/samples/sample_building_scala_libraries.html 

https://www.scala-lang.org/api/current/scala/Predef$.html https://www.systemverilog.io/sva-basics


## TODO
- Study supported assertion in Scala vs other HDL (e.g. SystemVerilog)
- Include race conditions?
- Make simple test example
