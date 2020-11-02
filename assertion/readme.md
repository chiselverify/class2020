# Assertions with time
This project focuses on verification with assertions with timing. Assertions check for a condition in a HDL design, which must be terminated within a specfic time.

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

https://www.scalatest.org/user_guide/using_assertions
https://verificationguide.com/systemverilog/systemverilog-assertions/

## TODO
- Study supported assertion in Scala vs other HDL (e.g. SystemVerilog)
- Include race conditions?
- Make simple test example