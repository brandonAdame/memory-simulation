// ****************************************************
// Reference-based implementation of ADT list using arrays.
// Due to the limitations with array of generics, the
// "data type" for the list items is fixed to be of type
// PageUsage.  Any program using this class must specify
// <PageUsage> as the value for the type parameter.
// ****************************************************
public class List<T> {
    // reference to linked list of items

    public static final int MAX_LIST = 20;
    public static final int NULL = -1;

    private PageUsage item[] = new PageUsage[MAX_LIST];  // data
    private int next[] = new int[MAX_LIST];       // pointer to next item

    private int head;     // pointer to front of list
    private int free;     // pointer to front of free list
    private int numItems; // number of items in list

// Constructor must initialize used list to empty and free list to 
// all available nodes.

    public List() {
        for (int index = 0; index < MAX_LIST - 1; index++)
            next[index] = index + 1;

        next[MAX_LIST - 1] = NULL;

        numItems = 0;
        head = NULL;
        free = 0;
    }

    public void removeAll() {   // reinitialize all nodes to free
        for (int index = 0; index < MAX_LIST - 1; index++)
            next[index] = index + 1;

        next[MAX_LIST - 1] = NULL;

        numItems = 0;
        head = NULL;
        free = 0;
    }

    public boolean isEmpty() {
        return numItems == 0;
    }

    public int size() {
        return numItems;
    }

    /**
     * Locates a specified node in a linked list.
     *
     * Pre-condition: index is the number of the desired
     * node. Assumes that 1 <= index <= numItems
     *
     * Post-condition: Returns a reference to the desired
     * node.
     *
     * Helper function.
     *
     * @param index
     * @return
     */
    private int find(int index) {
        int ptr = head;
        for (int i = 2; i <= index; i++) {
            ptr = next[ptr];
        }
        return ptr;
    }

    /**
     * Gets a PageUsage object at 'index'
     *
     * @param index Integer
     * @return PageUsage
     */
    public PageUsage get(int index) {
        if (!isEmpty()) {
            return item[find(index)];
        }
        return null;
    }

    /**
     * Adds a PageUsage object at a particular index
     * in the array 'item'.
     *
     * @param index   Integer
     * @param newItem PageUsage
     */
    public void add(int index, PageUsage newItem) {
        // inserting at the beginning of the list
        if (index == 1) {

            int tempOldHead = head;
            head = free;
            free = next[free];
            next[head] = tempOldHead;
            item[head] = newItem;

        } else if (index == size() + 1) { // inserting at the end

            int tmp = find(index-1); // finds get the
            next[tmp] = free;
            item[free] = newItem;
            free = next[free];
            next[next[tmp]] = NULL;

        } else { // inserting at the middle
            int pos = find(index-1);
            int pos2 = next[pos];

            next[pos] = free;
            free = next[free];
            next[next[pos]] = pos2;
            item[next[pos]] = newItem;
        }
        this.numItems++;
    }

    /**
     * This method removes an item at 'index'
     *
     * @param index Integer
     */
    public void remove(int index) {

        // Remove from the beginning
        if (index == 1) {
            // Save current 'next' pointer
            int tmp = next[head];

            next[head] = free;
            free = head;
            head = tmp;
            item[free] = null;
        } else {
            int prev = find(index - 1);
            int curr = next[prev];
            int nxt = next[curr];
            next[prev] = nxt;
            item[curr] = null;
            next[curr] = free;
            free = curr;
        }
        this.numItems--;
    }
}