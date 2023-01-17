


import java.io.*;
import java.util.*;

public class TOPKFI{

  public static boolean DEBUG = false;

  public static void main(String args[]){

    // parse input arguments
    if(args.length != 3){
      System.out.println("The arguments are not correct!");
      System.out.println("Please use \njava TopKFI datasetpath K M");
      return;
    }

    String db_path = args[0];
    int K = Integer.parseInt(args[1]);
    int M = Integer.parseInt(args[2]);

    if(K < 0 || M < 0){
      System.out.println("K and M should be positive!");
      return;
    }

    if(DEBUG){
      System.out.println("Path to dataset: "+db_path);
      System.out.println("K: "+K);
      System.out.println("M: "+M);
    }

    // read the input file
    try {
      File file_db = new File(db_path);
      Scanner db_reader = new Scanner(file_db);
      int transaction_id = 0;
      while (db_reader.hasNextLine()) {
        transaction_id++;
        String transaction = db_reader.nextLine();
        if(DEBUG){
          System.out.println("transaction "+transaction_id+" is "+transaction);
        }
        String[] items_str = transaction.split("\\s+");
        int[] items = new int[items_str.length];
          /* read the transaction "items_str" into the array "items" */
          for(int i=0; i<items_str.length; i++){
            try{
              items[i] = Integer.parseInt(items_str[i]);
              if(DEBUG){
                System.out.println("  item "+items[i]);
              }
            } catch (NumberFormatException e) {
              System.out.println("Input format of transaction is wrong!");
              System.out.println("transaction "+transaction_id+" is "+transaction);
              e.printStackTrace();
              return;
            }
          }

        /* do something with the array "items" ... */
        /* Compute supports of all singleton itemsets*/
        
        Map<Integer, Integer> supports = computeSupports(items);
        
        }
        db_reader.close();
      } catch (FileNotFoundException e) {
        System.out.println("The file "+db_path+" does not exist!");
        e.printStackTrace();
        return;
      }


/* start mining itemsets... */
      // Initialize priority queue with singleton itemsets ordered by support
      Map<Integer, Integer> supports = new HashMap<>();
      /*PriorityQueue<Itemset> queue = new PriorityQueue<>();
      for(Map.Entry<Integer, Integer> entry: supports.entrySet()){
          int item = entry.getKey();
          int support = entry.getValue();
          Itemset itemset = new Itemset(item, support);
          queue.offer(itemset);
      }*/
      
      PriorityQueue<Itemset> queue = new PriorityQueue<>(Comparator.comparing(Itemset::getSupport).reversed());
                       for (Map.Entry<Integer, Integer> entry : supports.entrySet()) {
                           List<Integer> itemset = new ArrayList<>();
                           itemset.add(entry.getKey());
                           queue.offer(new Itemset(itemset, entry.getValue()));
                       }
                       
                       // Initialize list S of itemsets
                       List<Itemset> S = new ArrayList<>();
                       
                       // Iterate K times or until queue is empty
                       for (int i = 0; i < K && !queue.isEmpty(); i++) {
                       // Extract highest-support itemset from queue
                          Itemset itemset = queue.poll();
                          S.add(itemset);
                          // Generate and add to queue all possible supersets of itemset with an additional item
            List<List<Integer>> transactions = new ArrayList<>();
            int maxItem = Collections.max(itemset.items);
            for (int j = maxItem + 1; j <= supports.size(); j++) {
                // Compute support of Y = X ∪ {b}
                Itemset Y = itemset.add(j);
                int supportY = computeSupport(transactions, Y.items);
                if (supportY > 0) {
                    queue.offer(new Itemset(Y.items, supportY));
                }
            }
        }
        
        // Output size of S
        System.out.println(S.size());

        // Output entries of S if |S| <= M
        if (S.size() <= M) {
            for (Itemset itemset : S) {
                System.out.println(itemset.items+ "(" + itemset.support + ")");
            }
        }
    
      
  }
  private static Map<Integer, Integer> computeSupports(int[] items) {
      Map<Integer, Integer> supports = new HashMap<>();
      for(int item: items){
          Integer currentSupport = supports.get(item);
          if (currentSupport == null){
              currentSupport = 0;
          }
          currentSupport++;
          
          supports.put(item, currentSupport);

      
      }
  return supports;
  }
  private static int computeSupport(List<List<Integer>> transactions, List<Integer> itemset) {
    int count = 0;
    for (List<Integer> transaction : transactions) {
        if (transaction.containsAll(itemset)) {
            count++;
        }
    }
    return count;
    }

  public static class Itemset {
    private List<Integer> items;
    private int support;
  
  public Itemset(List<Integer> items, int support) {
        this.items = items;
        this.support = support;
    }

    public List<Integer> getItems() {
        return items;
    }

    public int getSupport() {
        return support;
    }
    
    public Itemset add(int item) {
    List<Integer> newItems = new ArrayList<>(items);
    newItems.add(item);
    return new Itemset(newItems, support);
    }
    }
}

