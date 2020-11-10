# Assertions with time
This project sets out to introduce a way to make timedependent assertions, when working with testing in chisel. This type of assertions checks for a condition in a HDL design, which must be terminated within a specific time. This verification involves behaviour over time, as a signal should result in an expected behaviour from the design, or aborted within x clock cycles.  
To support this project topic, assertions as used in software and other HDLs are reviewed/compared. Since Scala runs on the JVM, Java is a convenient choice. As for assertions used in HDL, SystemVerilog is included.

## Documentation
Assertions checks for a boolean condition that should always be true and throws an error message, should the condition be false. Assertions already exists in Scala as immediate assertions, which are determined immediately. However, assertions over time, known as concurrent assertions, are currently only supported by mapping immediate assertions on the `Future`. 

### Immediate assertion
This type of assertion is essentially an if-else statement with an assertion control and is the current version of assertion in Scala. As long as the condition passes, the `assert` returns normally, however, should the condition not pass, the test will stop abruptly with an `AssertionError`.

### Concurrent assertion
This type of assertion checks the behaviour of multiple clock cycles. This could for example being a check on the rising edge of the clock, or expecting a behaviour to be true, or eventually be true, within a specific time window.

## Text material
"Writing Testbenches, functional verification of HDL models", second edition, 2003  
by Janick Bergeron  
Available at [FindIt](https://findit.dtu.dk/en/catalog/2441606068)

"Principles of Verifiable RTL Design", 2000  
by Lionel Benning and Harry Foster, Hewlett-Packard Company  
Available at [FindIt](https://findit.dtu.dk/en/catalog/2441585758)

"Principles of function verification" - Andreas Meyer  
Available at [FindIt](https://findit.dtu.dk/en/catalog/2305333384)

https://www.scalatest.org/user_guide/using_assertions  
http://doc.scalatest.org/3.0.1-2.12/org/scalatest/Assertions.html  
https://verificationguide.com/systemverilog/systemverilog-assertions/  
https://www.chipverify.com/systemverilog/systemverilog-assertions-time-delay  
https://docs.gradle.org/current/samples/sample_building_scala_libraries.html  
https://www.scala-lang.org/api/current/scala/Predef$.html https://www.systemverilog.io/sva-basics

## TODO

- Find a way to implement concurrent assertion in Scala
- Figure out what property testing is 
- Include race conditions?
- Make several test example
  - Some example with Master-slave transaction
  - 1965 Thunderbird Rear indicator

## Done
- Study supported assertion in Scala vs other HDL (e.g. SystemVerilog)
  - Research Property keyword from SystemVerilog
- Find a viable solution to count clockcycles
  - preferably be able to access the clocksignal in chisel, like you can do in systemverilog
