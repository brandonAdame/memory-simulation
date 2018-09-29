import java.util.Scanner;

/**
 * Brandon Gachuz
 * CSCI 2540
 * Fall 2017
 *
 * File: MemSim.java
 *
 * This program simulates memory management by an operating system. Memory is usually
 * organized by pages, each of which is capable of storing the same number of bytes.
 *
 * When a request for memory management is made by a program, the operating system must determine if
 * enough free pages are available to satisfy the memory management request.
 *
 * It must also keep track of which pages contain information for a given program,
 * as well as keep track of the pages still available.
 *
 * THE SIMULATION WORKS AS FOLLOWS:
 *
 * The program will read and process a sequence of instructions read from the terminal.
 *
 * <opcode> <prog_id> <size>
 *
 * 'opcode' is a single character. Possible values are, 'i' for initiate, 't' for terminate,
 * 'p' for print, 'g' for grow, 's' for shrink, and 'x' to exit.
 *
 * 'prog_id' is an integer between 0 and NUM_PROGRAMS-1 (inclusive).
 * 'size' is an integer between 0 and MAX_SIZE (inclusive).
 *
 * ERROR HANDLING: Required for the following nine situations
 * 1. Attempting to initiate a program that already exists
 * 2. Attempting to initiate a program for which there is not enough available memory.
 * 3. Attempting to terminate a non-existing program.
 * 4. Attempting to print the information for a non-existing program.
 * 5. Attempting to grow a program that does not exist.
 * 6. Attempting to grow a program for which there is not enough available space.
 * 7. Attempting to grow a program for which the additional <size>, when combined
 *    with the original size, exceeds MAX_SIZE.
 * 8. Attempting to shrink a program that does not exist.
 * 9. Attempting to shrink a program by more bytes then are currently allocated
 *    to the program.
 */
public class MemSim {

    // Maximum number of pages
    public static final int NUM_PAGES = MemParam.NUM_PAGES;

    // Maximum number of programs
    public static final int NUM_PROGRAMS = MemParam.NUM_PROGRAMS;

    // Maximum size
    public static final int MAX_SIZE = MemParam.MAX_SIZE;

    // Maximum number of page size
    public static final int PAGE_SIZE = MemParam.PAGE_SIZE;


    /**
     * Gets the number of free pages from param 'pages'
     *
     * @param pages List<PageUsage>
     * @return Int
     */
    public static int getFreePageCount(List<PageUsage> pages) {
        int pCount = 0;
        for (int i = 1; i <= pages.size(); i++) {
            pCount += pages.get(i).getEnd() - pages.get(i).getStart() + 1;
        }
        return pCount;
    }

    /**
     * When adding pages (PageUsage objects), this method will store them in ascending order
     * (based on the start value). 'param' will be stored in a list of PageUsage object 'lst'.
     *
     * @param param PageUsage
     * @param lst   List<PageUsage>
     */
    public static void putInOrder(PageUsage param, List<PageUsage> lst) {
        for (int i = 1; i <= lst.size(); i++) {
            if (lst.get(i).getStart() > param.getStart()) {
                lst.add(i, param);
                return;
            }
        }
        lst.add(lst.size() + 1, param);
    }

    /**
     * This method handles the initiate request. When the operating system requests to initiate a program,
     * it must first check if the program that it wants to initiate does not already exists.
     *
     * If the condition is true, the OS will proceed to calculate the amount of memory it needs to allocate
     * for the specific program. Additionally, if the program is too large to initiate, an error
     * message will display:
     *
     * insufficient space for the program
     *
     * If the OS attempts to initiate a program that already exists, it will display an error message:
     *
     * ERROR on initiate: program <prog_id> already exists
     *
     * @param programsArray ProgInfo[]
     * @param progID        Int
     * @param size          Int
     * @param freePageList  List<PageUsage>
     */
    public static void handleInitiate(ProgInfo programsArray[], int progID, int size,
                                      List<PageUsage> freePageList) {

        // Checks to see if program that will be initiated is not already active
        if (programsArray[progID].bytes == -1) {

            int numP = (size / PAGE_SIZE);

            // Calculates the number pages that will be needed
            if (size % PAGE_SIZE != 0) {
                numP++;
            }

            // Gets free page count
            int freePGCount = getFreePageCount(freePageList);

            if (freePGCount >= numP) {

                programsArray[progID].bytes = size;

                handleInitiateHelper(freePageList, numP, programsArray, progID);

                System.out.printf("%nProgram %d initiated, size = %d%n", progID, size);
            } else {
                // ERROR : NOT ENOUGH PAGES
                System.out.printf("insufficient space for Program %d%n", progID);
            }
        } else {
            // ERROR : PROGRAM DOES NOT EXIST
            System.out.printf("ERROR on initiate: Program %d already exists.%n", progID);
        }
    }

    /**
     * handleInitiateHelper is a method with the purpose of condensing the code in 'handleInitiate'.
     * The primary focus for this method is to loop through the list 'pUsage' and add PageUsage
     * items in ascending order.
     *
     * @param pUsage List<PageUsage>
     * @param numP   Int
     * @param pArr   ProgInfo[]
     * @param progID Int
     */
    public static void handleInitiateHelper(List<PageUsage> pUsage, int numP, ProgInfo pArr[], int progID) {

        // Looping through PageUsage list 'pUsage'
        for (int i = 1; numP != 0; i++) {

            PageUsage temp = pUsage.get(i);

            // Calculates the # of pages from end to start
            int ans = (temp.getEnd() - temp.getStart()) + 1;

            if (ans <= numP) {

                // Adds the pageUsage item in ascending order
                putInOrder(temp, pArr[progID].prog_usage);
                pUsage.remove(i);
                numP -= ans;
                --i;
            } else {

                // Adds the PageUsage item in ascending order
                putInOrder(new PageUsage(temp.getStart(),
                                temp.getStart() + numP - 1),
                        pArr[progID].prog_usage);
                temp.setStart(numP + temp.getStart());
                numP = 0;
            }
        }
    }

    /**
     * This method handles the terminate request. Before any termination can occur, the method must first
     * check if the program that wants to be terminated exists - if not, then an error message is displayed.
     *
     * Otherwise, the method will calculate the amount of memory that will be freed upon termination
     * and display that data.
     *
     * @param programsArray ProgInfo
     * @param progID        Int
     * @param freePageList  List<PageUsage>
     */
    public static void handleTerminate(ProgInfo programsArray[], int progID, List<PageUsage> freePageList) {
        int bytesAns = programsArray[progID].bytes;
        if (bytesAns == -1) {

            // Error on terminate command
            System.out.printf("ERROR on terminate command: Program %d does not exist", progID);

        } else {

            programsArray[progID].bytes = -1;
            List<PageUsage> temp = programsArray[progID].prog_usage;
            int freedPages = 0;

            for (int i = temp.size(); i >= 1; i--) {

                // Updating total number of freed pages
                freedPages += (temp.get(1).getEnd() - temp.get(1).getStart()) + 1;
                putInOrder(temp.get(1), freePageList);
                temp.remove(1);
            }
            System.out.printf("%nProgram %d terminated, %d pages freed", progID, freedPages);
        }
    }

    /**
     * handlePrint will print the contents of a free page list if prompted by the OS.
     *
     * If the method is successful it displays the start and end page for free page list.
     *
     * Example output:
     *
     * Contents of a free page list
     * Start page   End page
     * 1           3
     *
     * @param programsArray ProgInfo
     * @param progID        Int
     * @param freePageList  List<PageUsage>
     */
    public static void handlePrint(ProgInfo programsArray[], int progID, List<PageUsage> freePageList) {

        // Check if the progID is a negative value
        if (progID < 0) {

            System.out.printf("%nContents of free page list%n");
            System.out.printf("Start page  End page%n");

            for (int i = 1; i <= freePageList.size(); i++) {
                System.out.printf("%5d%11d%n", freePageList.get(i).getStart(),
                        freePageList.get(i).getEnd());
            }

        } else {
            handlePrintHelper(programsArray, progID);
        }
    }

    /**
     * This helper method is designed to condense the code in 'handlePrint'.
     *
     * If OS attempts to print for a program that does not exist, then the following message
     * will display:
     *
     * ERROR on print command: Program <prog_id> does not exist
     *
     * Otherwise, if the progID is not a negative and the program does exist,
     * the following (example output) information about the page usage of a program will display
     *
     * Example output:
     *
     * Page usage for program <prog_id> --- size = <size>
     * Start page   End page
     * 1           0
     *
     * @param programsArray ProgInfo
     * @param progID        Int
     */
    public static void handlePrintHelper(ProgInfo programsArray[], int progID) {
        if (programsArray[progID].bytes == -1) {

            System.out.printf("%nERROR on print command: Program %d does not exist", progID);

        } else {

            System.out.printf("Page usage for program %d --- size = %d", progID,
                    programsArray[progID].bytes);
            System.out.printf("%nStart page  End page%n");

            List<PageUsage> tmp = programsArray[progID].prog_usage;

            for (int i = 1; i <= tmp.size(); i++) {
                System.out.printf("%5d%11d%n", tmp.get(i).getStart(), tmp.get(i).getEnd());
            }
        }
    }

    /**
     * handleExit will handle exiting the memory simulation. Upon completion, a message will display
     * the number of programs that exist and the number of pages they occupy collectively.
     *
     * @param programsArray ProgInfo
     */
    public static void handleExit(ProgInfo programsArray[]) {
        int pgOccupied = 0;
        int running = 0;

        for (int i = 0; i < programsArray.length; i++) {
            if (programsArray[i].bytes > -1) {
                running++;

                List<PageUsage> pUsage = programsArray[i].prog_usage;

                for (int inner = 1; inner <= pUsage.size(); inner++) {
                    pgOccupied += pUsage.get(inner).getEnd() -
                            pUsage.get(inner).getStart() + 1;
                }
            }
        }
        System.out.printf("%nSIMULATOR EXIT: %d programs exist, occupying %d pages%n",
                running, pgOccupied);

    }

    /**
     * This helper method calculates the number of pages
     * needed by the amount of bytes passed through.
     *
     * @param bytes Integer
     * @return Integer
     */
    private static int calcPagesHelper(int bytes) {
        int ans;
        if (bytes % PAGE_SIZE == 0) {
            ans = 0;
        } else {
            ans = 1;
        }

        return (bytes/PAGE_SIZE) + (ans);
    }

    /**
     * This method grows the size of a program by the size requested in 'size'.
     * Common errors with this method are:
     *
     * Attempting to grow a program that does not exist
     *
     * Attempting to grow a program for which there is not enough available space
     *
     * Attempting to grow a program for which the additional <size>
     * when combined with the original size, exceeds MAX_SIZE
     *
     * @param programsArray ProgInfo
     * @param freePageList List<PageUsage>
     * @param progID Integer
     * @param size Integer
     */
    public static void handleGrow(ProgInfo programsArray[],
                                  List<PageUsage> freePageList, int progID, int size) {

        // Number of bytes in a program
        int bytesAns = programsArray[progID].bytes;

        if (bytesAns == -1) {
            System.out.printf("%nERROR on grow command: Program %d does not exist%n", progID);
        } else if (bytesAns + size > MAX_SIZE) {
            System.out.printf("%nERROR on grow command: MAX SIZE exceeded for Program %d%n", progID);
        } else {
            int progSize = programsArray[progID].bytes;
            int progPages = calcPagesHelper(progSize);
            progSize += size;
            int totPages = progSize;
            int amountNeeded = totPages - progPages;

            // Free page count
            int fPages = getFreePageCount(freePageList);

            if (amountNeeded > fPages) {
                System.out.printf("%nERROR on grow command: insufficient space for program %d%n", progID);
            } else {
                programsArray[progID].bytes = progSize;

                // Allocate needed pages
                for (int i = 1; i <= freePageList.size() && progPages > 0; i++) {
                    PageUsage temp = freePageList.get(i);
                    int ans = (temp.getEnd()-temp.getStart()) + 1;

                    if (ans > progPages) {
                        putInOrder(new PageUsage(temp.getStart(), temp.getStart() + progPages-1),
                                programsArray[progID].prog_usage);

                        temp.setStart(temp.getStart()+progPages);
                        progPages = 0;
                    } else {
                        putInOrder(temp, programsArray[progID].prog_usage);
                        progPages -= temp.getEnd()-temp.getStart()+1;
                        freePageList.remove(i);
                        --i;
                    }
                }
                // Success Message
                System.out.printf("%nProgram %d increased by %d bytes, new size = %d%n",
                        progID, size, progSize);
            }
        }
    }

    /**
     * This method handles shrinking a specific program in the memory simulation
     * by a specified 'size'.
     *
     * Common errors encountered:
     *
     * Attempting to shrink a program that does not exist
     *
     * Attempting to shrink a program by more bytes than are currently
     * allocated to the program
     *
     * @param programsArray ProgInfo
     * @param freePageList List<PageUsage>
     * @param progID Integer
     * @param size Integer
     */
    public static void handleShrink(ProgInfo programsArray[], List<PageUsage> freePageList,
                                    int progID, int size) {

        int pSize = programsArray[progID].bytes;

        if (pSize == -1) {
            System.out.printf("%nERROR on shrink command: Program %d does not exist%n", progID);
        } else if (pSize - size < 0) {
            System.out.printf("%nERROR on shrink command: insufficient allocation for Program %d%n", progID);
        } else {

            // Number of pages the current program occupies
            int numP = calcPagesHelper(pSize);

            // The new size in bytes
            int newSize = pSize - size;

            // The new size in terms of pages
            int ans = calcPagesHelper(newSize);

            // The amount to shrink by
            int shrinkBy = numP-ans;

            for (int i = programsArray[progID].prog_usage.size(); shrinkBy > 0; i--) {

                // Temporary PageUsage object
                PageUsage curr = programsArray[progID].prog_usage.get(i);

                int pages = (curr.getEnd()-curr.getStart()) +1;

                if (pages <= shrinkBy) {
                    programsArray[progID].prog_usage.remove(i);
                    programsArray[progID].bytes -= size;
                    putInOrder(curr, freePageList);
                    shrinkBy -= pages;
                } else {
                    putInOrder(new PageUsage(curr.getEnd() - pages, curr.getEnd()), freePageList);
                    shrinkBy = 0;
                }
            }
            System.out.printf("%nProgram %d decreased by %d, new size = %d%n", progID, size, newSize);
        }

    }

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        // 'i' for initiate, 't' for terminate, 'p' for print, 'x' to exit
        char opcode;

        // Program ID
        int progID;

        // Specifies how many bytes are need by the program
        int size;

        // Free page list
        List<PageUsage> freePageList = new List<PageUsage>();
        freePageList.add(1, new PageUsage(0, NUM_PAGES - 1)); // Logical position 1

        // Programs array
        ProgInfo programsArray[] = new ProgInfo[NUM_PROGRAMS];

        // Initializing the programs array
        for (int i = 0; i < programsArray.length; i++) {
            programsArray[i] = new ProgInfo();
            programsArray[i].bytes = -1;
            programsArray[i].prog_usage = new List<PageUsage>();
        }

        while (scan.hasNext()) {

            opcode = scan.next().trim().charAt(0);
            progID = scan.nextInt();
            size = scan.nextInt();
            switch (opcode) {
                case 'i':
                    handleInitiate(programsArray, progID, size, freePageList);
                    break;
                case 't':
                    handleTerminate(programsArray, progID, freePageList);
                    break;
                case 'p':
                    handlePrint(programsArray, progID, freePageList);
                    break;
                case 'x':
                    handleExit(programsArray);
                    break;
                case 'g':
                    handleGrow(programsArray, freePageList, progID, size);
                    break;
                case 's':
                    handleShrink(programsArray, freePageList, progID, size);
                    break;
                default:
                    System.out.println("%nSelection not found%n");
                    break;
            }

        }
    }
}
