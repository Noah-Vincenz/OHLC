import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class represents an Open-High-Low-Close generator from input data.
 * @author noah-vincenznoeh
 *
 */
public class OHLC {

	private static int previousClose = -1;

  public static void main(String[] args) {
		int startTime = 1506999900;
        List<Order> orders = new ArrayList<Order>();
        List<Order> ordersSortedByPrice = new ArrayList<Order>();
        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();
        while (!line.equals("")) { // loop while input is given
        		// split input by columns:
	    		// [0] = time
	    		// [1] = operation
	    		// [2] = id
	    		// [3] = side
	    		// [4] = size
	    		// [5] = price
        		String[] columnArr = line.split("\\s+");
            int currentTime = Integer.parseInt(columnArr[0]);
	        while (!line.equals("")) { // loop while being within period
	        		// split input by columns
	        		columnArr = line.split("\\s+"); 
	            currentTime = Integer.parseInt(columnArr[0]);
	            if ((currentTime - startTime) >= 300) {
	            		break;
	            }
	            String operation = columnArr[1];
	            if (operation.equals("ADD")) {
	                orders.add(new Order(currentTime, columnArr[2], columnArr[3], Integer.parseInt(columnArr[4]), Integer.parseInt(columnArr[5])));
	            }
	            else if (operation.equals("MODIFY")) {
	                modifyByID(orders, columnArr[2], Integer.parseInt(columnArr[3]), Integer.parseInt(columnArr[4]));
	            }
	            else if (operation.equals("CANCEL")) {
	                removeByID(orders, columnArr[2]);
	            }
	            else if (operation.equals("RESET")) {
	                orders.clear();
	                previousClose = -1;
	            }
	            line = scan.nextLine();
	        }
	        ordersSortedByPrice = copyList(orders);
	        Collections.sort(ordersSortedByPrice, new PriceTimeComparator());
	        produceOutput(startTime, orders, ordersSortedByPrice);
	        ordersSortedByPrice = executeOrders(ordersSortedByPrice);
	        // Need to do this, as orders has been updated, but ordersSortedByPrice needs to update accordingly
	        orders = copyList(ordersSortedByPrice);
	        Collections.sort(orders, new TimeComparator());
	        startTime += 300; // new 5-min period starts now
        }
        scan.close();
  }

  /**
   * This method removes a specific order from the list of orders.
   * @param orders List of orders to be used.
   * @param id The ID of the order to be removed.
   */
  private static void removeByID(List<Order> orders, String id) {
      for (int i = 0; i < orders.size(); ++i) {
    	  	  Order o = orders.get(i);
          if (o.getID().equals(id)) {
              orders.remove(o);
          }
      }
  }
  
  /**
   * This method modifies a specific order in the list of orders.
   * @param orders List of orders to be used.
   * @param id The ID of the order to be modified.
   * @param size The size that the order should have after modification.
   * @param price The price the order should have after modification.
   */
  private static void modifyByID(List<Order> orders, String id, int size, int price) {
      for (int i = 0; i < orders.size(); ++i) {
    	  	  Order o = orders.get(i);
          if (o.getID().equals(id)) {
        	  	  o.setSize(size);
              o.setPrice(price);
          }
      }
  }
  
  /**
   * This method can be used for debugging and it prints all orders in a given list of Order objects.
   * @param orders The list of orders to be printed out.
   */
  private static void printOrders (List<Order> orders) {
	  for (int i=0; i < orders.size(); ++i) {
		  System.out.println("Time: " + orders.get(i).getTime() + ", OrderID: " + orders.get(i).getID() + ", Side: " + orders.get(i).getSide() + ", Size: " + orders.get(i).getSize()+ ", Price: " + orders.get(i).getPrice());
	  }
  }
  
  /**
   * This method is used to make a copy of a given List of Order objects without referencing the same objects.
   * @param originalList The list of Order objects to be copied.
   * @return The new List.
   */
  private static List<Order> copyList (List<Order> originalList) {
      List<Order> copy = new ArrayList<Order>();
      for (int i = 0; i<originalList.size(); ++i) {
	    	  	Order o = originalList.get(i);
	        try {
				copy.add((Order) o.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
      }
      return copy;
  }

  /**
   * This method is used to print the output in the form of <TIME>\t<OPEN>\t<HIGH>\t<LOW>\t<CLOSE>\n
   * @param startTime The start time of the currently running period.
   * @param orders The list of orders sorted by time.
   * @param ordersSortedByPrice The list of orders sorted by price(descending)-time.
   */
  private static void produceOutput(int startTime, List<Order> orders, List<Order> ordersSortedByPrice) {
      // 1) OPEN: previous CLOSE or avg of first buy price and first sell price if no previous CLOSE exists
      int OPEN = -1;
	  if (previousClose == -1) { // no previous CLOSE
		  int firstBuyPrice = -1;
	      int firstSellPrice = -1;
	      for (int i = 0; i < orders.size(); ++i) {
	          Order o = orders.get(i);
	          if (o.getSide().equals("B") && o.getSize() != 0) {
	        	  	firstBuyPrice = o.getPrice();
	          }
	          else if (o.getSide().equals("S") && o.getSize() != 0) {
	        	  	firstSellPrice = o.getPrice();
	          }
	          if (firstBuyPrice != -1 && firstSellPrice != -1) {
	        	  	break;
	          }
	      }
	      if (firstBuyPrice != -1 && firstSellPrice != -1) {
	          OPEN = (int) Math.floor((firstBuyPrice + firstSellPrice) / 2); // otherwise OPEN will be -1 (= undefined)
	      }
	  } else {
		  OPEN = previousClose;
	  }
      
      // 2) CLOSE: avg of last buy price and last sell price
      int CLOSE = -1;
      int lastBuyPrice = -1;
      int lastSellPrice = -1;
      for (int i = orders.size() - 1; i >= 0; --i) {
          Order o = orders.get(i);
          if (o.getSide().equals("B") && o.getSize() != 0) {
        	  	lastBuyPrice = o.getPrice();
          }
          else if (o.getSide().equals("S") && o.getSize() != 0) {
        	  	lastSellPrice = o.getPrice();
          }
          if (lastBuyPrice != -1 && lastSellPrice != -1) {
        	  	break;
          }
      }
      if (lastBuyPrice != -1 && lastSellPrice != -1) {
          CLOSE = (int) Math.floor((lastBuyPrice + lastSellPrice) / 2);
      }
      // 3) HIGH: avg of two highest buy prices
      int HIGH = Math.max(OPEN, CLOSE);
      List<Integer> twoHighestBuyPrices = new ArrayList<Integer>(); // TODO: use array to make more efficient, since Arrays.sort() is quicker than Collections.sort()
      for (int i = 0; i < ordersSortedByPrice.size(); ++i) {
          Order o = ordersSortedByPrice.get(i);
          if (o.getSide().equals("B") && o.getSize() != 0) {
              if (twoHighestBuyPrices.size() == 0) {  // ArrayList is empty -> just add to the list
                  twoHighestBuyPrices.add(o.getPrice());
              } else if (twoHighestBuyPrices.size() == 1) { // ArrayList has one elem -> add to list and sort
                  twoHighestBuyPrices.add(o.getPrice());
                  Collections.sort(twoHighestBuyPrices);
              } else { // ArrayList has two elems with highest being at index 1
                  if (o.getPrice() > twoHighestBuyPrices.get(0)) {
                      twoHighestBuyPrices.add(1, o.getPrice());
                      twoHighestBuyPrices.remove(0);
                      Collections.sort(twoHighestBuyPrices);
                  }
              }
          }
      }
      if (twoHighestBuyPrices.size() == 2) {
          int newHigh = (twoHighestBuyPrices.get(0) + twoHighestBuyPrices.get(1)) / 2;
          if (newHigh > HIGH) {
              HIGH = newHigh;
          }
      }
      // 4) LOW: avg of two lowest sell prices
      int LOW = Math.min(OPEN, CLOSE);
      List<Integer> twoLowestSellPrices = new ArrayList<Integer>(); // TODO: use array to make more efficient
      for (int i = ordersSortedByPrice.size() - 1; i >= 0; --i) {
          Order o = ordersSortedByPrice.get(i);
          if (o.getSide().equals("S") && o.getSize() != 0) {
              if (twoLowestSellPrices.size() == 0) {  // ArrayList is empty -> just add to the list
                  twoLowestSellPrices.add(o.getPrice());
              } else if (twoLowestSellPrices.size() == 1) { // ArrayList has one elem -> add to list and sort
                  twoLowestSellPrices.add(o.getPrice());
                  Collections.sort(twoLowestSellPrices);
              } else { // ArrayList has two elems with highest being at index 1
                  if (o.getPrice() < twoLowestSellPrices.get(1)) {
                      twoLowestSellPrices.add(1, o.getPrice());
                      twoLowestSellPrices.remove(2);
                      Collections.sort(twoLowestSellPrices);
                  }
              }
          }
      }
      if (twoLowestSellPrices.size() == 2) {
    	  	  int newLow = (twoLowestSellPrices.get(0) + twoLowestSellPrices.get(1)) / 2;
          if (newLow < LOW) {
              LOW = newLow;
          }
      }
      if (OPEN != -1 && HIGH != -1 && LOW != -1 && CLOSE != -1) { // "Record should not be present if the midpoint price was not defined for the interval"
          System.out.println(startTime + "\t" + OPEN + "\t" + HIGH + "\t" + LOW + "\t" + CLOSE);
      }
      previousClose = CLOSE;
  }

  /**
   * This method is used to execute the orders in the book. It goes through all sell orders and finds buy orders for trade execution. It uses the Orders sorted by Price, because
   * Orders with best price are executed first.
   * @param ordersSortedByPrice The list of Order objects sorted by price.
   * @return The updated list of Order objects sorted by price.
   */
  private static List<Order> executeOrders(List<Order> ordersSortedByPrice) {
      for (int i = 0; i < ordersSortedByPrice.size(); ++i) { // looping from highest price order to lowest
          Order o1 = ordersSortedByPrice.get(i);
          if (o1.getSide().equals("S")) { // find sell orders
              int o1Time = o1.getTime();
              String o1ID = o1.getID();
              for (int j = 0; j < ordersSortedByPrice.size(); ++j) { // find corresponding buy orders to execute trade
            	  	Order o2 = ordersSortedByPrice.get(j);
        	  		if (o2.getSide().equals("B") && (o2.getTime() <= o1Time)) { // reduce the size of this buy order
        	  			int o2Size = o2.getSize();
        	  			String o2ID = o2.getID();
        	  			if (o2Size > o1.getSize()) {
        	  				modifyByID(ordersSortedByPrice, o2ID, o2Size - o1.getSize(), o2.getPrice());
        	  				modifyByID(ordersSortedByPrice, o1ID, 0, o1.getPrice());
        	  			} else if (o2Size <= o1.getSize()) {
        	  				modifyByID(ordersSortedByPrice, o2ID, 0, o2.getPrice());
        	  				modifyByID(ordersSortedByPrice, o1ID, o1.getSize() - o2Size, o1.getPrice());
        	  			}
        	  		}
        	  		if (o1.getSize() == 0) { // we have completely filled the sell order
    	  				break;
    	  			}
              }
          }
      }
      return ordersSortedByPrice;
  }

  /**
   * This class represents an Order object. Each order has a timestamp (Unix seconds), an operation, a unique id, a side (B or S), a size and a corresponding price.
   * @author noah-vincenznoeh
   *
   */
  private static class Order implements Cloneable {
    private int time;
    private String id;
    private String side;
    private int size;
    private int price;

    /**
     * This is the constructor for an Order object.
     * @param time The time the order was received.
     * @param operation The Order operation {ADD, MODIFY, RESET, CANCEL}.
     * @param id The unique order id.
     * @param side This specifies whether the order is a Buy or Sell order (B or S correspondingly)
     * @param size The size of the order.
     * @param price The price of the order.
     */
    private Order(int time, String id, String side, int size, int price) {
      this.time = time;
      this.id = id;
      this.side = side;
      this.size = size;
      this.price = price;
    }

    /**
     * This method returns the time of the Order object.
     * @return Order time.
     */
    private int getTime() {
      return time;
    }

    /**
     * This method returns the id of the Order object.
     * @return Order id.
     */
    private String getID() {
      return id;
    }

    /**
     * This method returns the side of the Order object.
     * @return Order side.
     */
    private String getSide() {
      return side;
    }

    /**
     * This method returns the size of the Order object.
     * @return Order size.
     */
    private int getSize() {
      return size;
    }

    /**
     * This method returns the price of the Order object.
     * @return Order price.
     */
    private int getPrice() {
      return price;
    }

    /**
     * This method sets the size of the Order object.
     * @param sizeIn The size the Order should have.
     */
    private void setSize(int sizeIn) {
      size = sizeIn;
    }

    /**
     * This method sets the price of the Order object.
     * @param priceIn The price the Order should have.
     */
    private void setPrice(int priceIn) {
      price = priceIn;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
  }
  
  /**
   * This is a Comparator implementation to sort Order objects by Time (ascending).
   * @author noah-vincenznoeh
   *
   */
  private static class TimeComparator implements Comparator<Order> {
	  	@Override
	    public int compare(Order a, Order b) {
	  		Integer time1 = a.getTime();
	  		Integer time2 = b.getTime();
	  		return time1.compareTo(time2);
	    }
  }
  
  /**
   * This is a Comparator implementation to sort Order objects by Price (descending) followed by Time (ascending).
   * @author noah-vincenznoeh
   *
   */
  private static class PriceTimeComparator implements Comparator<Order> {
	  	@Override
	    public int compare(Order a, Order b) {
	  		//price is descending - hence why we use Order b first
	  		Integer price1 = b.getPrice();
	  		Integer price2 = a.getPrice();
	  		int firstComparison = price1.compareTo(price2);
	  		if (firstComparison != 0) {
	  			return firstComparison;
	  		} 
	  		Integer time1 = a.getTime();
	  		Integer time2 = b.getTime();
	  		return time1.compareTo(time2);
	    }
  }
  
  
}
