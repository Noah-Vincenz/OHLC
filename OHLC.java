import java.util.*;


/* TODO:
1) parse through input and store all elems in List<String>
2) continue until time period has passed (start + 300)
3) produce output (Open=first buy & first sell avg, high=highest 2 buy prices (unless/close is higher), low=lowest 2 sell prices (unless/close is lower), close=last buy & last sell)
4) execute commands
5) continue with next period
*/

public class OHLC {

  public static void main(String[] args) {
        List<Order> orders = new ArrayList<Order>();
        List<Order> ordersSortedByPrice = new ArrayList<Order>();
        Scanner scan = new Scanner(System.in);
        int startTime = 1506999900;
        String line = scan.nextLine();
        while (!line.equals("")) {
            String[] arr = line.split("\\s+");
            int currentTime = Integer.parseInt(arr[0]);
           
            while (!line.equals("")) {
            		arr = line.split("\\s+");
                currentTime = Integer.parseInt(arr[0]);
                System.out.println("CurrentTime: " + currentTime);
                System.out.println("Current - start time: " + (currentTime - startTime));
                if ((currentTime - startTime) >= 300) {
                		break;
                }
                String operation = arr[1];
                System.out.println("Operation: " + operation);
                if (operation.equals("ADD")) {
                    String id = arr[2];
                    String side = arr[3];
                    int size = Integer.parseInt(arr[4]);
                    int price = Integer.parseInt(arr[5]);
                    Order o = new Order(currentTime, operation, id, side, size, price);
                    orders.add(o);
                    System.out.println("Orders(" + orders.size() +"): ");
                }
                else if (operation.equals("MODIFY")) {
                    String id = arr[2];
                    System.out.println("MODIFYYYYYY");
                    int size = Integer.parseInt(arr[3]);
                    int price = Integer.parseInt(arr[4]);
                    for (int i = 0; i < orders.size(); ++i) {
                        Order o = orders.get(i);
                        if(o.getID().equals(id)) {
                            o.setSize(size);
                            o.setPrice(price);
                        }
                    }
                }
                else if (operation.equals("CANCEL")) {
                    String id = arr[2];
                    System.out.println("CANCELLLLLLLLL");
                    System.out.println(orders.size());
                    removeByID(orders, id);
                    System.out.println(orders.size());
                }
                else if (operation.equals("RESET")) {
                    orders.clear();
                }
                line = scan.nextLine();
            }

            ordersSortedByPrice = copyList(orders);
            Collections.sort(ordersSortedByPrice);
            System.out.println("Orders(" + orders.size() +"): ");
            printList(orders);
            System.out.println("");
            System.out.println("Orders sorted by price-date(" + ordersSortedByPrice.size() + "): ");
            printList(ordersSortedByPrice);
            System.out.println("OUTPUT:");
            produceOutput(startTime, orders, ordersSortedByPrice);
            ordersSortedByPrice = executeOrders(ordersSortedByPrice);
            // Need to do this, as orders has been updated, but ordersSortedByPrice needs to update correspondingly
            orders = copyList(ordersSortedByPrice);
            Collections.sort(orders, new TimeComparator());
            System.out.println("After execution:");
            System.out.println("Orders(" + orders.size() +"): ");
            printList(orders);
            System.out.println("");
            System.out.println("Orders sorted by price-date(" + ordersSortedByPrice.size() + "): ");
            printList(ordersSortedByPrice);
            startTime += 300;
            //line = scan.nextLine();
        }
        scan.close();
  }

  public static void removeByID(List<Order> orders, String id) {
      for (int i = 0; i < orders.size(); ++i) {
    	  	  Order o = orders.get(i);
          if (o.getID().equals(id)) {
              orders.remove(o);
          }
      }
  }
  
  public static void printList (List<Order> orders) {
	  for (int i=0; i < orders.size(); ++i) {
		  System.out.println("Time: " + orders.get(i).getTime() + ", OrderID: " + orders.get(i).getID() + ", Side: " + orders.get(i).getSide() + ", Size: " + orders.get(i).getSize()+ ", Price: " + orders.get(i).getPrice());
	  }
  }

  public static List<Order> copyList (List<Order> originalList) {
      List<Order> copy = new ArrayList<Order>();
      for (Order o : originalList) {
          try {
			copy.add((Order) o.clone());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
      return copy;
  }

  public static void produceOutput(int startTime, List<Order> orders, List<Order> ordersSortedByPrice) {
      // TODO: use ordersSortedByPrice to access highest buy and sell price quickly
      // 1) OPEN: avg of first buy price and first sell price
      int firstBuyPrice = -1;
      int firstSellPrice = -1;
      for (int i = 0; i < orders.size(); ++i) {
          Order o = orders.get(i);
          if (o.getSide().equals("B") && o.getSize() != 0) {
              firstBuyPrice = o.getPrice();
              break;
          }
      }
      for (int i = 0; i < orders.size(); ++i) {
          Order o = orders.get(i);
          if (o.getSide().equals("S") && o.getSize() != 0) {
              firstSellPrice = o.getPrice();
              break;
          }
      }
      int OPEN = -1;
      if (firstBuyPrice != -1 && firstSellPrice != -1) {
          OPEN = (int) Math.floor((firstBuyPrice + firstSellPrice) / 2);
      }
      // 2) CLOSE: avg of last buy price and last sell price
      int lastBuyPrice = -1;
      int lastSellPrice = -1;
      for (int i = orders.size() - 1; i >= 0; --i) {
          Order o = orders.get(i);
          if (o.getSide().equals("B") && o.getSize() != 0) {
              lastBuyPrice = o.getPrice();
              break;
          }
      }
      for (int i = orders.size() - 1; i >= 0; --i) {
          Order o = orders.get(i);
          if (o.getSide().equals("S") && o.getSize() != 0) {
              lastSellPrice = o.getPrice();
              break;
          }
      }
      int CLOSE = -1;
      if (lastBuyPrice != -1 && lastSellPrice != -1) {
          CLOSE = (int) Math.floor((lastBuyPrice + lastSellPrice) / 2);
      }
      // 3) HIGH: avg of two highest buy prices
      // TODO: sort first to make more efficient
      int HIGH = Math.max(OPEN, CLOSE);
      List<Integer> twoHighestBuyPrices = new ArrayList<Integer>(); // TODO: use array to make more efficient
      for (int i = 0; i < ordersSortedByPrice.size(); ++i) {
          Order o = ordersSortedByPrice.get(i);
          if (o.getSide().equals("B") && o.getSize() != 0) {
              if (twoHighestBuyPrices.size() == 0) {  // ArrayList not full yet
                  twoHighestBuyPrices.add(o.getPrice());
              } else if (twoHighestBuyPrices.size() == 1) {
                  twoHighestBuyPrices.add(o.getPrice());
                  Collections.sort(twoHighestBuyPrices);
              } else {
                  if (o.getPrice() > twoHighestBuyPrices.get(0)) {
                      twoHighestBuyPrices.add(1, o.getPrice());
                      twoHighestBuyPrices.remove(0);
                      Collections.sort(twoHighestBuyPrices);
                  }
              }
          }
      }
      int newHigh = (twoHighestBuyPrices.get(0) + twoHighestBuyPrices.get(1)) / 2;
      if (newHigh > HIGH) {
          HIGH = newHigh;
      }
      // 4) LOW: avg of two lowest sell prices
      int LOW = Math.min(OPEN, CLOSE);
      List<Integer> twoLowestSellPrices = new ArrayList<Integer>(); // TODO: use array to make more efficient
      for (int i = ordersSortedByPrice.size() - 1; i >= 0; --i) {
          Order o = ordersSortedByPrice.get(i);
          if (o.getSide().equals("S") && o.getSize() != 0) {
              if (twoLowestSellPrices.size() == 0) {  // ArrayList not full yet
                  twoLowestSellPrices.add(o.getPrice());
              } else if (twoLowestSellPrices.size() == 1) {
                  twoLowestSellPrices.add(o.getPrice());
                  Collections.sort(twoLowestSellPrices);
              } else {
                  if (o.getPrice() < twoLowestSellPrices.get(1)) {
                      twoLowestSellPrices.add(1, o.getPrice());
                      twoLowestSellPrices.remove(2);
                      Collections.sort(twoLowestSellPrices);
                  }
              }
          }
      }
      int newLow = (twoLowestSellPrices.get(0) + twoLowestSellPrices.get(1)) / 2;
      if (newLow > LOW) {
          LOW = newLow;
      }
      System.out.println(startTime + "\t" + OPEN + "\t" + HIGH + "\t" + LOW + "\t" + CLOSE + "\n");
  }

  public static List<Order> executeOrders(List<Order> ordersSortedByPrice) {
      // go through all sell orders
      // look at sell amount and find buy amounts starting with highest before sell - decrease size
      // Sort orders by high
      // TODO: this does not work as we need to continue working with 'orders' in next round, however, we are only modyfying ordersSortedByPrice
      // maybe make orders a hashmap<time,Order>? can access elems O(1)
      for (int i = 0; i < ordersSortedByPrice.size(); ++i) { // looping from highest price order to lowest
          Order o1 = ordersSortedByPrice.get(i);
          if (o1.getSide().equals("S")) {
              int o1Time = o1.getTime();
              String o1ID = o1.getID();
              System.out.println("REDUCING SELL ORDER " + o1ID);
              for (int j = 0; j < ordersSortedByPrice.size(); ++j) {
            	  	Order o2 = ordersSortedByPrice.get(j);
        	  		if (o2.getSide().equals("B") && (o2.getTime() <= o1Time)) { // reduce the size of this buy order
        	  			int o2Size = o2.getSize();
        	  			String o2ID = o2.getID();
        	            System.out.println("REDUCING BUY ORDER " + o2ID);
        	  			if (o2Size > o1.getSize()) {
            	            System.out.println("O2 is larger than O1!");
        	  				reduceSizeByID(o2ID, o2Size - o1.getSize(), ordersSortedByPrice);
        	  				reduceSizeByID(o1ID, 0, ordersSortedByPrice);
        	  			} else if (o2Size <= o1.getSize()) {
            	            System.out.println("O2 is smaller than O1!");
        	  				reduceSizeByID(o2ID, 0, ordersSortedByPrice);
        	  				reduceSizeByID(o1ID, o1.getSize() - o2Size, ordersSortedByPrice);
        	  				System.out.println("New o1 size: " + o1.getSize());
        	  				System.out.println("New o2 size: " + o2.getSize());
        	  			}
        	  		}
        	  		if (o1.getSize() == 0) {
    	  				break;
    	  			}
              }
          }
      }
      return ordersSortedByPrice;
  }

  public static void reduceSizeByID(String id, int newSize, List<Order> orders) {
	  for (int i=0; i<orders.size(); ++i) {
		  Order o = orders.get(i);
		  if (o.getID().equals(id)) {
			  o.setSize(newSize);
		  }
	  }
  }
  
  public static class TimeComparator implements Comparator<Order> {
	  	@Override
	    public int compare(Order a, Order b) {
	        return a.getTime() < b.getTime() ? -1 : a.getTime() == b.getTime() ? 0 : 1;
	    }
	}

  public static class Order implements Comparable<Order>, Cloneable {
    private int time;
    private String operation;
    private String id;
    private String side;
    private int size;
    private int price;

    public Order(int time, String operation, String id, String side, int size, int price) {
      this.time = time;
      this.operation = operation;
      this.id = id;
      this.side = side;
      this.size = size;
      this.price = price;
    }

    public int getTime() {
      return time;
    }

    public String getOperation() {
      return operation;
    }

    public String getID() {
      return id;
    }

    public String getSide() {
      return side;
    }

    public int getSize() {
      return size;
    }

    public int getPrice() {
      return price;
    }

    public void setSize(int sizeIn) {
      size = sizeIn;
    }

    public void setPrice(int priceIn) {
      price = priceIn;
    }

    @Override
    public int compareTo(Order o) {

        // price is descending - hence why using Order o first
	    	Integer price1 = o.getPrice();
	    	Integer price2 = this.getPrice();
        int firstComparison = price1.compareTo(price2);
        if (firstComparison != 0) {
           return firstComparison;
        }
        Integer time1 = this.getTime();
        Integer time2 = o.getTime();
        int secondComparison = time1.compareTo(time2);
        return secondComparison;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {

        return super.clone();
    }

  }
}
