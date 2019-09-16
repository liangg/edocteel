/**
 * Leetcode questions (tree, list)
 */

import java.lang.Math;
import java.util.*;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

class ListNode {
    int val;
    ListNode next;
    ListNode(int x) {
        this.val = x;
        this.next = null;
    }
}

/**
 * Q-61 Rotate List
 *
 * Given a linked list, rotate the list to the right by k places, where k is non-negative.
 */
class RotateList {
  public ListNode rotateRight(ListNode head, int k) {
    return null;
  }
}

class AddNumbersList{
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        if (l1 == null && l2 == null)
            return null;
        else if (l1 == null)
            return l2;
        else if (l2 == null)
            return l1;

        int carry = 0;
        ListNode head = null, tail = null;
        for (; l1 != null && l2 != null; l1 = l1.next, l2 = l2.next) {
            int num = l1.val + l2.val + carry;
            ListNode n = new ListNode(num%10);
            carry = num/10;
            if (head == null) {
                head = tail = n;
            } else {
                tail.next = n;
                tail = n;
            }
        }
        ListNode l3 = l1 != null ? l1 : l2;
        for (; l3 != null; l3 = l3.next) {
            int num = l3.val + carry;
            ListNode n = new ListNode(num%10);
            carry = num/10;
            tail.next = n;
            tail = n;
        }
        if (carry > 0) {
            tail.next = new ListNode(carry);
        }
        return head;
    }
}


class PartitionList
{
  public ListNode partition(ListNode head, int x) {
    ListNode p1head = null, p2head = null;
    ListNode p1tail = null, p2tail = null;
    for (ListNode p = head; p != null; p = p.next) {
      if (p.val < x) {
        if (p1tail != null) {
          p1tail.next = p;
          p1tail = p;
        } else {
          p1head = p1tail = p;
        }
      } else {
        if (p2tail != null) {
          p2tail.next = p;
          p2tail = p;
        } else {
          p2head = p2tail = p;
        }
      }
    }
    if (p1tail != null)
      p1tail.next = p2head;
    if (p2tail != null)
      p2tail.next = null;
    return p1head != null ? p1head : p2head;
  }
}

/**
 * Q: Substring i.e. find needle in the haystack
 */
class FindSubstring {
    private static int PRIME = 7;
    private static int P = 3;

    private static int rollingHash(String s, int N) {
        int rhash = 0;
        for (int i = 0; i < N; ++i) {
            rhash = (rhash*P + s.charAt(i)) % PRIME;
        }
        return rhash;
    }

    private static boolean equality(String haystack, int idx, String needle) {
        for (int j = 0; j < needle.length(); ++j) {
            if (haystack.charAt(idx+j) != needle.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    public static int strStr(String haystack, String needle) {
        if (needle == null || haystack == null || haystack.length() == 0 || needle.length() == 0 ||
            needle.length() > haystack.length()) {
            return -1;
        }

        int nlen = needle.length();
        // MP is the P^(N-1) multiple for the first element in the rolling window
        int MP = 1;
        for (int i = 0; i < nlen-1; MP = ((MP*P) % PRIME), ++i);

        int needleHash = rollingHash(needle, nlen);
        int hash = rollingHash(haystack, nlen);
        if (needleHash == hash && equality(haystack, 0, needle)) {
            return 0;
        }

        // to avoid overflow, use modulo at every step
        for (int i = 1; i <= haystack.length() - nlen; ++i) {
            // plus PRIME is to avoid negative result from subtraction
            hash = (hash + PRIME -  haystack.charAt(i-1)*MP % PRIME) % PRIME;
            // shift hash to left by multiply PRIME
            hash = (hash*P % PRIME + haystack.charAt(i+nlen-1)) % PRIME;
            if (hash == needleHash && equality(haystack, i, needle)) {
                return i;
            }
        }
        return -1;
    }

    public static int strStr2(String haystack, String needle) {
        int nlen = needle.length();
        for (int i = 0; i <= haystack.length() - nlen; ++i) {
            int j = 0;
            for (; j < nlen; ++j) {
                if (haystack.charAt(i+j) != needle.charAt(j)) {
                    break;
                }
            }
            if (j == nlen) {
                return i;
            }
        }
        return -1;
    }

    public static void run() {
        System.out.println("------- Find Needle in Haystack ---------");
        System.out.println(strStr("213421", "342"));
        System.out.println(strStr("needtofindneedleinhaystack", "needle"));
        System.out.println(strStr("nneedleinhaystack", "needle"));
    }
}

/**
 * Q: Substrings with Concatenation of all Words
 *
 * You are given a string, S, and a list of words, L, that are all of the same
 * length. Find all starting indices of substring(s) in S that is a concatenation
 * of each word in L exactly once and without any intervening characters. For
 * example, the result for the S and L is [0,9].
 *
 * S: "barfoothefoobarman"
 * L: ["foo", "bar"]
 */
class SubstrsWithConcatWords {
  private static boolean checkConcatenatedWords(final int len, final int size, final int start,
                                         final int nwords, final String[] wordsAt,
                                         HashMap<String, Integer> dict) {
    int wordCount = nwords;
    for (int i = start; i <= len - size && wordCount > 0; i += size, --wordCount) {
      if (wordsAt[i] == null || dict.get(wordsAt[i]) == 0)
        return false;
      int cnt = dict.get(wordsAt[i]);
      dict.put(wordsAt[i], new Integer(cnt-1));
    }
    return wordCount == 0 ? true : false;
  }

  public static List<Integer> findSubstring(String S, String[] L) {
    final int slen = S.length();
    final int wordSize = L[0].length();
    final int nwords = L.length;
    final int concatLength = nwords * wordSize;
    final HashMap<String, Integer> dict = new HashMap<String, Integer>();

    /* build a dictionary with word count */
    for (String s : L) {
      int cnt = 0;
      if (dict.containsKey(s)) {
        cnt = dict.get(s);
      }
      dict.put(s, new Integer(cnt+1));
    }

    /* remember whether a word starts at an subscript index of S */
    final String[] wordsAt = new String[slen];
    for (int i = 0; i <= slen - wordSize; ++i) {
      final String s = S.substring(i, i+wordSize);
      wordsAt[i] = null;
      if (dict.containsKey(s))
        wordsAt[i] = s;
    }

    ArrayList<Integer> result = new ArrayList<Integer>();
    HashMap<String, Integer> dict2 = new HashMap<String, Integer>();
    /* stop early if knowing the suffix is not long enough, otherwise it'd timeout large testcase */
    for (int i = 0; i <= slen - concatLength; ++i) {
      if (wordsAt[i] != null) {
        dict2.putAll(dict);
        int cnt = dict2.get(wordsAt[i]);
        dict2.put(wordsAt[i], new Integer(cnt-1));
        if (checkConcatenatedWords(slen, wordSize, i+wordSize, nwords-1, wordsAt, dict2))
          result.add(new Integer(i));
      }
    }
    System.out.println(S + "->" );
    for (Integer i : result)
      System.out.println(i + " ");
    return result;
  }

  public static void run() {
    System.out.println("--------- Substrings with concatenation of all words --------");
    String[] L = new String[2];
    L[0] = new String("foo");
    L[1] = new String("bar");
    String S = new String("barfoothefoobarman");
    findSubstring(S, L);
  }
}


/**
 * Q-316: Remove Duplicate Letters
 *
 * Given a string which contains only lowercase letters, remove duplicate letters so that every letter
 * appear once and only once. You must make sure your result is the smallest in lexicographical order
 * among all possible results.
 */
class RemoveDuplicateLetters {
    public static String removeDuplicateLetters(String s) {
        int nchars = 0;
        Map<Character, List<Integer>> positions = new HashMap<Character, List<Integer>>();
        // remember positions of each individual letter
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            List<Integer> p = positions.get(c);
            if (p == null) {
                nchars++;
                p = new ArrayList<Integer>();
                positions.put(c, p);
            }
            p.add(c - 'a');
        }

        Set<Character> taken = new HashSet<Character>();
        char result[] = new char[nchars];
        for (int i = 0; i < nchars; ++i) {
            char ch = 0;
            int ch_pos = -1;
            for (Iterator it = positions.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Character, List<Integer>> entry = (Map.Entry) it.next();
                if (taken.contains(entry.getKey()))
                    continue;

                char ech = entry.getKey();
                List<Integer> epos = entry.getValue();
                if (ch_pos == -1) {
                    ch = ech;
                    ch_pos = epos.remove(0);
                } else {
                    if (epos.size() == 1) {
                        if (positions.get(ch).isEmpty() && epos.get(0) < ch_pos) {
                            // restore the position of previous chosen char
                            positions.get(ch).add(ch_pos);
                            ch = ech;
                            ch_pos = epos.remove(0);
                        }
                    } else {
                        for (int p = epos.get(0); !epos.isEmpty() && p < ch_pos; p = epos.get(0)) {
                            epos.remove(0);
                        }
                    }
                }
            }

            taken.add(ch);
            result[i] = ch;
        }

        return String.valueOf(result);
    }
}

/**
 * Q-332: Reconstruct Itinerary
 *
 * Given a list of airline tickets represented by pairs of departure and arrival
 * airports [from, to], reconstruct the itinerary in order. All of the tickets
 * belong to a man who departs from JFK. Thus, the itinerary must begin with JFK.
 */
class ReconstructItinerary {
    private static boolean flyNext(List<String> trip, Map<String, ArrayList<String>> flights,
                            final int numCities) {
        if (trip.size() == numCities)
            return true;

        String curr = trip.get(trip.size()-1);
        if (!flights.containsKey(curr) || flights.get(curr).size() == 0)
            return false;

        ArrayList<String> orig = flights.get(curr);
        ArrayList<String> triedDests = new ArrayList<String>();
        ArrayList<String> destinations = new ArrayList<String>(orig);
        while (destinations.size() > 0) {
            String next = destinations.remove(0);
            trip.add(next);
            ArrayList<String> newDests = new ArrayList<String>(triedDests);
            newDests.addAll(destinations);
            flights.put(curr, newDests);
            if (flyNext(trip, flights, numCities))
                return true;

            trip.remove(trip.size() - 1);
            triedDests.add(next);
        }

        flights.put(curr, orig);
        return false;
    }

    private static List<String> findItinerary(String[][] tickets) {
        int numCities = tickets.length + 1;
        Map<String, ArrayList<String>> flightsMap = new HashMap<String, ArrayList<String>>();
        for (String[] t : tickets) {
            if (!flightsMap.containsKey(t[0])) {
                flightsMap.put(t[0], new ArrayList<String>());
            }
            flightsMap.get(t[0]).add(t[1]);
        }

        // sort destinations of the same source city
        for (Iterator it = flightsMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, ArrayList<String>> entry = (Map.Entry) it.next();
            Collections.sort(entry.getValue());
        }

        // assume there is at lease one valid trip
        List<String> trip = new ArrayList<String>();
        trip.add("JFK");
        flyNext(trip, flightsMap, numCities);

        for (String c : trip)
            System.out.print(c + " ");
        System.out.println();
        return trip;
    }

    private static void dfs(String from, Map<String, ArrayList<String>> flights, List<String> result) {
        // I first used SortedSet in flights, thinking it would provide sorting; however, remove() would remove all
        // duplicate city strings
        while (flights.get(from) != null && flights.get(from).size() > 0) {
            String to = flights.get(from).get(0);
            flights.get(from).remove(0);
            dfs(to, flights, result);
        }
        result.add(from);
    }

    // Implementation #2 use recursive DFS
    private static List<String> recreateItinerary(String[][] tickets) {
        ArrayList<String> result = new ArrayList<String>();
        if (tickets.length == 0)
            return result;
        // build adjacency sorted list
        Map<String, ArrayList<String>> flights = new HashMap<String, ArrayList<String>>();
        for (String[] t : tickets) {
            if (!flights.containsKey(t[0]))
                flights.put(t[0], new ArrayList<String>());
            flights.get(t[0]).add(t[1]);
        }
        // sort destinations,
        for (Map.Entry<String, ArrayList<String>> e : flights.entrySet()) {
            Collections.sort(e.getValue());
        }

        // do DFS from the start JFK
        dfs("JFK", flights, result);
        Collections.reverse(result);
        System.out.println(Arrays.toString(result.toArray()));
        return result;
    }

    public static void run() {
        System.out.println("------------ Reconstruct Itinerary ------------");
        String[][] tickets = {{"JFK","SFO"},{"JFK","ATL"},{"SFO","ATL"},{"ATL","JFK"},{"ATL","SFO"}};
        recreateItinerary(tickets);

        String[][] tickets1 = {{"ATL","TKO"},{"SFO","JFK"},{"JFK","SFO"},{"JFK","ATL"}};
        recreateItinerary(tickets1);

        String[][] tickets2 = {{"ATL","TKO"},{"SFO","JFK"},{"JFK","SFO"},{"JFK","ATL"},{"ATL","JML"},
                {"JML","ATL"}};
        recreateItinerary(tickets2);

        String[][] tickets4 = {{"EZE","AXA"},{"TIA","ANU"},{"ANU","JFK"},{"JFK","ANU"},{"ANU","EZE"},{"TIA","ANU"},
                {"AXA","TIA"},{"TIA","JFK"},{"ANU","TIA"},{"JFK","TIA"}};
        recreateItinerary(tickets4); // ["JFK","ANU","EZE","AXA","TIA","ANU","JFK","TIA","ANU","TIA","JFK"]

        String[][] tickets3 = {{"CBR","JFK"},{"TIA","EZE"},{"AUA","TIA"},{"JFK","EZE"},
                {"BNE","CBR"},{"JFK","CBR"},{"CBR","AUA"},{"EZE","HBA"},{"AXA","ANU"},{"BNE","EZE"},
                {"AXA","EZE"},{"AUA","ADL"},{"OOL","JFK"},{"BNE","AXA"},{"OOL","EZE"},{"EZE","ADL"},
                {"TIA","BNE"},{"EZE","TIA"},{"JFK","AUA"},{"AUA","EZE"},{"ANU","ADL"},{"TIA","BNE"},
                {"EZE","OOL"},{"ANU","BNE"},{"EZE","ANU"},{"ANU","AUA"},{"BNE","ANU"},{"CNS","JFK"},
                {"TIA","ADL"},{"ADL","AXA"},{"JFK","OOL"},{"AUA","ADL"},{"ADL","TIA"},{"ADL","ANU"},
                {"ADL","JFK"},{"BNE","EZE"},{"ANU","BNE"},{"JFK","BNE"},{"EZE", "AUA"}, {"EZE","AXA"},
                {"AUA","TIA"},{"ADL","CNS"},{"AXA","AUA"}};
        findItinerary(tickets3);
        recreateItinerary(tickets3);
    }
}


/**
 * Q-378 K-th Smallest Element in Sorted Matrix
 *
 * Given a n x n matrix where each of the rows and columns are sorted in ascending order, find the kth smallest
 * element in the matrix. Note that it is the kth smallest element in the sorted order, not the kth distinct element.
 */
class KthSmallestInSortedMatrix {
    private static class Cell {
        public int x;
        public int y;
        Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static int kthSmallest(final int[][] matrix, int k) {
        boolean enqueued[][] = new boolean[matrix.length][matrix[0].length];
        PriorityQueue<Cell> priorityQueue =  new PriorityQueue<Cell>(k, new Comparator<Cell>() {
            @Override
            public int compare(Cell o1, Cell o2) {
                if (matrix[o1.x][o1.y] == matrix[o2.x][o2.y])
                    return 0;
                else if (matrix[o1.x][o1.y] < matrix[o2.x][o2.y])
                    return -1;
                return 1;
            }
        });
        priorityQueue.add(new Cell(0,0));
        enqueued[0][0] = true;

        int elem = matrix[0][0];
        for (int i = 0; i < k; ++i) {
            Cell top = priorityQueue.poll();
            elem = matrix[top.x][top.y];
            if (top.x < matrix.length-1 && !enqueued[top.x+1][top.y]) {
                priorityQueue.add(new Cell(top.x+1, top.y));
                enqueued[top.x+1][top.y] = true;
            }
            if (top.y < matrix[0].length-1 && !enqueued[top.x][top.y+1]) {
                priorityQueue.add(new Cell(top.x, top.y+1));
                enqueued[top.x][top.y+1] = true;
            }
        }
        return elem;
    }

    public static void test() {
        System.out.println("----- Kth Smallest Element in Sorted Matrix ----");
        int m0[][] = {{1,5,9},{10,11,13},{12,13,15}};
        System.out.println(kthSmallest(m0, 8));
        System.out.println(kthSmallest(m0, 5));
        int m1[][] = {{1,3,5},{6,7,12},{11,14,14}};
        System.out.println(kthSmallest(m1, 6));
    }
}

/** Q-382 Linked List Random Node (Reservoir Sampling) */
class LinkedListRandomNode {
    ListNode head;
    Random random;

    public LinkedListRandomNode(ListNode head) {
        this.head = head;
        this.random = new Random();
    }

    /** Returns a random node's value. */
    public int getRandom() {
        int count = 0, result = -1;
        ListNode n = head;
        while (n != null) {
            count++;
            if (random.nextInt() % count == 0)
                result = n.val;
            n = n.next;
        }
        return result;
    }
}

/** Q-384 Shuffle an Array */
class ShuffleArray {
    int[] orig;

    public ShuffleArray(int[] nums) {
        orig = nums;
    }

    /** Resets the array to its original configuration and return it. */
    public int[] reset() {
        return orig;
    }

    /** Returns a random shuffling of the array. */
    public int[] shuffle() {
        int[] result = Arrays.copyOf(orig, orig.length);
        Random random = new Random();
        for (int i = 0; i < orig.length; ++i) {
            int r = i + random.nextInt(orig.length - i);
            int tmp = result[i];
            result[i] = result[r];
            result[r] = tmp;
        }
        System.out.println(Arrays.toString(result));
        return result;
    }

    public static void test() {
        System.out.println("Q-384 Shuffle an Array");
        int[] a1 = {1,2,3,4,5,6,7,8,9,10};
        ShuffleArray sa = new ShuffleArray(a1);
        sa.shuffle();
    }
}

/** Q-386 Lexicographical Numbers */
class LexicographicalNumbers {
    public static void helper(int d, int n, List<Integer> result) {
        if (d > n) return;
        result.add(d);
        for (int i = 0; i <= 9; ++i) {
            if (d * 10 + i > n)
                break;
            helper(d*10+i, n, result);
        }
    }

    public static List<Integer> lexicalOrder(int n) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 1; i <= 9; ++i) {
            helper(i, n, result);
        }
        System.out.println(Arrays.toString(result.toArray()));
        return result;
    }

    public static void test() {
        System.out.println("Q-386 Lexicographical Numbers");
        lexicalOrder(126);
    }
}

/**
 * Q-395 Longest Substring with At Least K Repeating Characters
 *
 * Find the length of the longest substring T of a given string (consists of lowercase letters only)
 * such that every character in T appears no less than k times.
 */
class LongestSubstingWithKRepeatingChars {
    public static int longestSubstring(String s, int k) {
        if (s.length() < k)
            return 0;
        char[] str = s.toCharArray();
        int[] counts = new int[26];
        for (char c : str)
            counts[c-'a'] += 1;

        boolean found = false;
        int i = 0;
        char cc = 'a';
        for (; i < 26; ++i) {
            if (counts[i] != 0 && counts[i] < k) {
                cc = (char)('a' + i);
                found = true;
                break;
            }
        }

        if (!found)
            return s.length();

        int first = s.indexOf(cc);
        int last = s.lastIndexOf(cc);
        int len1 = first > 0 ? longestSubstring(s.substring(0,first), k) : 0;
        int len2 = (last - first) -1 >= k ? longestSubstring(s.substring(first+1, last), k) : 0;
        int len3 = last < s.length()-1 ? longestSubstring(s.substring(last+1), k) : 0;
        int max = len1 > len2 ? len1 : len2;
        return max > len3 ? max : len3;
    }

    public static void test() {
        System.out.println("Q-395 Longest Substring with K Repeated Chars");
        System.out.println(longestSubstring("ababbc", 2));
        System.out.println(longestSubstring("ababbcababd", 2));
        System.out.println(longestSubstring("baaabcb", 3));
    }
}

/**
 * Q-398 Random Pick Index (Reservoir Sampling)
 *
 * Given an array of integers with possible duplicates, randomly output the index of a given target number. You can
 * assume that the given target number must exist in the array.
 */
class RandomPickIndex {
    int[] nums;
    Random random;

    public RandomPickIndex(int[] nums) {
        this.nums = nums;
        this.random = new Random();
    }

    public int pick(int target) {
        int count = 0, result = -1;
        for (int i = 0; i < this.nums.length; ++i) {
            if (this.nums[i] != target)
                continue;
            count += 1;
            if (random.nextInt() % count == 0)
                result = i;
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-398 Random Pick Index");
        int[] a1 = new int[]{1,3,2,3,3};
        RandomPickIndex rpi = new RandomPickIndex(a1);
        System.out.println(rpi.pick(2));
        System.out.println(rpi.pick(3));
        System.out.println(rpi.pick(3));
        System.out.println(rpi.pick(3));
    }
}

/**
 * Q-406 Queue Reconstruction by Height
 *
 * Suppose you have a random list of people standing in a queue. Each person is described by a pair of integers (h, k),
 * where h is the height of the person and k is the number of people in front of this person who have a height greater
 * than or equal to h. Write an algorithm to reconstruct the queue.
 */
class QueueReconstructionByHeight {
    public static int[][] reconstructQueue(int[][] people) {
        Arrays.sort(people, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) { // sort h descendingly
                if (o1[0] < o2[0]) return 1;
                else if (o1[0] > o2[0]) return -1;
                else if (o1[1] < o2[1]) return -1; // sort k ascendingly
                else return 1;
            }
        });
        //for (int i = 0; i < people.length; ++i)
        //    System.out.print(Arrays.toString(people[i]));
        for (int i = 0; i < people.length; ++i) {
            int npred = 0;
            for (int j = 0; j < i; ++j) {
                // look for insert index, ie. after K people of higher or equal height
                int insert = -1;
                if (people[i][1] == 0)
                    insert = 0;
                else if (people[j][0] >= people[i][0]) {
                    npred += 1;
                    if (npred == people[i][1])
                        insert = j + 1;
                }
                if (insert != -1) {
                    int[] old = people[i];
                    for (int k = i; k > insert; --k)
                        people[k] = people[k - 1];
                    people[insert] = old;
                    break;
                }
            }
        }
        //System.out.println("");
        //for (int i = 0; i < people.length; ++i)
        //    System.out.print(Arrays.toString(people[i]));
        return people;
    }

    public static void test() {
        System.out.println("Q-406 Queue Reconstruction By Height");
        int[][] p = new int[][]{{5,0},{7,0},{5,2},{6,1},{4,4},{7,1}};
        reconstructQueue(p);
    }
}

/** Q-419 Battleships in a Board */
class BattleshipsInBoard {
    public static int countBattleships(char[][] board) {
        int result = 0;
        if (board.length == 0 || board[0].length == 0)
            return result;
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[0].length; ++j) {
                // since no invalid input (i.e. neighbouring ships, look for first X
                if (board[i][j] == 'X') {
                    if ((i-1 < 0 || board[i-1][j] != 'X') && (j-1 < 0 || board[i][j-1] != 'X'))
                        result += 1;
                }
            }
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-419 Battleships in a Board");
        char[][] b1 = {{'X','.','.','.','X'},{'.','.','.','.','X'},{'X','.','X','.','X'},{'X','.','.','.','X'}}; // 4
        char[][] b2 = {{'X','X','X','.','X'},{'.','.','.','.','X'},{'X','.','X','.','.'},{'X','.','.','X','X'}}; // 5
        System.out.println(countBattleships(b1));
        System.out.println(countBattleships(b2));
    }
}

/**
 * Q-424 Longest Repeating Character Replacement (sliding window)
 */
class LongestRepeatingCharacterReplacement {
    public static int characterReplacement(String s, int k) {
        int result = 0, maxCount = 0, start = 0;
        int[] counts = new int[26];
        for (int i = 0; i < s.length(); ++i) {
            counts[s.charAt(i) - 'A'] += 1;
            if (counts[s.charAt(i) - 'A'] > maxCount)
                maxCount = counts[s.charAt(i) - 'A'];
            // shift sliding window and recount max count char
            if (i - start + 1 - maxCount > k) {
                counts[s.charAt(start++) - 'A'] -= 1;
                maxCount = 0;
                for (int j = 0; j < 26; ++j)
                    if (counts[j] > maxCount)
                        maxCount = counts[j];
            }
            if (i - start + 1 > result)
                result = i-start+1;
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-424 Longest Repeating Character Replacement");
        System.out.println(characterReplacement("AABABBA", 1)); // 4
        System.out.println(characterReplacement("ABABCCDBC", 2)); // 5
    }
}

/**
 * Q-433 Minimum Genetic Mutations
 *
 * A gene string can be represented by an 8-character long string, with choices from "A", "C", "G", "T". Suppose we need
 * to investigate about a mutation (mutation from "start" to "end"), where ONE mutation is defined as ONE single
 * character changed in the gene string. For example, "AACCGGTT" -> "AACCGGTA" is 1 mutation. Also, there is a given
 * gene "bank", which records all the valid gene mutations. A gene must be in the bank to make it a valid gene string.
 * Now, given 3 things - start, end, bank, your task is to determine what is the minimum number of mutations needed to
 * mutate from "start" to "end". If there is no such a mutation, return -1.
 */
class MinimumGeneticMutations {
    static class Gene {
        public String geneStr;
        public int distance;
        public Gene(String g, int d) {
            this.geneStr = g;
            this.distance = d;
        }
    }

    public static int minMutation(String start, String end, String[] bank) {
        char[] geneChars = new char[]{'A', 'C', 'G', 'T'};
        Set<String> dict = new HashSet<String>();
        dict.addAll(Arrays.asList(bank));
        Set<String> visited = new HashSet<String>();

        PriorityQueue<Gene> priQueue =  new PriorityQueue<Gene>(1, new Comparator<Gene>() {
            @Override
            public int compare(Gene o1, Gene o2) {
                if (o1.distance == o2.distance) return 0;
                else if (o1.distance < o2.distance) return -1;
                return 1;
            }
        });
        priQueue.add(new Gene(start, 0));

        // shortest path
        while (!priQueue.isEmpty()) {
            Gene g = priQueue.poll();
            visited.add(g.geneStr);
            for (int i = 0; i < end.length(); ++i) {
                for (char c : geneChars) {
                    if (g.geneStr.charAt(i) == c) continue;
                    char[] gchars = g.geneStr.toCharArray();
                    gchars[i] = c;
                    String gs = new String(gchars);
                    if (dict.contains(gs)) {
                        // the mutated end must be valid gene string in the bank
                        if (gs.equals(end))
                            return g.distance + 1;
                        if (!visited.contains(gs))
                            priQueue.add(new Gene(gs, g.distance + 1));
                    }
                }

            }
        }

        return -1;
    }

    public static void test() {
        System.out.println("Q-433 Minimum Genetic Mutations");
        System.out.println(minMutation("AACCGGTT", "AAACGGTA", new String[]{"AACCGGTA","AACCGCTA","AAACGGTA"})); // 2
        System.out.println(minMutation("AAAAACCC","AACCCCCC", new String[]{"AAAACCCC", "AAACCCCC", "AACCCCCC"})); // 3
        System.out.println(minMutation("AACCGGTT", "AACCGGTA", new String[]{})); // -1
        System.out.println(minMutation("AACCGGTT", "AAACGGTA", new String[]{"AACCGATT","AACCGATA","AAACGATA","AAACGGTA"})); // 4
    }
}


/**
 * Q-452 Minimum Arrows To Burst Balloons
 */
class MinimumArrowsToBurstBalloons {
    public static int findMinArrowShots(int[][] points) {
        if (points.length == 0)
            return 0;
        Arrays.sort(points, new Comparator<int[]>() {
            @Override
            public int compare(int[] a1, int[] a2) {
                if (a1[0] == a2[0] && a1[1] == a2[1])
                    return 0;
                else if (a1[0] < a2[0] || (a1[0] == a2[0] && a1[1] < a2[1]))
                   return -1;
                else
                    return 1;
            }
        });
        int result = 1;
        int end = points[0][1];
        // similar to finding the maximum number of overlapped intervals
        for (int i = 1; i < points.length; ++i) {
            if (points[i][0] <= end) {
                // greedy - adjust the end to cover overlapped balloons at a point
                if (points[i][1] < end)
                    end = points[i][1];
            } else {
                result += 1;
                end = points[i][1];
            }
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-452 Minimum Arrows To Burst Balloons");
        int[][] a1 = {{10,16},{3,8},{1,6},{7,12},{2,7}}; // 2
        int[][] a2 = {{1,2},{2,3},{3,4},{4,5}}; // 2
        int[][] a3 = {{9,12},{1,10},{4,11},{8,12},{3,9},{6,9},{6,7}}; // 2
        System.out.println(MinimumArrowsToBurstBalloons.findMinArrowShots(a1));
    }
}

/**
 * Q-456 132 Pattern (Stack)
 */
class OneThreeTwoPattern {
    public static boolean find132pattern(int[] nums) {
        int N = nums.length;
        if (nums.length < 3)
            return false;
        int third = Integer.MIN_VALUE;
        Stack<Integer> stack = new Stack<Integer>();
        for (int i = nums.length-1; i >= 0; --i) {
            int curr = nums[i];
            // the stack has an element that is larger than third
            if (curr < third) return true;
            while (!stack.empty() && curr > stack.peek().intValue())
                third = stack.pop().intValue();
            stack.push(curr);
        }
        return false;
    }

    public static void test() {
        System.out.println("Q-456 132 Pattern");
        int[] a1 = {3,1,4,2,0};
        int[] a3 = {7,8,5,10,4}; // false
        int[] a4 = {-1, 3, 2, 0}; // true
        int[] a5 = {-2,1,2,-2,1,2}; // true
        //System.out.println(find132pattern(a1));
        //System.out.println(find132pattern(a3));
        System.out.println(find132pattern(a5));
    }
}

/**
 * Q-467 Unique Substrings in Wraparound String
 *
 * Consider the string s to be the infinite wraparound string of "abcdefghijklmnopqrstuvwxyz", so s will look like this:
 * "...zabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcd....". Now we have another string p. Your job is to find
 * out how many unique non-empty substrings of p are present in s. In particular, your input is the string p and you
 * need to output the number of different non-empty substrings of p in the string s.
 */
class UniqueSubstringsInWraparoundString {
    // Check every continuous substring will not pass large test case, so instead find the longest continuous
    // substring and count the number of substrings L*(L+1)/2. However, for the unique requirement, it should
    // exclude dup substring ending at the current char of length L. So, it can use HashSet<char+L> to track
    // dups, and exclude dups in counting.
    //
    // The smart solution is to count the length of continuous substring ending at each char, and that count
    // includes every continuous substring ending that that char. The result is the sum of these counts.
    public static int findSubstringInWraproundString(String p) {
        if (p == null || p.isEmpty())
            return 0;
        int[] memo = new int[26];
        int len = 1;
        memo[p.charAt(0)-'a'] = 1;
        for (int i = 1; i < p.length(); ++i) {
            // continuous substring or wraparound substring
            if (p.charAt(i) - p.charAt(i-1) == 1 || p.charAt(i-1) - p.charAt(i) == 25) {
                len++;
                if (memo[p.charAt(i) - 'a'] < len)
                    memo[p.charAt(i) - 'a'] = len;
            } else {
                len = 1;
                if (memo[p.charAt(i)-'a'] == 0)
                    memo[p.charAt(i)-'a'] = 1;
            }
        }
        if (memo[p.charAt(p.length()-1) - 'a'] < len)
            memo[p.charAt(p.length()-1) - 'a'] = len;
        System.out.println(Arrays.toString(memo));
        int result = 0;
        for (int i = 0; i < 26; ++i)
            result += memo[i];
        return result;
    }

    public static void test() {
        System.out.println("Q-467 Unique Substring in Wraparound String");
        System.out.println(findSubstringInWraproundString("cac")); // 2 unique substrings "c" "a"
        System.out.println(findSubstringInWraproundString("ace")); // 3
        System.out.println(findSubstringInWraproundString("zab")); // 6
        System.out.println(findSubstringInWraproundString("zacd")); // 6
        System.out.println(findSubstringInWraproundString("abza")); // 5
        System.out.println(findSubstringInWraproundString("zabyza")); // 9
        System.out.println(findSubstringInWraproundString("zaba")); // 6
    }
}

/** Q-539 Minimum Time Difference */
class MinimumTimeDifference {
    private static int numberify(String s) {
        String[] time = s.split(":");
        return Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]);
    }

    public static int findMinDifference(List<String> timePoints) {
        Collections.sort(timePoints);
        int diff = 24*24*60;
        int t1 = numberify(timePoints.get(0));
        for (int i = 1; i < timePoints.size(); ++i) {
            int t2 = numberify(timePoints.get(i));
            if (t2-t1 < diff)
                diff = t2-t1;
            t1 = t2;
        }
        // also check wrap-around time points
        t1 = numberify(timePoints.get(0));
        int tn = numberify(timePoints.get(timePoints.size()-1));
        if (24*60 - tn + t1 < diff)
            diff = 24*60 - tn + t1;
        return diff;
    }

    public static void test() {
        System.out.println("Q-539 Minimum Time Difference");
        String[] a1 = {"24:00","23:50","02:15","03:59","04:00"};
        String[] a2 = {"23:59","00:00"};
        System.out.println(findMinDifference(Arrays.asList(a2)));
    }
}

/**
 * Q-542 01 Matrix
 *
 * Given a matrix consists of 0 and 1, find the distance of the nearest 0 for each cell. The distance between two
 * adjacent cells is 1.
 */
class ZeroOneMatrix {
    public static int[][] updateMatrix(int[][] matrix) {
        int[][] M = new int[matrix.length][matrix[0].length];
        // initialize each cell distance to max possible value
        for (int i = 0; i < matrix.length; ++i)
            for (int j = 0; j < matrix[0].length; ++j)
                M[i][j] = matrix.length + matrix[0].length;
        for (int i = 0; i < matrix.length; ++i)
            System.out.println(Arrays.toString(M[i]));
        // scan top-down left-right
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[0].length; ++j) {
                if (matrix[i][j] == 0)
                    M[i][j] = 0;
                else {
                    if (i > 0) // check above
                        M[i][j] = M[i-1][j] + 1;
                    if (j > 0 && M[i][j-1] + 1 < M[i][j]) // check left
                        M[i][j] = M[i][j-1] + 1;
                }
            }
        }
        // scan bottom-up right-left
        for (int i = matrix.length-1; i >= 0; --i) {
            for (int j = matrix[0].length-1; j >= 0; --j) {
                if (matrix[i][j] == 0)
                    M[i][j] = 0;
                else {
                    if (i < matrix.length-1 && M[i+1][j] + 1 < M[i][j]) // check below
                        M[i][j] = M[i+1][j] + 1;
                    if (j < matrix[0].length-1 && M[i][j+1] + 1 < M[i][j]) // check right
                        M[i][j] = M[i][j+1] + 1;
                }
            }
        }
        for (int i = 0; i < matrix.length; ++i)
            System.out.println(Arrays.toString(M[i]));
        return M;
    }

    public static void test() {
        System.out.println("Q-542 01 Matrix");
        int[][] m1 = {{0,0,0}, {0,1,0}, {1,1,1}};
        int[][] m2 = {{0,1,0}, {0,1,0}, {0,1,0}, {0,1,0}, {0,1,0}};
        int[][] m3 = {{1, 1, 0, 0, 1, 0, 0, 1, 1, 0}, {1, 0, 0, 1, 0, 1, 1, 1, 1, 1}, {1, 1, 1, 0, 0, 1, 1, 1, 1, 0},
                      {0, 1, 1, 1, 0, 1, 1, 1, 1, 1}, {0, 0, 1, 1, 1, 1, 1, 1, 1, 0}, {1, 1, 1, 1, 1, 1, 0, 1, 1, 1},
                      {0, 1, 1, 1, 1, 1, 1, 0, 0, 1}, {1, 1, 1, 1, 1, 0, 0, 1, 1, 1}, {0, 1, 0, 1, 1, 0, 1, 1, 1, 1},
                      {1, 1, 1, 0, 1, 0, 1, 1, 1, 1}};
        updateMatrix(m3);
    }
}

/** Q-547 Friend Circles (Union Find) */
class FriendCircles {
    private int root(int[] union, int n) {
        int r = n;
        while (union[r] != r) r = union[r];
        return r;
    }

    // Union-find
    public int findCircleNum(int[][] M) {
        int N = M.length;
        if (N == 0) return 0;
        int[] union = new int[N];
        for (int i = 0; i < N; ++i) union[i] = i;
        int circles = N;
        for (int i = 0; i < N; ++i) {
            for (int j = i+1; j < N; ++j) {
                if (M[i][j] == 1) {
                    int ri = root(union, i);
                    int rj = root(union, j);
                    if (ri != rj) {
                        union[rj] = ri;
                        circles--;
                    }
                }
            }
        }
        return circles;
    }
}

/**
 * Q-622 Design Circular Queue (thread unsafe, use count to simplify code)
 */
class MyCircularQueue {
    private int head = 0, tail = -1, count = 0;
    private final int capacity;
    private final int[] queue;

    /** Initialize your data structure here. Set the size of the queue to be k. */
    public MyCircularQueue(int k) {
        capacity = k;
        queue = new int[k];
    }

    /** Insert an element into the circular queue. Return true if the operation is successful. */
    public boolean enQueue(int value) {
        if (isFull()) return false;
        tail = (tail+1) % capacity;
        queue[tail] = value;
        count++;
        return true;
    }

    /** Delete an element from the circular queue. Return true if the operation is successful. */
    public boolean deQueue() {
        if (isEmpty()) return false;
        head = (head+1) % capacity;
        count--;
        return true;
    }

    /** Get the front item from the queue. */
    public int Front() {
        return isEmpty() ? -1 : queue[head];
    }

    /** Get the last item from the queue. */
    public int Rear() {
        return isEmpty() ? -1 : queue[tail];
    }

    /** Checks whether the circular queue is empty or not. */
    public boolean isEmpty() {
        return count == 0;
    }

    /** Checks whether the circular queue is full or not. */
    public boolean isFull() {
        return count == capacity;
    }

    public static void test() {
        System.out.println("Q-622 Design Circular Queue");
        MyCircularQueue cq = new MyCircularQueue(3);
        System.out.println(cq.enQueue(1));
        System.out.println(cq.enQueue(2));
        System.out.println(cq.enQueue(3));
        System.out.println(cq.enQueue(4)); // false
        System.out.println(cq.Rear()); // 3
        System.out.println(cq.isFull()); // true
        System.out.println(cq.deQueue()); // true
        System.out.println(cq.enQueue(4)); // true
        System.out.println(cq.Rear()); // 4
    }
}

/**
 * Q-641 Design Circular Dequeue i.e double-ended queue
 */
class MyCircularDeque {
    private int head = 0, tail = -1, count = 0;
    private final int capacity;
    private final int[] queue;

    /** Initialize your data structure here. Set the size of the deque to be k. */
    public MyCircularDeque(int k) {
        capacity = k;
        queue = new int[k];
    }

    /** Adds an item at the front of Deque. Return true if the operation is successful. */
    public boolean insertFront(int value) {
        if (isFull()) return false;
        if (isEmpty()) return insertLast(value);
        head = head == 0 ? capacity-1 : head-1;
        queue[head] = value;
        count++;
        return true;
    }

    /** Adds an item at the rear of Deque. Return true if the operation is successful. */
    public boolean insertLast(int value) {
        if (isFull()) return false;
        tail = (tail+1) % capacity;
        queue[tail] = value;
        count++;
        return true;
    }

    /** Deletes an item from the front of Deque. Return true if the operation is successful. */
    public boolean deleteFront() {
        if (isEmpty()) return false;
        head = (head+1) % capacity;
        count--;
        return true;
    }

    /** Deletes an item from the rear of Deque. Return true if the operation is successful. */
    public boolean deleteLast() {
        if (isEmpty()) return false;
        tail = tail == 0 ? capacity-1 : tail-1;
        count--;
        return true;
    }

    /** Get the front item from the deque. */
    public int getFront() {
        return isEmpty() ? -1 : queue[head];
    }

    /** Get the last item from the deque. */
    public int getRear() {
        return isEmpty() ? -1 : queue[tail];
    }

    /** Checks whether the circular deque is empty or not. */
    public boolean isEmpty() {
        return count == 0;
    }

    /** Checks whether the circular deque is full or not. */
    public boolean isFull() {
        return count == capacity;
    }

    public static void test() {
        System.out.println("Q-641 Design Circular Deque (Double-ended Queue)");
        MyCircularDeque cq = new MyCircularDeque(3);
        System.out.println(cq.insertFront(9));
        System.out.println(cq.getRear()); // 9
        System.out.println(cq.insertFront(9));
        System.out.println(cq.getRear()); // 9
        System.out.println(cq.insertLast(5)); // true
        System.out.println(cq.getFront()); // 9
        System.out.println(cq.getRear()); // 5
        System.out.println(cq.getFront()); // 9
        System.out.println(cq.insertLast(8)); // false
        System.out.println(cq.deleteLast()); // true
        System.out.println(cq.getFront()); // 9
    }
}

/**
 * Q-646 Maximum Length of Pair Chain (Sort)
 *
 * You are given n pairs of numbers. In every pair, the first number is always smaller than the second number. Now, we
 * define a pair (c, d) can follow another pair (a, b) if and only if b < c. Chain of pairs can be formed in this
 * fashion. Given a set of pairs, find the length longest chain which can be formed. You needn't use up all the given
 * pairs. You can select pairs in any order.
 */
class MaximumLengthOfPairChain {
    public static int findLongestChain(int[][] pairs) {
        Arrays.sort(pairs, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                // if sort by total order, it becomes LIS with O(N^2) running time
                //if (o1[0] == o2[0] && o1[1] == o2[1]) return 0;
                //if (o1[1] < o2[0] || (o1[0] == o2[0] && o1[1] < o2[1])) return -1;
                //return 1;
                return o1[1] < o2[1] ? -1 : 1;
            }
        });
        for (int[] a : pairs) System.out.println(Arrays.toString(a));
        int result = 1, last = 0;
        for (int i = 1; i < pairs.length; ++i) {
            if (pairs[i][0] > pairs[last][1]) {
                result += 1;
                last = i;
            }
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-646 Maximum Length og Pair Chain");
        int[][] a = {{3,4},{2,3},{4,5}}; // 2
        int[][] a1 = {{-6,9},{1,6},{8,10},{-1,4},{-6,-2},{-9,8},{-5,3},{0,3}}; // 3
        System.out.println(findLongestChain(a));
        System.out.println(findLongestChain(a1));
    }
}

/**
 * Q-648 Replace Words
 *
 * In English, we have a concept called root, which can be followed by some other words to form another longer word -
 * let's call this word successor. For example, the root an, followed by other, which can form another word another.
 * Now, given a dictionary consisting of many roots and a sentence. You need to replace all the successor in the
 * sentence with the root forming it. If a successor has many roots can form it, replace it with the root with the
 * shortest length. You need to output the sentence after the replacement.
 */
class ReplaceWords {
    public static String replaceWords(List<String> dict, String sentence) {
        Map<Character, ArrayList<String>> roots = new HashMap<Character, ArrayList<String>>(26);
        for (String s : dict) {
            char c = s.charAt(0);
            ArrayList<String> l = roots.containsKey(c) ? roots.get(c) : new ArrayList<String>();
            l.add(s);
            roots.put(c, l);
        }
        for (Map.Entry<Character, ArrayList<String>> e : roots.entrySet())
            Collections.sort(e.getValue());

        StringBuilder result = new StringBuilder();
        String[] words = sentence.split(" ");
        for (String s : words) {
            String replace = s;
            if (roots.containsKey(s.charAt(0))) {
                for (String r : roots.get(s.charAt(0))) {
                    if (s.startsWith(r)) {
                        replace = r;
                        break;
                    }
                }
            }
            result.append(replace).append(" ");
        }
        return result.toString().trim();
    }

    public static void test() {
        System.out.println("Q-648 Replace Words");
        String[] rootDict = {"cat", "bat", "rat"};
        System.out.println(replaceWords(Arrays.asList(rootDict), "the cattle was rattled by the battery"));
    }
}

/**
 * Q-658 Find K Closest Elements (2 pointers)
 */
class FindKClosestElements {
    public static List<Integer> findClosestElements(int[] arr, int k, int x) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int l = 0, r = arr.length-1;
        // If use binary search to find x first, then use 2 pointers, we have O(lgN + k)
        while (l <= r) {
            if (r - l + 1 == k) {
                for (int i = l; i <= r; ++i)
                    result.add(arr[i]);
                break;
            }
            if (Math.abs(x - arr[l]) <= Math.abs(arr[r] - x))
                r--;
            else
                l++;
        }
        System.out.println(Arrays.toString(result.toArray()));
        return result;
    }

    public static void test() {
        System.out.println("Q-658 Find K Closest Elements");
        findClosestElements(new int[]{1,2,3,4,5}, 4, 3); // (1,2,3,4)
        findClosestElements(new int[]{1,2,3,5,6,8,9,12}, 4, 4); // (2,3,5,6)
    }
}

/**
 * Q-676 Implement Magic Dictionary (data structure, hashmap)
 */
class MagicDictionary {
    final HashMap<Integer, List<String>> dict = new HashMap<Integer, List<String>>();

    /** Initialize your data structure here. */
    public MagicDictionary() {
    }

    /** Build a dictionary through a list of words */
    public void buildDict(String[] dict) {
        for (String word : dict) {
            int len = word.length();
            List<String> list = this.dict.containsKey(len) ? this.dict.get(len) : new ArrayList<String>();
            list.add(word);
            this.dict.put(len, list);
        }
    }

    /** Returns if there is any word in the trie that equals to the given word after modifying exactly one character */
    public boolean search(String word) {
        List<String> list = dict.containsKey(word.length()) ? dict.get(word.length()) : null;
        if (list == null)
            return false;
        for (int i = 0; i < list.size(); ++i) {
            int mismatched = 0;
            String w = list.get(i);
            if (!w.equals(word)) {
                int j = 0;
                for (; j < word.length(); ++j) {
                    if (w.charAt(j) != word.charAt(j)) {
                        if (mismatched == 0)
                            mismatched += 1;
                        else
                            break;
                    }
                }
                if (j == word.length() && mismatched == 1)
                    return true;
            }
        }
        return false;
    }

    public static void test() {
        System.out.println("Q-676 Magic Dictionary");
        MagicDictionary md = new MagicDictionary();
        md.buildDict(new String[]{"leetcode", "hello", "supreme"});
        System.out.println(md.search("leetcoded")); // false
        System.out.println(md.search("leatcode")); // true
        System.out.println(md.search("heelo")); // true
        System.out.println(md.search("hlelo")); // false
        System.out.println(md.search("supreme")); // false
    }
}

/**
 * Q-677 Map Sum Pairs
 *
 * Implement a MapSum class with insert, and sum methods.
 *
 * For the method insert, you'll be given a pair of (string, integer). The string represents the key and the integer
 * represents the value. If the key already existed, then the original key-value pair will be overridden to the new one.
 *
 * For the method sum, you'll be given a string representing the prefix, and you need to return the sum of all the
 * pairs' value whose key starts with the prefix.
 */
class MapSum {
    final private HashMap<String, Integer> kv = new HashMap<String, Integer>();
    final private HashMap<String, Integer> prefixSum = new HashMap<String, Integer>();

    /** Initialize your data structure here. */
    public MapSum() {
    }

    public void insert(String key, int val) {
        int oldValue = 0;
        if (kv.containsKey(key))
            oldValue = kv.get(key);
        kv.put(key, val);
        updatePrefix(key, oldValue, val);
    }

    public int sum(String prefix) {
        int result = 0;
        if (prefixSum.containsKey(prefix))
            result = prefixSum.get(prefix);
        return result;
    }

    private void updatePrefix(String key, int oldValue, int newValue) {
        for (int i = 1; i <= key.length(); ++i) {
            String p = key.substring(0, i);
            int sum = 0;
            if (prefixSum.containsKey(p))
                sum = prefixSum.get(p).intValue();
            sum = sum - oldValue + newValue;
            prefixSum.put(p, sum);
        }
    }

    public static void test() {
        System.out.println("Q-677 Map Sum Pairs");
        MapSum ms = new MapSum();
        ms.insert("apple", 3);
        ms.insert("app", 5);
        System.out.printf("sum(apple) = %d\n", ms.sum("apple")); // 3
        System.out.printf("sum(app) = %d\n", ms.sum("app")); // 8
        ms.insert("apple", 4);
        System.out.printf("sum(apple) = %d\n", ms.sum("apple")); // 4
        System.out.printf("sum(app) = %d\n", ms.sum("app")); // 9
        System.out.printf("sum(ap) = %d\n", ms.sum("app")); // 9
    }
}

/**
 * Q-684 Redundant Connections (Union Find)
 */
class RedundantConnections {
    private static int root(int[] union, int n) {
        int r = n;
        while (union[r] != r) r = union[r];
        return r;
    }

    public static int[] findRedundantConnection(int[][] edges) {
        if (edges.length == 0) return null;
        int[] uf = new int[2001]; // union-find, 1-base plus extra 0
        for (int i = 0; i < 2001; ++i)
            uf[i] = i;
        for (int i = 0; i < edges.length; ++i) {
            int u = edges[i][0], v = edges[i][1];
            int uroot = root(uf, u), vroot = root(uf, v);
            if (uroot != vroot)
                uf[vroot] = uroot;
            else
                return new int[]{u,v};
        }
        return null;
    }

    public static void test() {
        System.out.println("Q-684 Redundant Connections");
        int[][] e1 = {{1,2}, {2,3}, {3,4}, {1,4}, {1,5}}; // (1,4)
        int[][] e2 = {{3,4},{1,2},{2,4},{3,5},{2,5}}; // (2,5)
        System.out.println(Arrays.toString(findRedundantConnection(e2)));
    }
}

/**
 * Q-698 Partition K Equal Sum Subsets (recursion)
 *
 * Given an array of integers nums and a positive integer k, find whether it's possible to divide this array into k
 * non-empty subsets whose sums are all equal.
 */
class PartitionKEqualSumSubsets {
    private static boolean subset(int[] nums, int start, int kth, int sum, int targetSum, boolean[] used) {
        if (kth == 1) return true;
        if (sum > targetSum)
            return false;
        if (sum == targetSum)
            return subset(nums, 0, kth-1, 0, targetSum, used);
        for (int i = start; i < nums.length; ++i) {
            if (used[i]) continue;
            used[i] = true;
            if (subset(nums, i+1, kth, sum + nums[i], targetSum, used))
                return true;
            used[i] = false;
        }
        return false;
    }

    public static boolean canPartitionKSubsets(int[] nums, int k) {
        if (nums.length == 0)
            return true;
        int total = 0;
        for (int n : nums)
            total += n;
        if (total % k != 0)
            return false;
        boolean[] used = new boolean[nums.length];
        return subset(nums, 0, k, 0, total/k, used);
    }

    public static void test() {
        System.out.println("Q-698 Partition K Equal Sum Subsets");
        System.out.println(canPartitionKSubsets(new int[]{4,3,2,3,5,2,1}, 4)); // true
        System.out.println(canPartitionKSubsets(new int[]{4,3,1,3,1,5,2,1}, 4)); // true
        System.out.println(canPartitionKSubsets(new int[]{4,15,1,1,1,1,3,11,1,10}, 3)); // true
    }
}

/**
 * Q-721 Accounts Merge (Union Find)
 */
class AccountsMerge {
    private static String find(HashMap<String, String> union, String e) {
        return union.get(e) != e ? find(union, union.get(e)) : e;
    }

    private static String find2(HashMap<String, String> union, String e) {
        String root = union.get(e);
        for (String r = e; r != root; root = r)
            r = union.get(root);
        return root;
    }

    public static List<List<String>> accountsMerge(List<List<String>> accounts) {
        List<List<String>> result = new ArrayList<List<String>>();
        if (accounts == null || accounts.size() == 0)
            return result;

        HashMap<String, String> userMap = new HashMap<String, String>(); // email -> user
        HashMap<String, String> unionMap = new HashMap<String, String>(); // email -> email
        for (List<String> acct : accounts) {
            for (int i = 1; i < acct.size(); ++i) {
                unionMap.put(acct.get(i), acct.get(i));
                userMap.put(acct.get(i), acct.get(0));
            }
        }
        // union find to set the root of emails
        for (List<String> acct : accounts) {
            String root = find(unionMap, acct.get(1));
            for (int i = 2; i < acct.size(); ++i) {
                unionMap.put(find(unionMap, acct.get(i)), root);
            }
        }
        // merge emails by following the root
        HashMap<String, TreeSet<String>> mergedAccounts = new HashMap<String, TreeSet<String>>();
        for (List<String> acct : accounts) {
            for (int i = 1; i < acct.size(); ++i) {
                String root = find(unionMap, acct.get(i));
                TreeSet<String> merged = mergedAccounts.containsKey(root) ? mergedAccounts.get(root) : new TreeSet<String>();
                merged.add(acct.get(i));
                mergedAccounts.put(root, merged);
            }
        }
        // massage merged accounts to result format
        for (Map.Entry<String, TreeSet<String>> e : mergedAccounts.entrySet()) {
            ArrayList<String> r = new ArrayList<String>();
            r.add(userMap.get(e.getKey()));
            for (String email : e.getValue())
                r.add(email);
            System.out.println(Arrays.toString(r.toArray()));
            result.add(r);
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-721 Accounts Merge");
        List<List<String>> accounts = new ArrayList<List<String>>() {{
            add(new ArrayList<String>() {{add("John"); add("johnsmith@mail.com"); add("john00@mail.com");}});
            add(new ArrayList<String>() {{add("John"); add("johnnybravo@mail.com");}});
            add(new ArrayList<String>() {{add("John"); add("john00@yahoo.com"); add("johnsmith@mail.com");}});
            add(new ArrayList<String>() {{add("Mary"); add("mary@mail.com");}});
        }};
        List<List<String>> accounts2 = new ArrayList<List<String>>() {{
            add(new ArrayList<String>() {{add("David"); add("David0@m.co"); add("David4@m.co"); add("David3@m.co");}});
            add(new ArrayList<String>() {{add("David"); add("David5@m.co"); add("David5@m.co"); add("David0@m.co");}});
            add(new ArrayList<String>() {{add("David"); add("David1@m.co"); add("David4@m.co"); add("David0@m.co");}});
            add(new ArrayList<String>() {{add("David"); add("David0@m.co"); add("David1@m.co"); add("David3@m.co");}});
            add(new ArrayList<String>() {{add("David"); add("David4@m.co"); add("David1@m.co"); add("David3@m.co");}});
        }};
        List<List<String>> accounts3 = new ArrayList<List<String>>() {{
            add(new ArrayList<String>() {{add("David"); add("David0@m.co"); add("David1@m.co");}});
            add(new ArrayList<String>() {{add("David"); add("David3@m.co"); add("David4@m.co");}});
            add(new ArrayList<String>() {{add("David"); add("David4@m.co"); add("David5@m.co");}});
            add(new ArrayList<String>() {{add("David"); add("David2@m.co"); add("David3@m.co");}});
            add(new ArrayList<String>() {{add("David"); add("David1@m.co"); add("David2@m.co");}});
        }};
        accountsMerge(accounts2);
        accountsMerge(accounts3);
    }
}

/**
 * Q-739 Daily Temperature
 */
class DailyTemperature {
    public static int[] dailyTemperatures(int[] temperatures) {
        int[] result = new int[temperatures.length];
        Stack<Integer> stack = new Stack<Integer>();
        for (int i = temperatures.length-1; i >= 0; --i) {
            while (!stack.empty() && temperatures[stack.peek().intValue()] <= temperatures[i])
                stack.pop();
            result[i] = stack.empty() ? 0 : stack.peek().intValue() - i;
            stack.push(i);
        }
        System.out.print(Arrays.toString(result));
        return result;
    }

    public static void test() {
        System.out.println("Q-739 Daily Temperature");
        dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73});
    }
}

/**
 * Q-743 Network Delay Time (single-source multi-destination shortest path)
 *
 * There are N network nodes, labelled 1 to N. Given times, a list of travel times as directed edges times[i] = (u,v,w),
 * where u is the source node, v is the target node, and w is the time it takes for a signal to travel from source to
 * target. Now, we send a signal from a certain node K. How long will it take for all nodes to receive the signal?
 * If it is impossible, return -1.
 *
 * Implementation node - If single-source single-destination, Dijkstra's algorithm works using a priority queue. For
 * single-source multi-destination, do BFS to reach nodes at increasing level (i.e. increasing number of edges), and
 * converge node to shortest distance from the single source node. Other algorithm is Floyd-Warshall or Bellman-Ford.
 */
class NetworkDelayTime {
    public static int networkDelayTime(int[][] times, int N, int K) {
        int[][] graph = new int[N+1][N+1]; // node index 1 to N
        // graph adjacency matrix, -1 means no edge between 2 nodes
        for (int i = 0; i <= N; ++i)
            for (int j = 0; j <= N; ++j)
                graph[i][j] = -1;
        for (int i = 0; i < times.length; ++i)
            graph[times[i][0]][times[i][1]] = times[i][2];
        int[] distance = new int[N+1];
        for (int i = 1; i <= N; ++i)
            distance[i] = Integer.MAX_VALUE;
        distance[K] = 0;
        ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
        queue.add(K);
        // BFS to reach nodes level by level, and each node converges to the shortest distance from K
        while (!queue.isEmpty()) {
            HashSet<Integer> added = new HashSet<Integer>();
            for (int i = queue.size()-1; i >= 0; --i) {
                int s = queue.poll();
                for (int j = 1; j <= N; ++j) {
                    int e = graph[s][j];
                    // edge exists from s to j
                    if (e != -1 && distance[s] + e < distance[j]) {
                        distance[j] = distance[s] + e;
                        if (!added.contains(j)) {
                            added.add(j);
                            queue.add(j);
                        }
                    }
                }
            }
        }
        // the longest travel time among shortest travel time of all nodes
        int result = -1;
        for (int i = 1; i <= N; ++i)
            if (distance[i] > result)
                result = distance[i];
        return result == Integer.MAX_VALUE ? -1 : result;
    }

    public static void test() {
        System.out.println("Q-743 Network Delay Time");
        int a1[] = new int[]{1,2,1}, a2[] = new int[]{2,1,3};
        System.out.println(networkDelayTime(new int[][]{a1, a2}, 2, 2));
    }
}

/**
 * Q-748 Shortest Completing Word
 */
class ShortestCompletingWords {
    public static String shortestCompletingWord(String licensePlate, String[] words) {
        int totalChars = 0;
        int[] licensePlateChars = new int[26];
        for (char c : licensePlate.toCharArray())
            if (Character.isLetter(c)) {
                licensePlateChars[Character.toLowerCase(c) - 'a'] += 1;
                totalChars++;
            }
        // a -1 tells a letter not appearing in license plate
        for (int i = 0; i < licensePlateChars.length; ++i)
            if (licensePlateChars[i] == 0)
                licensePlateChars[i] = -1;
        String result = null;
        for (String word : words) {
            int count = totalChars;
            int[] charCounts = licensePlateChars.clone();
            for (char c : word.toCharArray()) {
                int index = Character.toLowerCase(c) - 'a';
                // skip letter not appearing in license plate
                if (charCounts[index] > -1) {
                    if (charCounts[index] > 0) {
                        charCounts[index] -= 1;
                        count--;
                    }
                }
            }
            if (count == 0 && (result == null || word.length() < result.length())) {
                result = word;
            }
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-748 Shortest Completing Word");
        System.out.println(shortestCompletingWord("1s3 PSt", new String[]{"step", "stepss", "stripe", "stepple", "steps"}));
        System.out.println(shortestCompletingWord("1s3 456", new String[]{"looks", "pest", "stew", "show"}));
    }
}

/**
 * Q-785 Is Graph Bipartite (2-coloring Graph, BFS)
 */
class GraphBipartite {
    private static boolean bfs(int[][] graph, boolean[] visited, boolean[] colors, int root) {
        ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int node = queue.poll();
            visited[node] = true;
            final boolean opposite = colors[node] == true ? false : true;
            for (int j = 0; j < graph[node].length; ++j) {
                int n = graph[node][j];
                if (visited[n]) {
                    if (colors[n] != opposite) {
                        System.out.printf("%d,%s,%d,%s\n",node,String.valueOf(colors[node]),n,String.valueOf(colors[n]));
                        return false;
                    }
                } else {
                    colors[n] = opposite;
                    queue.add(n);
                }
            }
        }
        return true;
    }

    public static boolean isBipartite(int[][] graph) {
        if (graph.length == 0)
            return false;
        boolean[] visited = new boolean[graph.length];
        boolean[] colors = new boolean[graph.length]; // use boolean to mimic 2-coloring
        // do BFS from every node in the graph
        for (int i = 0; i < graph.length; ++i) {
            if (!visited[i]) {
                colors[i] = true; // white color
                if (!bfs(graph, visited, colors, i))
                    return false;
            }
        }
        return true;
    }

    public static void test() {
        System.out.println("Q-785 Is Graph Bipartite");
        System.out.println(isBipartite(new int[][]{new int[]{1,3}, new int[]{0,2}, new int[]{1,3}, new int[]{0,2}})); // true
        System.out.println(isBipartite(new int[][]{new int[]{1,2,3}, new int[]{0,2}, new int[]{0,1,3}, new int[]{0,2}})); // false
        System.out.println(isBipartite(new int[][]{new int[]{1}, new int[]{0,3}, new int[]{3}, new int[]{1,2}})); // true
    }
}

/**
 * Q-792 Number of Matching Subsequences (2 pointers + hashmap)
 *
 * Given string S and a dictionary of words words, find the number of words[i] that is a subsequence of S.
 */
class NumberOfMatchingSubsequences {
    // check whether s is a subsequence of t
    private static boolean isSubsequence(String s, String t) {
        int si = 0, ti = 0;
        for (; si < s.length(); ++si) {
            boolean match = false;
            for (int j = ti; j < t.length(); ++j) {
                if (s.charAt(si) == t.charAt(j)) {
                    match = true;
                    ti = j+1; // move t index after matched char
                    break;
                }
            }
            if (!match)
                break;
        }
        return si == s.length() ? true : false;
    }

    public static int numMatchingSubseq(String S, String[] words) {
        int result = 0;
        HashSet<String> matched = new HashSet<String>();
        HashSet<String> notMatched = new HashSet<String>();
        for (String w : words) {
            if (matched.contains(w)) {
                result += 1;
                continue;
            } else if (notMatched.contains(w)) {
                continue;
            }
            if (isSubsequence(w, S)) {
                result += 1;
                matched.add(w);
            } else {
                notMatched.add(w);
            }
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-792 Number of Matching Subsequences");
        System.out.println(numMatchingSubseq("abcde", new String[]{"a", "bb", "acd", "ace", "acd", "adf"})); // 4
    }
}

/**
 * Q-799 Champagne Tower (pyramid matrix)
 */
class ChampagneTower {
    public static double champagneTower(int poured, int query_row, int query_glass) {
        double[][] m = new double[101][101];
        m[0][0] = poured;
        for (int i = 0; i < 100; i++) {
            boolean overflowed = false;
            for (int j = 0; j < i+1; j++) {
                if (Double.compare(m[i][j], 1.0) > 0) { // overflow
                    double overflow = m[i][j] - 1.0;
                    m[i][j] = 1.0;
                    m[i+1][j] += overflow/2.0;
                    m[i+1][j+1] += overflow/2.0;
                    overflowed = true;
                }
            }
            if (!overflowed)
                break;
        }
        return m[query_row][query_glass];
    }

    public static void test() {
        System.out.println("Q-799 Champagne Tower");
        System.out.println(champagneTower(1,0,0)); // 1.0
        System.out.println(champagneTower(2,0,0)); // 1.0
        System.out.println(champagneTower(2,1,0)); // 0.5
        System.out.println(champagneTower(5,2,1)); // 1.0
        System.out.println(champagneTower(5,2,2)); // 0.5
        System.out.println(champagneTower(6,2,0)); // 0.75
    }
}

public class Leetcode
{
  public static void main(String[] args)
  {
      System.out.println("Leetcode");
      SimplifyPath.run();
      SubstrsWithConcatWords.run();
      FindSubstring.run();
      CoinChange.run();
      ReconstructItinerary.run();
      KthSmallestInSortedMatrix.test();
      LongestSubstingWithKRepeatingChars.test();
      ZeroOneMatrix.test();
      MinimumGeneticMutations.test();
      RandomPickIndex.test();
      QueueReconstructionByHeight.test();
      BattleshipsInBoard.test();
      OneThreeTwoPattern.test();
      UniqueSubstringsInWraparoundString.test(); //
      LongestRepeatingCharacterReplacement.test();
      MyCircularQueue.test(); // thread-unsafe
      MyCircularDeque.test();
      MaximumLengthOfPairChain.test(); // sort, special compare
      ReplaceWords.test();
      FindKClosestElements.test();
      MagicDictionary.test(); // hashmap
      MapSum.test(); // data structure
      RedundantConnections.test(); // union find
      PartitionKEqualSumSubsets.test(); // recursion
      AccountsMerge.test(); // union find
      DailyTemperature.test(); // stack
      NetworkDelayTime.test(); // single-source multi-destination shortest path
      ShortestCompletingWords.test();
      GraphBipartite.test(); // BFS
      NumberOfMatchingSubsequences.test(); // is_subsequence + memoization
      ChampagneTower.test(); // matrix + memoization

  }
}
