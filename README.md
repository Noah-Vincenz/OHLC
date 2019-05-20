# Open-High-Low-Close Java Program

This Java program uses buy and sell trading data passed in via the Java standard input and produces the corresponding OPEN-HIGH-LOW-CLOSE output for 5 minute periods (300 seconds in UNIX time format). The list of sample data via the Java standard input should be in the following format: <br />
`Time \t OPCode \t OrderID \t Side \t Size \t Price \n`, where OPCode is one of the following:
  * ADD - add an order at a price and size
  * MODIFY - modify an order to a new price and size. Note that size is new open size in the book (i.e. not including whatever trades have already occurred)
  * CANCEL - cancel an order
  * RESET - reset state of the book (all existing orders should be cleared)
  
The output is in the following format: <br />
Time \t Open \t High \t Low \t Close \n

An example input would be the following:

```
1507000000	RESET
1507000001	ADD	1	B	10000	495
1507000002	ADD	2	S	10000	501
1507000031	ADD	3	B	5000	480
1507000050	ADD	4	B	600	499
1507000051	ADD	5	B	200	501
1507000053	ADD	6	S	800	495
1507000301	CANCEL	1
1507000302	MODIFY	2		3000	479
1507000303	MODIFY	3		5000	470
1507000304	ADD	7	S	4000	481
```

... and this produces the following output via the Java standard output:

```
1506999900  498 500 498 498
1507000200  498 498 475 475
```

## Running the Program

cd into the directory containing the OHLC.java file and run:

```
javac OHLC.java
java OHLC
```

... and pass in your data via the terminal

