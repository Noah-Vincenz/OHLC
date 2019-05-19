import java.util.*;


/* TODO:
1) parse through input and store all elems in List<String>
2) continue until time period has passed (start + 300)
3) produce output (Open=first buy & first sell avg, high=highest 2 buy prices (unless/close is higher), low=lowest 2 sell prices (unless/close is lower), close=last buy & last sell)
4) execute commands
5) continue with next period
*/

// java.util.Date time=new java.util.Date((long)timeStamp*1000)
public class OHLCData {

  public static void main(String[] args) {
        List<java.util.Date> dates = new ArrayList<java.util.Date>();
        List<Double> prices = new ArrayList<Double>();
        List<String> orders = new ArrayList<String>();

        Scanner scan = new Scanner(System.in);
        int startDate = 1506999900;
        String line = scan.nextLine();
        while (!line.equals("")) {

            String line = scan.nextLine();
            String[] arr = line.split("\\s+");
            int unixSeconds = Integer.parseInt(arr[0]);
            Date date = new Date((long) unixSeconds*1000L);
            //Date time = new Date((long)Integer.parseInt(arr[0])*1000);
            String operation = arr[1];
            switch (operation) {
                case "ADD":
                    double price = Double.parseDouble(arr[5]);
                    Record r = new Record(date, operation, id, side, size, price);
                    orders.add(r);

                    dates.add(time);
                    prices.add(price);
                    break;

                case "MODIFY":
                    double price = Double.parseDouble(arr[5]);
                    String id = arr[2];
                    int size = arr[3];
                    int price = arr[4];
                    double
                    dates.add(time);
                    prices.add(price);
                    break;

                case "CANCEL":
                    double price = Double.parseDouble(arr[5]);
                    dates.add(time);
                    prices.add(price);
                    break;

                case "RESET":
                    double price = Double.parseDouble(arr[5]);
                    dates.add(time);
                    prices.add(price);
                    break;
            }
            line = scan.nextLine();
        }
        scan.close();
        for (Date d : dates) {
          System.out.println(d.toString());
        }
        Date[] datesArr = convertDates(dates);
        double[] pricesArr = convertDoubles(prices);
        OHLCData data = new OHLCData(datesArr, pricesArr, datesArr[0], 300);
        System.out.println(data.getDates());
        System.out.println(data.getLows());
        System.out.println(data.getHighs());
        System.out.println(data.getOpens());
        System.out.println(data.getCloses());
  }

  public class Record {
    private int date;
    private String operation;
    private int id;
    private String side;
    private int size;
    private int price;

    public Record(int date, String operation, int id, String side, int size, int price) {
      this.date = date;

    }

    public int getPrice() {
      return price;
    }
  }
}
