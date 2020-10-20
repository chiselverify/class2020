# Verification of AMBA AXI Interfaced Components
This project focuses on implementation of AXI4 master interface definitions and a small framework providing support for all of the defined transactions from the AXI master's point of view.

## Documentation
The public protocol specification is available from ARM [here](https://developer.arm.com/documentation/ihi0022/e/).

A good video introduction is available from [ARM's YouTube channel](https://www.youtube.com/watch?v=7Vl9JrGgNwk).

## TODO
- AXI4 master interface
    - ACLK and ARESETn
    - _Write address channel_
    - _Write data channel_
    - _Write response channel_
    - _Read address channel_
    - _Read data channel_
- AXI4 master transactions
- Find simple slave system to test
- Implement small test example