# Verification of AMBA AXI Interfaced Components
This project focuses on implementation of AXI4 master interface definitions and a small framework providing support for all of the defined transactions from the AXI master's point of view in the protocol.

## Documentation
The AXI protocol defines five independent channels for reads and writes as listed below. As they are independent, read/write addresses may be transferred to a slave ahead of transferring the data. So-called _transactions_ consist of one or more _tranfers_ across a set of channels. Data is transferred in _bursts_ which consist of one or more _beats_. The protocol also supports multiple outstanding transactions and out-of-order completion by using tagged packets. All channels use simple decoupled ready-valid signalling.

### Channels
The following channels are defined in the AXI4(3) protocol
- _Write address channel_ used to initiate a write transaction from master to slave. A transfer contains ID, address, burst length, burst type, cacheability etc. (see page A2-29)
- _Write data channel_ used to transfer write data from master to slave. A transfer contains ID, data, byte-wide write enable etc. (see page A2-30)
- _Write response channel_ used to inform a master about the completion of a write transaction. A transfer contains ID, write status etc. (see page A2-31)
- _Read address channel_ used to initiate a read transaction from slave to master. A transfer contains ID, address, burst length, burst type, cacheability etc. (see page A2-32)
- _Read data channel_ used to transfer read data from slave to master. A transfer contains ID, data etc. (see page A2-33)

Additionally, two global signals are used (see page A2-28)
- `ACLK` a shared global clock signal
- `ARESETn` a shared global active-low reset signal

### References
The full public protocol specification is available from ARM [here](https://developer.arm.com/documentation/ihi0022/e/) and in PDF format [here](http://www.gstitt.ece.ufl.edu/courses/fall15/eel4720_5721/labs/refs/AXI4_specification.pdf). A good video introduction is available from [ARM's YouTube channel](https://www.youtube.com/watch?v=7Vl9JrGgNwk).

## TODO
- AXI4 master interface
    - `ACLK` and `ARESETn`
    - _Write address channel_
    - _Write data channel_
    - _Write response channel_
    - _Read address channel_
    - _Read data channel_
- AXI4 master transactions
- Find simple slave system to test
- Implement small test example