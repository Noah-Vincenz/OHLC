import java.util.*;


/* TODO:
1) parse through input and store all elems in List<String>
2) continue until time period has passed (start + 300)
3) produce output (Open=first buy & first sell avg, high=highest 2 buy prices (unless/close is higher), low=lowest 2 sell prices (unless/close is lower), close=last buy & last sell)
4) execute commands
5) continue with next period
*/

public class OHLCData {

  public static void main(String[] args) {
        List<Integer> times = new ArrayList<Integer>();
        List<Integer> prices = new ArrayList<Integer>();
        List<Order> orders = new ArrayList<String>();
        Scanner scan = new Scanner(System.in);
        int startTime = 1506999900;
        String line = scan.nextLine();
        while (!line.equals("")) {

            String[] arr = line.split("\\s+");
            int currentTime = Integer.parseInt(arr[0]);

            while ((currentTime - startTime) < 300) {
                String operation = arr[1];
                switch (operation) {
                    case "ADD":
                        String id = arr[2];
                        String side = arr[3];
                        int size = Integer.parseInt(arr[4]);
                        int price = Integer.parseInt(arr[5]);
                        Order o = new Order(currentTime, operation, id, side, size, price);
                        orders.add(o);
                        break;

                    case "MODIFY":
                        String id = arr[2];
                        int size = Integer.parseInt(arr[3]);
                        int price = Integer.parseInt(arr[4]);
                        for (int i = 0; i < orders.size(); ++i) {
                            Order o = orders.get(i);
                            if(o.getID().equals(id)) {
                                o.setSize(size);
                                o.setPrice(price);
                            }
                        }
                        break;

                    case "CANCEL":
                        String id = arr[2];
                        removeByID(orders, id);
                        break;

                    case "RESET":
                        orders.clear();
                        break;
                }
            }
            startTime = currentTime;
            produceOutput();
            executeOrders();
            line = scan.nextLine();
        }
        scan.close();
  }

  public removeByID(List<Order> orders, String id) {
      for (Order o : orders) {
          if (o.getID().equals(id)) {
              orders.remove(o);
          }
      }
  }

  public produceOutput(List<Order> orders, String id) {
      // 1) OPEN: avg of first buy price and first sell price
      int firstBuyPrice = -1;
      int firstSellPrice = -1;
      for (int i = 0; i < orders.size(); ++i) {
          Order o = orders.get(i);
          if (o.getSide().equals("B")) {
              firstBuyPrice = o.getPrice;
              break;
          }
      }
      for (int i = 0; i < orders.size(); ++i) {
          Order o = orders.get(i);
          if (o.getSide().equals("S")) {
              firstSellPrice = o.getPrice;
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
          if (o.getSide().equals("B")) {
              lastBuyPrice = o.getPrice;
              break;
          }
      }
      for (int i = orders.size() - 1; i >= 0; --i) {
          Order o = orders.get(i);
          if (o.getSide().equals("S")) {
              lastSellPrice = o.getPrice;
              break;
          }
      }
      int CLOSE = -1;
      if (lastBuyPrice != -1 && lastSellPrice != -1) {
          CLOSE = (int) Math.floor((lastBuyPrice + lastSellPrice) / 2);
      }
      
      System.out.println("");
  }

  public class Order {
    private int time;
    private String operation;
    private String id;
    private String side;
    private int size;
    private int price;

    public Order(int time, String operation, String id, String side, int size, int price) {
      this.date = date;

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
      return price;
    }

    public int getPrice() {
      return price;
    }

  }
}
