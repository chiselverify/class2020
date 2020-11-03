# Verification of AMBA AXI Interfaced Components
This project focuses on implementation of AXI4 master interface definitions and a small framework providing support for all of the defined transactions from the AXI master's point of view in the protocol.

## Documentation
The AXI protocol defines five independent channels for reads and writes as listed below. As they are independent, read/write addresses may be transferred to a slave ahead of transferring the data. So-called _transactions_ consist of one or more _tranfers_ across a set of channels. Data is transferred in _bursts_ which consist of one or more _beats_. The protocol also supports multiple outstanding transactions and out-of-order completion by using tagged packets. All channels use simple decoupled ready-valid signalling.

### Channels
The following channels are defined in the AXI4(3) protocol
- _Write address channel_ used to initiate a write transaction from master to slave. A transfer contains ID, address, burst length, burst type, cacheability etc. (see page A2-29)
  - `AWID    [_:0]` partly optional ID field (interconnect appends master number to this, i.e. it does not have to be unique) 
  - `AWADDR  [_:0]` start address of this transaction
  - `AWLEN   [7:0]` number of beats in the burst
  - `AWSIZE  [2:0]` beat size encoding, i.e. number of bytes per beat is 2^AWSIZE
  - `AWBURST [1:0]` one of _FIXED_, _INCR_, or _WRAP_
  - `AWLOCK`, `AWCACHE`, `AWPROT`, and `AWQOS` control data protection and quality of service
  - `AWREGION` allows sharing of a single physical interface for multiple logical regions
  - _Optional_ `AWUSER`
  - `AWREADY` and `AWVALID` handshake signals

- _Write data channel_ used to transfer write data from master to slave. A transfer contains ID, data, byte-wide write enable etc. (see page A2-30)


- _Write response channel_ used to inform a master about the completion of a write transaction. A transfer contains ID, write status etc. (see page A2-31)


- _Read address channel_ used to initiate a read transaction from slave to master. A transfer contains ID, address, burst length, burst type, cacheability etc. (see page A2-32)


- _Read data channel_ used to transfer read data from slave to master. A transfer contains ID, data etc. (see page A2-33)


Additionally, two global signals are used (see page A2-28)
- `ACLK` a shared global clock signal
- `ARESETn` a shared global active-low reset signal

Channel descriptions are available in `./src/main/scala/Defs.scala`. DUVs must conform to the signal names and interfaces provided to function correctly - hence, their IO should extend either the available master or slave interfaces. Wrappers are provided in `./src/main/scala/SlaveWrapper.scala` and `./src/main/scala/MasterWrapper.scala`.

### References
The full public protocol specification is available from ARM [here](https://developer.arm.com/documentation/ihi0022/e/) and in PDF format [here](http://www.gstitt.ece.ufl.edu/courses/fall15/eel4720_5721/labs/refs/AXI4_specification.pdf). A good video introduction is available from [ARM's YouTube channel](https://www.youtube.com/watch?v=7Vl9JrGgNwk).

## TODO
- AXI4 master interface
- AXI4 master transactions
- Find simple slave system to test
- Implement small test example

## Notes
The following are taken from the specification.

### Regarding ready-valid interface
- No combinational logic between `Ready` and `Valid` in neither master nor slave components
- A source _may not_ wait until `Ready` is asserted before asserting `Valid`
- A destination _may_ wait until `Valid` is asserted before asserting `Ready`
- Once asserted, `Valid` _must_ remain asserted until a handshake occurs
- If `Ready` is asserted, it _can_ be deasserted before assertion of `Valid`
- Transfers occur at the first rising edge after both `Ready` and `Valid` are asserted
- Address channels _should_ per default have `AWREADY` and `ARREADY` asserted to avoid transfers taking at least two cycles

### Regarding channel relationships
- A write response must always follow the last write transfer in a write transaction
- Read data must always follow the address to which it the data relates
- Write data _can_ appear before or simultaneously with the write address for the transaction

### Regarding transactions
- Bursts _must not_ cross a 4KB boundary (to avoid device address space mapping issues)
- Burst length is in \[1, 256\] for burst type INCR; for all other burst types it is in \[1, 16\]
- Bursts _must_ be completed (i.e. no support for early termination)
- Beat size _must not_ exceed the data bus width of neither the master nor the slave.

