public class ListTest {
    // This class is meant to test the reimplementation of List.java as an
// array-based linked list. It adds a few entries, removes entries, and
// prints the list (thus testing the retrieval of entries).

        public static void main(String args[]) {
            List<PageUsage> L = new List<PageUsage>();

            System.out.printf("The initial size of the list is: %d\n", L.size());

            System.out.printf("Adding entries to list. List should be: 10 -> 20 -> 25 -> 30\n");
            L.add(1, new PageUsage(10, 10));
            L.add(2, new PageUsage(25, 25));
            L.add(2, new PageUsage(20, 20));
            L.add(L.size()+1, new PageUsage(30, 30));

            printList(L);

            System.out.printf("Removing entries from list. List should be: 20 -> 30\n");
            L.remove(1);
            L.remove(2);

            printList(L);

        }

        // Prints the current entries of a list
        // @param L The list to print the entries of
        public static void printList(List<PageUsage> L) {
            System.out.printf("Current list entries are as follows:\n");
            for(int i=1; i<=L.size(); i++)
                System.out.printf("  L[%d]: %d\n", i, L.get(i).getStart());
        }

}