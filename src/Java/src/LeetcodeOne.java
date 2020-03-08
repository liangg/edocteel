/**
 * Leetcode Questions - medium level 1 - 350
 *
 * Including intervals questions
 */

import java.util.*;


/** Q-3 Longest Substring Without Repeating Characters */
class LongestSubstringWithoutRepeatingChars {
    public static int lengthOfLongestSubstring(String s) {
        int result = 0;
        // maintain a window that has only unique chars
        HashSet<Character> window = new HashSet<Character>();
        for (int i = 0, j = 0; i < s.length(); ++i) {
            // push window right index with j
            while (j < s.length() && !window.contains(Character.valueOf(s.charAt(j)))) {
                window.add(Character.valueOf(s.charAt(j)));
                if (j-i+1 > result)
                    result = j-i+1;
                j += 1;
            }
            // move window left index when there is repeating char at the right side
            window.remove(Character.valueOf(s.charAt(i)));
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-3 Longest Substring Without Repeating Characters");
        System.out.println(lengthOfLongestSubstring("pwwkew"));
        System.out.println(lengthOfLongestSubstring("pwwkewpw"));
    }
}

/** Q-15 Three Sum */
class ThreeSum {
    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        Arrays.sort(nums);
        System.out.println(Arrays.toString(nums));
        if (nums.length == 0 || nums[0] > 0 || nums[nums.length-1] < 0)
            return result;
        for (int i = 0; i < nums.length-2; ++i) {
            if (nums[i] > 0) // no more possible solution
                break;
            if (i > 0 && nums[i] == nums[i-1])
                continue;
            int target = 0-nums[i], l = i+1, r = nums.length-1;
            while (l < r) {
                if (nums[l] + nums[r] == target) {
                    Integer sol[] = new Integer[]{nums[i], nums[l], nums[r]};
                    result.add(Arrays.asList(sol));
                    while (l < r && nums[l] == nums[l+1]) ++l;
                    while (r > l && nums[r] == nums[r-1]) --r;
                    ++l;
                    --r;
                }
                else if (nums[l] + nums[r] > target)
                    --r;
                else
                    ++l;
            }
        }
        System.out.println(result.toString());
        return result;
    }

    public static void test() {
        System.out.println("Q-15 Three Sum");
        threeSum(new int[]{0,0,0});
        threeSum(new int[]{-1, 0, 1, 2, -1, -4});
    }
}

/** Q-17 Letter Combinations of Phone Number */
class LetterComboOfPhoneNumber {
  static String[] letterMap = new String[] {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

  public static List<String> letterCombinations(String digits) {
      int totals = 1;
      for (char c : digits.toCharArray()) {
          int nletters = letterMap[Character.getNumericValue(c)].length();
          totals *= nletters;
      }

      // has digit 0 or 1, and therefore empty result
      if (totals == 0) {
          return new ArrayList<String>();
      }

      char strs[][] = new char[totals][digits.length()];
      for (int di = 0, left = 1; di < digits.length(); ++di) {
          String letters = letterMap[Character.getNumericValue(digits.charAt(di))];
          int nletters = letters.length();
          int strIdx = 0;
          int right = totals / (left * nletters);
          for (int l = 0; l < left; ++l) {
              for (int i = 0; i < nletters; ++i) {
                  for (int r = 0; r < right; ++r){
                      strs[strIdx++][di] = letters.charAt(i);
                  }
              }
          }
          left *= nletters;
      }

      List<String> result = new ArrayList<String>();
      for (int i = 0; i < totals; ++i) {
          result.add(new String(strs[i]));
          System.out.println(result.get(i));
      }
      return result;
  }

  public static List<String> letterCombinationsRecursive(String digits) {
    List<String> result = new ArrayList<String>();
    if (digits == null || digits.isEmpty())
      return result;
    if (digits.length() == 1) {
      for (char c : letterMap[digits.charAt(0) - '0'].toCharArray())
        result.add(Character.toString(c));
      return result;
    }

    char c = digits.charAt(0);
    List<String> words = letterCombinationsRecursive(digits.substring(1));
    for (String s : words) {
      for (char lc : letterMap[c - '0'].toCharArray())
        result.add(lc + s);
    }
    return result;
  }

  public static void run() {
    System.out.println("------------ Letter Combinations of Phone Number ------------");
    letterCombinations("234");
    List<String> r = letterCombinationsRecursive("234");
    System.out.println(r.toString());
  }
}

/**
 * Q-23 Merge K Sorted Lists
 */
class MergeKSortedLists {
    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> minHeap = new PriorityQueue<>(10, new Comparator<ListNode>() {
            @Override
            public int compare(ListNode o1, ListNode o2) {
                return o1.val == o2.val ? 0 : (o1.val < o2.val ? -1 : 1);
            }
        });
        for (ListNode lh : lists)
            if (lh != null) minHeap.add(lh);
        ListNode dummy = new ListNode(-1);
        ListNode tail = dummy;
        while (!minHeap.isEmpty()) {
            ListNode n = minHeap.poll();
            tail.next = n;
            tail = n;
            ListNode next = n.next;
            tail.next = null;
            if (next != null) minHeap.add(next);
        }
        ListNode head = dummy.next;
        dummy.next = null;
        return head;
    }
}

/** Q-31 Next Permutation */
class NextPermutation {
  public static void nextPermutation(int[] nums) {
    int i = 0;
    // find the first number X that breaks a ascending order, e.g. 2 in "2531"
    for (i = nums.length-2; i >= 0 && nums[i] >= nums[i+1]; --i);
    if (i < 0) {
      Arrays.sort(nums);
      return;
    }

    // find the smallest number Y that is larger than X, and swap them
    int k = i+1;
    for (int j = i+2; j < nums.length; ++j) {
      if (nums[j] <= nums[i])
        break;
      if (nums[j] < nums[k])
        k = j;
    }
    int t = nums[k];
    nums[k] = nums[i];
    nums[i] = t;
    // reverse sort numbers after Y
    Arrays.sort(nums, i+1, nums.length);
  }

  public static void test() {
    System.out.println("Q-31 Next Permutation");
    int[] n = {1,3,2}; // 213
    int[] n2 = {1,2,5,3}; // 1325
    int[] n3 = {5,3,2,1}; // 1235
    nextPermutation(n);
    System.out.println(Arrays.toString(n));
    nextPermutation(n2);
    System.out.println(Arrays.toString(n2));
    nextPermutation(n3);
    System.out.println(Arrays.toString(n3));
  }
}

/**
 * Q-34. Find First and Last Position of Element in Sorted Array
 */
class FindFirstLastPosSortedArray {
  public static int[] searchRange(int[] nums, int target) {
    if (nums.length == 0)
      return new int[]{-1, -1};

    int l, r, left = -1, right = -1;
    for (l = 0, r = nums.length-1; l < r;) {
      int m = l + (r-l)/2;
      if (nums[m] >= target)
        r = m;
      else
        l = m+1;
    }

    if (nums[l] != target)
      return new int[]{-1, -1};
    left = l;

    for (l = 0, r = nums.length-1; l < r;) {
      int m = l + (r-l)/2 + 1;
      if (nums[m] > target)
        r = m-1;
      else
        l = m;
    }
    right = l;
    return new int[]{left, right};
  }

  public static void test() {
    System.out.println("Q-34 Find First and Last Position of Element in Sorted Array");
    int[] a = new int[]{1,3,4,4,4,4,5};
    System.out.println(Arrays.toString(searchRange(a, 4)));
    System.out.println(Arrays.toString(searchRange(a, 5)));
    System.out.println(Arrays.toString(searchRange(a, 6)));
  }
}

/** Q-36 Valid Sodoku */
class ValidSudoku {
    public boolean isValidSudoku(char[][] board) {
        int N = board[0].length;
        if (N != 9)
            return false;
        Boolean[] taken = new Boolean[N];

        for (int i = 0; i < N; ++i) {
            Arrays.fill(taken, Boolean.FALSE);
            for (int j = 0; j < N; ++j) {
                if (board[i][j] != '.') {
                    int d = board[i][j] - '0';
                    if (!(d > 0 && d <= 9) || taken[d-1] == Boolean.TRUE) {
                        return false;
                    }
                    taken[d-1] = Boolean.TRUE;
                }
            }
        }

        for (int i = 0; i < N; ++i) {
            Arrays.fill(taken, Boolean.FALSE);
            for (int j = 0; j < N; ++j) {
                if (board[j][i] != '.') {
                    int d = board[j][i] - '0';
                    if (!(d > 0 && d <= 9) || taken[d-1] == Boolean.TRUE) {
                        return false;
                    }
                    taken[d-1] = Boolean.TRUE;
                }
            }
        }

        for (int i = 0; i < N; i += 3) {
            for (int j = 0; j < N; j += 3) {
                Arrays.fill(taken, Boolean.FALSE);
                for (int r = 0; r < 3; ++r) {
                    for (int c = 0; c < 3; ++c) {
                        if (board[i+r][j+c] != '.') {
                            int d = board[i+r][j+c] - '0';
                            if (!(d > 0 && d <= 9) || taken[d-1] == Boolean.TRUE) {
                                return false;
                            }
                            taken[d-1] = Boolean.TRUE;
                        }
                    }
                }
            }
        }

        return true;
    }
}

/**
 * Q-39 Combination Sum
 *
 * Given a set of candidate numbers "candidates" (without duplicates) and a target number "target", find all unique
 * combinations in candidates where the candidate numbers sums to target. The same repeated number may be chosen from
 * candidates unlimited number of times.
 *
 * II:
 *
 * Given a collection of candidate numbers "candidates" and a target number (target), find all unique combinations in
 * candidates where the candidate numbers sums to target. Each number in candidates may only be used once in the combination.
 */
class CombinationSum {
  // I:
  public static List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> result = new ArrayList<List<Integer>>();
    if (candidates.length == 0)
      return result;
    Arrays.sort(candidates);
    for (int i = 0; i < candidates.length; ++i) {
      if (candidates[i] > target) break;
      if (candidates[i] == target) {
        List<Integer> sol = new ArrayList<Integer>();
        sol.add(candidates[i]);
        result.add(sol);
        break;
      }
      // the crucial point is to clone candidates with begin at 'i', not 'i+1'; if use the entire
      // candidate, the result would have duplicates such as (2,3,3), (3,2,3).
      int[] restCandidates = Arrays.copyOfRange(candidates, i, candidates.length);
      List<List<Integer>> sols = combinationSum(restCandidates, target - candidates[i]);
      for (List<Integer> s : sols) {
        s.add(0, candidates[i]);
        result.add(s);
      }
    }

    return result;
  }

  // II There are duplicates in candidates, and each can be used exactly once
  public static List<List<Integer>> combinationSum2(int[] candidates, int target) {
    List<List<Integer>> result = new ArrayList<List<Integer>>();
    if (candidates.length == 0)
      return result;
    Arrays.sort(candidates);
    List<Integer> sol = new ArrayList<Integer>();
    combinationSumRecursive2(candidates, target, 0, sol, result);
    return result;
  }

  private static void combinationSumRecursive2(int[] candidates, int target, int start, List<Integer> sol, List<List<Integer>> result) {
    if (target < 0)
      return;
    if (target == 0) {
      result.add(new ArrayList<Integer>(sol));
      return;
    }
    for (int i = start; i < candidates.length; ++i) {
      if (i > start && candidates[i] == candidates[i - 1]) // skip duplicate solution
        continue;
      sol.add(candidates[i]);
      combinationSumRecursive2(candidates, target-candidates[i], i+1, sol, result);
      sol.remove(sol.size()-1); // pop the last number
    }
  }

  public static void test() {
    System.out.println("Q-39 Combination Sum I, II");
    System.out.println(combinationSum(new int[]{2,3,5}, 8).toString());
    System.out.println(combinationSum2(new int[]{10,1,2,7,6,1,5}, 8).toString());
  }
}

/**
 * Q-44: Wildcard Matching
 */
class WildcardMatching {
    public static boolean isMatch(String s, String p) {
        int pstar = -1; // position of last wildcard star '*'
        int star_matched = -1; // first position after matched by '*'
        int sp = 0, pp = 0;
        // keep matching string as long as there is a wildcard star, which might be
        // the last char in the pattern string p
        while (sp < s.length()) {
            if (pp < p.length() && (s.charAt(sp) == p.charAt(pp) || p.charAt(pp) == '?')) {
                pp++;
                sp++;
            } else if (pp < p.length() && p.charAt(pp) == '*') {
                pstar = pp;
                star_matched = sp;
                pp++;
            } else if (pstar != -1) {
                // backtrace and match more chars in string s
                pp = pstar + 1;
                star_matched++;
                sp = star_matched;
            } else {
                break;
            }
        }
        // wildcard stars at the end of the pattern matches all
        if (pp < p.length()) {
            for (; pp < p.length() && p.charAt(pp) == '*'; ++pp);
        }
        boolean matched = (pp == p.length() && sp == s.length());
        System.out.print(s + " and " + p + (matched ? " match" : " not match") + "\n");
        return matched;
    }

    public static void run() {
        System.out.println("---------- Wildcard Matching ----------");
        isMatch("ab", "*ab");
        isMatch("ho", "**ho");
        isMatch("ho", "ho**");
        isMatch("abcd", "*?c*");
        isMatch("aaaaaaaa", "*aaaaaaaaa*");
        isMatch("aaaaaaaaaaaa", "*aaaaa*");
        isMatch("aaabbbaabaaaaababaabaaabbabbbbbbbbaabababbabbbaaaaba", "a*******b");
    }
}

/**
 * Q-47 Permutation II
 *
 * Given a collection of numbers that might contain duplicates, return all possible unique permutations.
 */
class Permutations {
  public static List<List<Integer>> permuteUnique(int[] nums) {
    List<List<Integer>> result = new ArrayList<List<Integer>>();
    if (nums.length == 0)
      return result;
    Arrays.sort(nums);
    boolean[] vivisted = new boolean[nums.length];
    List<Integer> sol = new ArrayList<Integer>();
    permSearch(nums, 0, vivisted, sol, result);
    return result;
  }

  private static void permSearch(int[] nums, int level, boolean[] visited, List<Integer> sol, List<List<Integer>> result) {
    if (level == nums.length) {
      result.add(new ArrayList<Integer>(sol));
      return;
    }
    for (int i = 0; i < nums.length; ++i) {
      if (visited[i])
        continue;
      if (i > 0 && nums[i] == nums[i-1] && !visited[i-1])
        continue;
      visited[i] = true;
      sol.add(nums[i]);
      permSearch(nums, level + 1, visited, sol, result);
      sol.remove(sol.size()-1);
      visited[i] = false;
    }
  }

  public static void test() {
    System.out.println("Q-47 Permutation II");
    System.out.println(permuteUnique(new int[]{1,2,1,3}).toString());
  }
}

/** Q-48 Rotate Images */
class RotateImage {
    public void rotate(int[][] matrix) {
        int N = matrix[0].length;
        for (int i = 1; i < N; ++i) {
            for (int j = i; j > i/2; --j) {
                int tmp = matrix[i-j][j];
                matrix[i-j][j] = matrix[j][i-j];
                matrix[j][i-j] = tmp;

                if (i == N-1)
                    continue;

                tmp = matrix[N-1-(i-j)][N-1-j];
                matrix[N-1-(i-j)][N-1-j] = matrix[N-1-j][N-1-(i-j)];
                matrix[N-1-j][N-1-(i-j)] = tmp;
            }
        }

        for (int i = 0; i < N; ++i) {
            for (int l = 0, r = N - 1; l < r; ++l, --r) {
                int tmp = matrix[i][l];
                matrix[i][l] = matrix[i][r];
                matrix[i][r] = tmp;
            }
        }
    }
}

/**
 * Q-55 Jump Game
 *
 * Given an array of non-negative integers, you are initially positioned at the first index of the array. Each element
 * in the array represents your maximum jump length at that position. Determine if you are able to reach the last index.
 */
class JumpGame {
  public static boolean canJump(int[] nums) {
    // greedy algorithm to find the max reach point
    int maxReach = 0;
    for (int i = 0; i < nums.length; ++i) {
      // max reach point no longer move, or reach the last index
      if (i > maxReach || maxReach >= nums.length-1)
        break;
      maxReach = Math.max(maxReach, i+nums[i]);
    }
    return maxReach >= nums.length-1;
  }

  public static void test() {
    System.out.println("Q-55 Jump Game");
    System.out.println(canJump(new int[]{2,0,0})); // true
    System.out.println(canJump(new int[]{2,3,1,1,4})); // true
    System.out.println(canJump(new int[]{3,2,1,0,4})); // false
  }
}

/** Q-56 Merge Intervals */
class MergeIntervals {
    public static List<Interval> merge(List<Interval> intervals) {
        ArrayList<Interval> result = new ArrayList<Interval>();
        if (intervals == null || intervals.isEmpty())
            return result;

        // sort intervals by start
        Collections.sort(intervals, new Comparator<Interval>() {
            @Override
            public int compare(Interval o1, Interval o2) {
                if (o1.start == o2.start && o1.end == o2.end)
                    return 0;
                if (o1.start < o2.start || (o1.start == o2.start && o1.end < o2.end))
                    return -1;
                return 1;
            }
        });

        boolean first = true;
        int start = 0, end = 0;
        for (Interval intv : intervals) {
            if (first) { // first
                start = intv.start;
                end = intv.end;
                first = false;
                continue;
            }
            if (end >= intv.start) {
                if (end < intv.end)
                    end = intv.end;
            } else {
                result.add(new Interval(start, end));
                start = intv.start;
                end = intv.end;
            }
        }
        result.add(new Interval(start, end));
        System.out.println(Arrays.deepToString(result.toArray()));
        return result;
    }

    public static void test() {
        System.out.println("Q-56 Merge Intervals");
        Interval v1 = new Interval(8,10), v2 = new Interval(2,6), v3 = new Interval(15,18), v4 = new Interval(1,3), v5 = new Interval(0,0);
        ArrayList<Interval> intervals0 = new ArrayList<Interval>(Arrays.asList(v4, v5));
        MergeIntervals.merge(intervals0);
        ArrayList<Interval> intervals = new ArrayList<Interval>(Arrays.asList(v1, v2, v3, v4));
        MergeIntervals.merge(intervals);
    }
}

/** Q-67 Add Binary */
class AddBinary {
    public static String addBinary(String a, String b) {
        if (a.length() < b.length()) {
            String t = a;
            a = b;
            b = t;
        }
        StringBuilder result = new StringBuilder("");
        int bi = b.length()-1, ai = a.length()-1, carry = 0;
        for (; bi >= 0; bi--, ai--) {
            int da = a.charAt(ai) - '0', db = b.charAt(bi) - '0';
            int s = da + db + carry;
            result.append(s % 2);
            carry = s/2;
        }
        for (; ai >= 0; ai--) {
            int s = (int)(a.charAt(ai) - '0') + carry;
            result.append(s % 2);
            carry = s/2;
        }
        if (carry > 0)
            result.append(carry);
        return result.reverse().toString();
    }

    public static void test() {
        System.out.println("Q-67 Add binary");
        System.out.println(addBinary("1010", "1011")); // "10101"
        System.out.println(addBinary("101011", "1011")); // "110110"
    }
}

/**
 * Q-71 Simplify Path
 *
 * Given an absolute path for a file (Unix-style), simplify it.
 */
class SimplifyPath
{
    public static String simplifyPath(String path) {
        ArrayDeque<String> pathQueue = new ArrayDeque<String>();
        StringBuilder dirStr = new StringBuilder();
        for (int i = 0; i < path.length(); ++i) {
            if (path.charAt(i) == '/') {
                final String dir = dirStr.toString();
                if (dir.equals("..")) {
                    if (!pathQueue.isEmpty())
                        pathQueue.removeLast();
                } else if (!(dir.length() == 0 || dir.equals("."))) {
                    pathQueue.add(dir);
                }
                dirStr.setLength(0);
            } else {
                dirStr.append(path.charAt(i));
            }
        }
        /* check last directory in path */
        if (dirStr.length() > 0) {
            final String lastDir = dirStr.toString();
            if (lastDir.equals("..")) {
                if (!pathQueue.isEmpty())
                    pathQueue.removeLast();
            } else if (!(lastDir.length() == 0 || lastDir.equals("."))) {
                pathQueue.add(lastDir);
            }
        }

        /* reconstruct simplified path */
        dirStr.setLength(0);
        Iterator<String> itr = pathQueue.iterator();
        while (itr.hasNext()) {
            dirStr.append("/").append(itr.next());
        }
        String newPath = dirStr.length() > 0 ? dirStr.toString() : "/";
        System.out.println(path + " is simplified to " + newPath);
        return newPath;
    }

    public static void run() {
        System.out.println("------- Simplify Path -------");
        simplifyPath("/a/./b/../../c/");
        simplifyPath("/../");
        simplifyPath("/...");
        simplifyPath("/home//foo/");
        simplifyPath("/home..//..foo../.");
        simplifyPath("/home..//..foo../..");
    }
}

/** Q-75 Sort Colors */
class SortColors {
  private static void swap(int[] nums, int l, int r) {
    int t = nums[l];
    nums[l] = nums[r];
    nums[r] = t;
  }

  public static void sortColors_Old(int[] nums) {
    int l = 0, r = nums.length-1;
    while (l < r) {
        if (nums[l] == 1 && nums[r] == 1) {
          int i;
          for (i = l; i <= r && nums[i] == 1; ++i);
          if (i <= r)
              swap(nums, l, i);
          else
              break;
        }
        while (nums[l] == 0 && l < r) ++l;
        while (nums[r] == 2 && r > 0) --r;
        if (l < r)
          swap(nums, l, r);
    }
    System.out.println(Arrays.toString(nums));
  }

  public static void sortColors(int[] nums) {
    int l = 0, r = nums.length-1;
    for (int i = 0; i <= r; ) {
      if (nums[i] == 0) {
        swap(nums, i++, l);
        l++;
      } else if (nums[i] == 2) {
        swap(nums, i, r);
        r--;
      } else
        i++;
    }
    System.out.println(Arrays.toString(nums));
  }

  public static void test() {
    System.out.println("Q-75 Sort Colors");
    int[] t1 = {1,0,0,2,1,2,0,2};
    SortColors.sortColors(t1);
    int[] t2 = {2,0,1};
    SortColors.sortColors(t2);
  }
}

/**
 * Q-76: Minimum Window Substring
 */
class MinimumWindowSubstring {
    private class Elem {
        final Character c;
        final Integer position;
        public Elem(Character c, Integer p) { this.c = c; this.position = p; }
    }

    private class ElemComparactor implements Comparator<Elem> {
        @Override
        public int compare(Elem e1, Elem e2) {
            return e1.position.compareTo(e2.position);
        }
    }

    public String minWindow(String S, String T) {
        HashMap<Character, Integer> tchars = new HashMap<Character, Integer>();
        for (char c : T.toCharArray()) {
            if (tchars.containsKey(c)) {
                int count = tchars.get(c);
                tchars.put(c, count+1);
            } else {
                tchars.put(c, 1);
            }
        }

        int minWinSize = Integer.MAX_VALUE;
        int minWinFirst = -1, minWinLast = -1;
        // remember current position that a T char appears in the window
        HashMap<Character, ArrayList<Integer>> currTcPos = new HashMap<Character, ArrayList<Integer>>();
        TreeSet<Elem> window = new TreeSet<Elem>(new ElemComparactor());
        for (int i = 0; i < S.length(); ++i) {
            Character sc = new Character(S.charAt(i));
            if (tchars.containsKey(sc) == false) {
                continue;
            }

            // replace T char position in the window with the latest position
            if (currTcPos.containsKey(sc)) {
                ArrayList<Integer> cpos = currTcPos.get(sc);
                if (cpos.size() == tchars.get(sc)) {
                    Integer currP = cpos.get(0);
                    window.remove(new Elem(sc, currP));
                    // remove the first and add the latest position
                    cpos.remove(0);
                }
                cpos.add(i);
            } else {
                ArrayList<Integer> cpos = new ArrayList<Integer>();
                cpos.add(i);
                currTcPos.put(sc, cpos);
            }
            window.add(new Elem(sc, new Integer(i)));

            if (window.size() == T.length()) {
                Elem first = window.first();
                Elem last = window.last();
                if (last.position - first.position < minWinSize) {
                    minWinSize = last.position - first.position;
                    minWinFirst = first.position;
                    minWinLast = last.position;
                }
            }
        }

        String winStr = (minWinSize == Integer.MAX_VALUE) ? "" : S.substring(minWinFirst, minWinLast+1);
        System.out.println(S + ", " + T + " = " + winStr);
        return winStr;
    }

    public void run() {
        System.out.println("---------- Minimum Window Substring -----------");
        minWindow("ADOBECODEBANC", "ABC");
        minWindow("a", "aa");
        minWindow("ADOBECOADEBANC", "ABCA");
    }
}

/** Q-77 Combinations */
class Combinations
{
    private void combine_k_n(int n, int k, int idx, int combo_idx, final Integer[] combo,
                             final List<List<Integer>> result)
    {
        if (combo_idx == k) {
            result.add(new ArrayList<Integer>(Arrays.asList(combo)));
            return;
        }
        /* not enough integers to fill in combination */
        if ((idx + k - combo_idx - 1) > n)
            return;

        combine_k_n(n, k, idx+1, combo_idx, combo, result);
        combo[combo_idx] = new Integer(idx);
        combine_k_n(n, k, idx+1, combo_idx+1, combo, result);
    }

    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        Integer[] combo = new Integer[k];
        combine_k_n(n, k, 1, 0, combo, result);
        return result;
    }
}

/**
 * Q-78: Subsets
 *
 * Given a set of distinct integers, S, return all possible subsets. Note, Elements in a subset must be in
 * non-descending order; the solution set must not contain duplicate subsets.
 *
 * Q-90 Subsets II
 *
 * Given a collection of integers that might contain duplicates, nums, return all possible subsets (the power set).
 * Note: The solution set must not contain duplicate subsets
 */
class Subsets {
    private void gen_subsets(int[] S, int idx, boolean[] solution, ArrayList<ArrayList<Integer>> result) {
        if (idx == S.length) {
            ArrayList<Integer> sol = new ArrayList<Integer>();
            for (int i = 0; i < S.length; ++i)
                if (solution[i])
                    sol.add(new Integer(S[i]));
            result.add(sol);
            return;
        }

        solution[idx] = false;
        gen_subsets(S, idx+1, solution, result);
        solution[idx] = true;
        gen_subsets(S, idx+1, solution, result);
    }

    public ArrayList<ArrayList<Integer>> subsets_OLD(int[] S) {
        boolean[] solution = new boolean[S.length];
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>(S.length*2);
        Arrays.sort(S);
        gen_subsets(S, 0, solution, result);
        return result;
    }

    private void search(int[] nums, int level, List<Integer> sol, List<List<Integer>> result) {
        if (level == nums.length) {
            result.add(new ArrayList<>(sol));
            return;
        }

        sol.add(nums[level]);
        search(nums, level+1, sol, result);
        sol.remove(sol.size()-1);
        search(nums, level+1, sol, result);
    }

    // Subsets I
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        List<Integer> sol = new ArrayList<>();
        search(nums, 0, sol, result);
        return result;
    }

    private static void searchWithDup(int[] nums, int level, List<Integer> sol, List<List<Integer>> result) {
        result.add(new ArrayList<>(sol));

        for (int i = level; i < nums.length; ++i) {
            sol.add(nums[i]);
            searchWithDup(nums, i+1, sol, result);
            sol.remove(sol.size() - 1);
            while (i < nums.length-1 && nums[i] == nums[i+1]) // skip dup
                ++i;
        }
    }

    // Subsets II
    public static List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        List<Integer> sol = new ArrayList<>();
        Arrays.sort(nums);
        searchWithDup(nums, 0, sol, result);
        System.out.println(result.toString());
        return result;
    }

    public static void test() {
        System.out.println("Q-90 Subsets II");
        subsetsWithDup(new int[]{1,2,2,2});
    }
}

/** Q-79 Word Search */
class WordSearch {
    /* Search deadend with predecessor coordniator i.e. current cell and
     * the from path cell */
    private class SearchTuple {
        public int row;
        public int col;
        public int from_row;
        public int from_col;
        public int idx;

        public SearchTuple(int r, int c, int from_r, int from_c, int i) {
            row = r;
            col = c;
            from_row = from_r;
            from_col = from_c;
            idx = i;
        }

        public boolean equals(Object obj) {
            SearchTuple o = (SearchTuple)obj;
            return row == o.row && col == o.col && from_row == o.from_row &&
                    from_col == o.from_col && idx == o.idx;
        }

        public int hashCode() {
            return (row*row*row + col*col + idx*31) ^ (from_row*from_row + from_col);
        }
    }

    private boolean search_word(char[][] board, int N, int M,
                                int row, int col, int from_row, int from_col,
                                String word, int idx, boolean[][] visited,
                                HashSet<SearchTuple> memo)
    {
        if (idx == word.length())
            return true;
        /* check matrix boundary, whether the cell was visited, and whether the
           cell is known to be deadend */
        if (row < 0 || row >= N || col < 0 || col >= M ||
                visited[row][col] || board[row][col] != word.charAt(idx) ||
                memo.contains(new SearchTuple(row, col, from_row, from_col, idx)))
            return false;
        visited[row][col] = true;
        /* walk to neighboring cells recursively */
        if (search_word(board, N, M, row-1, col, row, col, word, idx+1, visited, memo) ||
                search_word(board, N, M, row, col+1, row, col, word, idx+1, visited, memo) ||
                search_word(board, N, M, row+1, col, row, col, word, idx+1, visited, memo) ||
                search_word(board, N, M, row, col-1, row, col, word, idx+1, visited, memo))
            return true;
        /* memorize search miss for the coordinate and search word index */
        memo.add(new SearchTuple(row, col, from_row, from_col, idx));
        visited[row][col] = false;
        return false;
    }

    public boolean exist(char[][] board, String word) {
        int N = board.length;
        int M = board[0].length;

        /* visit map to remember cells that have been taken */
        boolean[][] visited = new boolean[N][M];
        for (int i = 0; i < N; ++i)
            for (int j = 0; j < M; ++j)
                visited[i][j] = false;

        /* memoization to remember deadend cells i.e. search miss */
        HashSet<SearchTuple> memo = new HashSet<SearchTuple>();
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < M; ++j) {
                if (search_word(board, N, M, i, j, 0, 0, word, 0, visited, memo))
                    return true;
            }
        }
        return false;
    }

    public void run() {
        System.out.println("--------- Word Search --------");
        char[][] b0 = new char[][] {{'a','a'}};
        System.out.println("aaa" + (exist(b0, "aaa") ? " " : " not ") + "exists");

        char[][] b1 = new char[][] {
                {'e','a','f','a','d'}, {'f','b','c','b','c'},{'c','e','d','d','e'},
                {'a','c','f','b','e'}
        };

        System.out.println("dbcba" + (exist(b1, "dbcba") ? " " : " not ") + "exists");
        System.out.println("abcbf" + (exist(b1, "abcbf") ? " " : " not ") + "exists");
    }
}


/** Q-80 Remove Duplicates from Sorted Array II */
class RemoveDuplicatesFromSortedArray {
    public static int removeDuplicates(int[] nums) {
        if (nums.length <= 2)
            return nums.length;
        int write = 2;
        for (int i = 2; i < nums.length; ++i) {
            if (nums[i] == nums[write-1] && nums[i] == nums[write-2])
                ;
            else
                nums[write++] = nums[i];
        }
        return write;
    }

    public static void test() {
        System.out.println("Q-80. Remove Duplicates from Sorted Array II ");
        int[] t1 = new int[] {0,0,0,1,1,1,1,2,3,3,3,4,4,4};
        System.out.println(removeDuplicates(t1));
    }
}

/**
 * Q-89 Gray Code
 *
 * The gray code is a binary numeral system where two successive values differ in only one bit. Given a non-negative
 * integer n representing the total number of bits in the code, print the sequence of gray code. A gray code sequence
 * must begin with 0.
 */
class GrayCode {
    public static List<Integer> grayCode(int n) {
        List<Integer> result = new ArrayList<>();
        result.add(0);
        for (int i = 0; i < n; ++i) {
            int L = result.size();
            for (int j = L-1; j >= 0; --j) {
                result.add((1 << i) | result.get(j));
            }
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-89 Gray Code");
        System.out.println(grayCode(0));
        System.out.println(grayCode(2));
        System.out.println(grayCode(3));
    }
}

/**
 * Q-93 Restore IP Address
 *
 * If we only cares for the number of possible IP addresses, it can be solved by iterative algorithm using memoization
 * matrix.
 */
class RestoreIpAddress {
    private static void restore(String s, int idx, int k, String sol, List<String> result) {
        if (k == 4) {
            if (idx == s.length()) {
                result.add(sol);
                return;
            }
        }

        if (idx >= s.length())
            return;

        int d = 0;
        boolean first0 = false;
        for (int i = 0; i < 3 && !first0 && idx + i < s.length(); ++i) {
            d = d*10 + s.charAt(idx+i) - '0';
            first0 = d == 0;
            if (d > 255)
                return;
            restore(s, idx+i+1, k+1, sol + Integer.toString(d) + (k+1 < 4 ? "." : ""), result);
        }
    }

    private static List<String> restoreIpAddresses(String s) {
        final List<String> result = new ArrayList<String>();
        if (s.length() < 4 || s.length() > 12)
            return result;
        restore(s, 0, 0, "", result);
        System.out.println(result.toString());
        return result;
    }

    public static void test() {
        System.out.println("----------- Restore IP Addresses --------------");
        restoreIpAddresses("25525511135");
        restoreIpAddresses("123562559");
        restoreIpAddresses("0000"); // 0.0.0.0
        restoreIpAddresses("010010"); // 0.10.0.10, 0.100.1.0
    }
}

/**
 * Q-127: Word Ladder
 *
 * Given two words (start and end), and a dictionary, find the length of shortest transformation sequence from start to
 * end, with rules 1, Only one letter can be changed at a time; 2, Each intermediate word must exist in the dictionary.
 *
 * For example,
 * start = "hit", end = "cog", dict = ["hot","dot","dog","lot","log"].
 * One shortest transformation is "hit" -> "hot" -> "dot" -> "dog" -> "cog" and
 * its word ladder length is 5.
 */
class WordLadder {
    static class Pair {
        public String word;
        public int length;
        public Pair(String n, int l) {
            word = n;
            length = l;
        }
    }

    private int ladderLength(String start, String end, HashSet<String> dict) {
        HashSet<String> visited = new HashSet<String>();
        ArrayDeque<Pair> workset = new ArrayDeque<Pair>();
        workset.add(new Pair(start, 1));
        while (!workset.isEmpty()) {
            Pair p = workset.removeFirst();
            String word = p.word;
            if (word.equals(end))
                return p.length;
            if (visited.contains(word))
                continue;
            else
                visited.add(word);
            StringBuffer wbuf = new StringBuffer(word);
            for (int i = 0; i < word.length(); ++i) {
                char cur_c = wbuf.charAt(i);
                for (char c = 'a'; c <= 'z'; ++c) {
                    if (c != cur_c) {
                        wbuf.setCharAt(i, c);
                        String str = wbuf.toString();
                        if (dict.contains(str) && !visited.contains(str))
                            workset.add(new Pair(str, p.length+1));
                    }
                }
                wbuf.setCharAt(i, cur_c);
            }
        }
        return 0;
    }
}

/**
 * Q-128 Longest Consecutive Sequence
 *
 * Given an unsorted array of integers, find the length of the longest consecutive elements sequence. Your algorithm
 * should run in O(n) complexity.
 */
class LongestConsecutiveSequence {
    public int longestConsecutive(int[] nums) {
        Set<Integer> numSet = new HashSet<>();
        for (int n : nums) numSet.add(n);
        int result = 0;
        for (int n : nums) {
            if (!numSet.contains(n)) continue;
            numSet.remove(n);
            int left = n-1, right = n+1;
            while (numSet.contains(left)) {
                numSet.remove(left);
                left--;
            }
            while (numSet.contains(right)) {
                numSet.remove(right);
                right++;
            }
            if (right-left-1 > result)
                result = right-left-1;
        }
        return result;
    }
}

/**
 * Q-130 Surrounded Regions
 *
 * Given a 2D board containing 'X' and 'O' (the letter O), capture all regions surrounded by 'X'. A region is captured
 * by flipping all 'O's into 'X's in that surrounded region.
 */
class SurroundedRegions {
    private void dfs(char[][] board, int r, int c, boolean flip) {
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] != (flip ? 'O' : 'Y'))
            return;
        board[r][c] = flip ? 'Y' : 'O';
        dfs(board, r-1, c, flip);
        dfs(board, r+1, c, flip);
        dfs(board, r, c-1, flip);
        dfs(board, r, c+1, flip);
    }

    public void solve(char[][] board) {
        if (board.length <= 1 || board[0].length <= 1)
            return;
        for (int i = 0; i < board.length; ++i) {
            if (board[i][0] == 'O')
                dfs(board, i, 0, true);
            if (board[i][board[0].length-1] == 'O')
                dfs(board, i, board[0].length-1, true);
        }
        for (int i = 0; i < board[0].length; ++i) {
            if (board[0][i] == 'O')
                dfs(board, 0, i, true);
            if (board[board.length-1][i] == 'O')
                dfs(board, board.length-1, i, true);
        }
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[0].length; ++j)
                if (board[i][j] == 'O')
                    board[i][j] = 'X';
        }
        for (int i = 0; i < board.length; ++i) {
            if (board[i][0] == 'Y')
                dfs(board, i, 0, false);
            if (board[i][board[0].length-1] == 'Y')
                dfs(board, i, board[0].length-1, false);
        }
        for (int i = 0; i < board[0].length; ++i) {
            if (board[0][i] == 'Y')
                dfs(board, 0, i, false);
            if (board[board.length-1][i] == 'Y')
                dfs(board, board.length-1, i, false);
        }
    }
}

/** Q-133 Clone Graph */
class CloneGraph {
    private static class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new ArrayList<Node>();
        }

        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<Node>();
        }

        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }

    public Node cloneGraph(Node node) {
        if (node == null)
            return null;
        Map<Node, Node> cloneMap = new HashMap<>();
        cloneMap.put(node, new Node(node.val));
        Deque<Node> queue = new ArrayDeque<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            Node n = queue.pop();
            Node nclone = cloneMap.get(n);
            for (Node neighbour : n.neighbors) {
                if (!cloneMap.containsKey(neighbour)) {
                    cloneMap.put(neighbour, new Node(neighbour.val));
                    queue.add(neighbour);
                }
                nclone.neighbors.add(cloneMap.get(neighbour));
            }
        }
        return cloneMap.get(node);
    }
}

/**
 * Q-134 Gas Station
 *
 * There are N gas stations along a circular route, where the amount of gas at station i is gas[i]. You have a car with
 * an unlimited gas tank and it costs cost[i] of gas to travel from station i to its next station (i+1). You begin the
 * journey with an empty tank at one of the gas stations. Return the starting gas station's index if you can travel
 * around the circuit once, otherwise return -1.
 */
class GasStation {
    public static int canCompleteCircuit(int[] gas, int[] cost) {
        int total = 0, start = 0, need = 0;
        for (int i = 0; i < gas.length; ++i) {
            total += gas[i];
            if (total >= cost[i]) {
                total -= cost[i];
            } else {
                need += cost[i] - total;
                total = 0;
                start = i + 1;
            }
        }
        return (start > gas.length || total < need) ? -1 : start;
    }

    public static void test() {
        System.out.println("Q-134 Gas Station");
        System.out.println(canCompleteCircuit(new int[] {1,2,3,4,5}, new int[] {3,4,5,1,2}));
        System.out.println(canCompleteCircuit(new int[] {2,3,4}, new int[] {3,4,3}));
    }
}

/**
 * Q-137 Single Number II
 *
 * Given a non-empty array of integers, every element appears three times except for one, which appears exactly once.
 * Find that single one. Your algorithm should have a linear runtime complexity. Could you implement it without using
 * extra memory?
 */
class SingleNumber {
    public int singleNumber(int[] nums) {
        int result = 0;
        for (int i = 0; i < 32; ++i) {
            int s = 0;
            for (int j = 0; j < nums.length; ++j) {
                s += ((nums[j] >> i) & 1);
            }
            result |= ((s%3) << i);
        }
        return result;
    }
}

/**
 * Q-139 Word Break
 *
 * Q-140 Word Break II
 *
 * Given a non-empty string s and a dictionary wordDict containing a list of non-empty words, add spaces in s to
 * construct a sentence where each word is a valid dictionary word. Return all such possible sentences.
 */
class WordBreak {
    // Q-139
    public static boolean wordBreak(String s, Set<String> dict) {
        // hasWord[i] remembers the length of longest word ends at [i]
        int hasWord[] = new int[s.length()];
        for (int i = 0; i < s.length(); ++i) {
            String w = s.substring(0, i+1);
            if (dict.contains(w)) {
                hasWord[i] = i+1;
            }

            if (hasWord[i] == 0) {
                for (int j = i; j >= 0; --j) {
                    w = s.substring(j, i+1);
                    // mark hasWord[i] iff prefix is word as well
                    if (dict.contains(w) && (j == 0 || hasWord[j-1] != 0)) {
                        hasWord[i] = i-j+1;
                        break;
                    }
                }
            }
        }
        System.out.println(s + " is " + (hasWord[s.length()-1] != 0 ? "word" : "not word"));
        return hasWord[s.length()-1] != 0;
    }

    // Q-140
    public static List<String> wordBreak2(String s, List<String> wordDict) {
        Map<String, Set<String>> memo = new HashMap<>();
        Set<String> dict = new HashSet<>();
        for (String w : wordDict)
            dict.add(w);
        List<String> result = new ArrayList<>(helper(s, dict, memo));
        System.out.println(result);
        return result;
    }

    private static Set<String> helper(String s, Set<String> dict, Map<String, Set<String>> memo) {
        if (s.length() == 0) {
            Set<String> r = new HashSet<>();
            r.add("");
            return r;
        }
        if (memo.containsKey(s))
            return memo.get(s);
        Set<String> result = new HashSet<>();
        for (int i = 0; i < s.length(); ++i) {
            String prefix = s.substring(0, i+1);
            if (dict.contains(prefix)) {
                String suffix = s.substring(i+1);
                Set<String> suffixes = helper(suffix, dict, memo);
                for (String ss : suffixes) {
                    String s2 = ss.isEmpty() ? prefix : prefix + " " + ss;
                    if (!result.contains(s2))
                        result.add(s2);
                }
            }
        }
        memo.put(s, result);
        return result;
    }

    public static void run() {
        System.out.println("--------- Word Break -----------");
        Set<String> dict = new TreeSet<String>();
        dict.add("leet");
        dict.add("code");
        dict.add("love");
        dict.add("lover");
        dict.add("chair");
        dict.add("reading");
        dict.add("ear");
        dict.add("a");
        dict.add("b");
        wordBreak("leetcod", dict);
        wordBreak("loverchair", dict);
        wordBreak("lovereading", dict);
        wordBreak("eareading", dict);
        wordBreak("ab", dict);

        String[] dict2 = {"cat", "cats", "and", "sand", "dog"};
        wordBreak2("catsanddog", Arrays.asList(dict2));
    }
}

/**
 * Q-146 LRU Cache
 */
class LRUCache {
    private final int capacity;
    Map<Integer, Integer> cache = new HashMap<>(); // ConcurrentHashMap
    LinkedList<Integer> accessList = new LinkedList<>(); // need lock

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        if (cache.containsKey(key)) {
            int value = cache.get(key);
            // move to the head of the access list
            accessList.remove(Integer.valueOf(key));
            accessList.addFirst(Integer.valueOf(key));
            return value;
        }
        return -1;
    }

    public void put(int key, int value) {
        boolean keyExists = cache.containsKey(key);
        if (!keyExists && accessList.size() >= capacity) {
            Integer evicted = accessList.pollLast();
            cache.remove(evicted);
        }
        if (keyExists)
            accessList.remove(Integer.valueOf(key));
        cache.put(key, value);
        accessList.addFirst(Integer.valueOf(key));
    }
}

/**
 * Q-150 Evaluate Reverse Polish Notation (stack)
 */
class EvaluateReversePolishNotation {
    public int evalRPN(String[] tokens) {
        Stack<Integer> operands = new Stack<Integer>();
        for (String t : tokens) {
            if (t.equals("+") || t.equals("-") || t.equals("*") || t.equals("/")) {
                int op1 = operands.pop();
                int op2 = operands.pop();
                if (t.equals("+")) {
                    operands.push(op1 + op2);
                } else if (t.equals("-")) {
                    operands.push(op2 - op1);
                } else if (t.equals("*")) {
                    operands.push(op1 * op2);
                } else {
                    operands.push(op2/op1);
                }
            } else {
                int num = Integer.parseInt(t);
                operands.push(num);
            }
        }
        int result = operands.pop();
        return result;
    }
}

/**
 * Q-158 Read N Characters Given Read4 -- read multiple times
 *
 * Given a file and assume that you can only read the file using a given method read4, implement a method read to
 * read n characters. Your method read may be called multiple times.
 */
class ReadNCharactersWithRead4 {
    private int read4(char[] buf) {
        return 0;
    }

    // Q-157. Read N characters given read4
    public int read(char[] buf, int n) {
        char[] readbuf = new char[4];
        int writePtr = 0;
        for (int i = 0; i <= n/4; ++i) {
            int r = read4(readbuf);
            for (int j = 0; j < r; ++j)
                buf[writePtr++] = readbuf[j];
        }
        return n > writePtr ? writePtr : n;
    }

    // II read may be called multiple times
    // file = "abcdefg", do read(1), read(2), read(2), should return "abcde"
    public int read_II(char[] buf, int n) {
        for (int i = 0; i < n; ++i) {
            if (readPos == writePos) {
                writePos = read4(rbuf);
                readPos = 0;
                if (writePos == 0)
                    return i;
            }
            buf[i] = rbuf[readPos++];
        }
        return n;
    }

    private char[] rbuf = new char[4];
    private int readPos = 0, writePos = 0;
}

/**
 * Q-162 Find Peak Element
 *
 * A peak element is an element that is greater than its neighbors. Given an input array nums, where
 * nums[i] != nums[i+1], find a peak element and return its index. The array may contain multiple peaks,
 * in that case return the index to any one of the peaks is fine. You may imagine that nums[-1] = nums[n] = -inf.
 */
class FindPeakElement {
    public static int findPeakElement0(int[] num) {
        int l = 0, r = num.length-1;
        while (l < r) {
            int m = l + (r - l)/2;
            if ((m == 0 || num[m] > num[m-1]) && (m == num.length-1 || num[m] > num[m+1])) {
                return m;
            }
            if (m > l && (Math.abs(num[m] - num[l]) < m-l || num[l] > num[m])) {
                r = m;
            } else { // (Math.abs(num[r] - num[m]) < r-m)
                l = m+1;
            }
        }

        if (l == num.length-1 && (l == 0 || num[l] > num[l-1]))
            return l;
        else if ((l == 0 || num[l] > num[l-1]) && (l == num.length-1 || num[l] > num[l+1]))
            return l;
        return -1;
    }

    public static int findPeakElement(int[] nums) {
        int l = 0, r = nums.length-1;
        while (l < r) {
            int m = l + (r-l)/2;
            if (nums[m] < nums[m+1])
                l = m+1;
            else
                r = m;
        }
        return r;
    }

    public static void test() {
        System.out.println("Q-162 Find Peak Element");
        System.out.println(findPeakElement(new int[] {1,2,1,3,5,6,4}));
        System.out.println(findPeakElement(new int[] {4,2,4,5,6,7}));
    }
}

/**
 * Q-179 Largest Number
 *
 * Given a list of non negative integers, arrange them such that they form the largest number.
 */
class LargestNumber {
    public static String largestNumber(int[] nums) {
        if (nums == null || nums.length == 0)
            return "";

        ArrayList<String> numstrs = new ArrayList<String>();
        for (int n : nums) {
            numstrs.add(String.valueOf(n));
        }

        Collections.sort(numstrs, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if (o1.length() != o2.length() || !o1.equals(o2)) {
                    // compare the first differing number
                    int i1, i2;
                    for (i1 = 0, i2 = 0; i1 < o1.length() && i2 < o2.length(); ++i1, ++i2) {
                        if (o1.charAt(i1) > o2.charAt(i2))
                            return 1;
                        else if (o1.charAt(i1) < o2.charAt(i2))
                            return -1;
                    }
                    // compare the 2 arranged numbers
                    String o1o2 = o1 + o2;
                    String o2o1 = o2 + o1;
                    return (Long.valueOf(o1o2) > Long.valueOf(o2o1)) ? 1 : -1;
                }
                return 0;
            }
        });

        Collections.reverse(numstrs);
        System.out.println(Arrays.deepToString(numstrs.toArray()));
        StringBuilder result = new StringBuilder();
        for (String s : numstrs)
            result.append(s);
        String r = result.toString();
        return r.charAt(0) == '0' ? "0" : r;
    }

    public static void test() {
        System.out.println("Q-179 Largest Numbers");
        System.out.println(largestNumber(new int[] {267,39,3,27}));
        System.out.println(largestNumber(new int[] {824,938,1399,5607,6973,5703,9609,4398,8247})); // "9609938824824769735703560743981399"
    }
}

/** Q-187 Repeated DNA Sequences */
class RepeatedDNASequence {
    private static String restoreSeq(int seq, final char[] seqMap) {
        char[] chars = new char[10];
        for (int i = 0; i < 10; ++i, seq >>= 2) {
            int bits = seq & 3;
            chars[i] = seqMap[bits];
        }
        return String.valueOf(chars);
    }

    public static List<String> findRepeatedDnaSequences(String s) {
        ArrayList<String> result = new ArrayList<String>();
        if (s == null || s.length() < 10)
            return result;

        final int genMask = 3; // 0011
        final int seqMask = 0xfffff; // 20 bits
        final char[] bitsChar = new char[] {'A', 'C', 'G', 'T'};
        HashMap<Character, Integer> charBits = new HashMap<Character, Integer>();
        charBits.put('A', 0x00);
        charBits.put('C', 0x01);
        charBits.put('G', 0x10);
        charBits.put('T', 0x11);

        int seq = 0;
        for (int i = 0; i < 10; ++i) {
            System.out.print(s.charAt(i));
            seq = (seq << 2) | charBits.get(s.charAt(i));
        }
        System.out.println("first: " + restoreSeq(seq, bitsChar));
        HashMap<Integer, Integer> seqCounter = new HashMap<Integer, Integer>();
        seqCounter.put(seq, 1);

        // roll forward the sequence window
        for (int i = 10; i < s.length(); ++i) {
            Integer gbit = charBits.get(s.charAt(i));
            seq = ((seq << 2) & seqMask) | gbit;
            int count = 1;
            if (seqCounter.containsKey(seq)) {
                count = seqCounter.get(seq) + 1;
            }
            seqCounter.put(seq, count);
        }

        for (Iterator iter = seqCounter.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<Integer, Integer> e = (Map.Entry) iter.next();
            System.out.println(restoreSeq(e.getKey(), bitsChar) + "," + e.getValue());
            if (e.getValue() > 1) {
                result.add(restoreSeq(e.getKey(), bitsChar));
            }
        }
        System.out.println(Arrays.toString(result.toArray()));
        return result;
    }

    public static void test() {
        System.out.println("Q-187 Repeated DNA Sequence");
        findRepeatedDnaSequences("AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT");
        //findRepeatedDnaSequences("ACGTGTAACCAAACGTGTAACCGAAATGCAAA");
    }
}

/** Q-202 Happy Number */
class HappyNumber {
    public static boolean isHappy(int n) {
        HashSet<Integer> visited = new HashSet<>();
        for (int num = n, value = 0; num != 1; num = value, value = 0) {
            for (int num1 = num; num1 > 0; num1 /= 10) {
                int d = num1 % 10;
                value += d*d;
            }
            if (visited.contains(value)) return false;
            visited.add(value);
        }
        return true;
    }

    public static void test() {
        System.out.println("Q-202 Happy Number");
        System.out.println(isHappy(19)); // true
        System.out.println(isHappy(11)); // false
    }
}

/**
 * Q-207: Course Schedule
 * Q-210: Course Schedule II
 *
 * There are a total of n courses you have to take, labeled from 0 to n - 1. Some courses
 * may have prerequisites, for example to take course 0 you have to first take course 1,
 * which is expressed as a pair: [0,1]. Given the total number of courses and a list of
 * prerequisite pairs, is it possible for you to finish all courses?
 */
class CourseSchedule {
    // Implementation #1 uses DFS
    public static boolean canFinish1(int numCourses, int[][] prerequisites) {
        if (prerequisites.length == 0)
            return true;

        // construct adjacency list of graph
        HashSet<Integer> courses = new HashSet<Integer>();
        HashMap<Integer, Integer> n_deps = new HashMap<Integer, Integer>();
        HashMap<Integer, List<Integer>> graphs = new HashMap<Integer, List<Integer>>();
        for (int i = 0; i < prerequisites.length; ++i) {
            int src = prerequisites[i][0], dep = prerequisites[i][1];
            if (graphs.containsKey(src)) {
                graphs.get(src).add(prerequisites[i][1]);
            } else {
                ArrayList<Integer> edges = new ArrayList<Integer>();
                edges.add(prerequisites[i][1]);
                graphs.put(src, edges);
            }
            // record the number of courses that depend on a course node
            int count = 0;
            if (n_deps.containsKey(dep)) {
                count = n_deps.get(dep);
            }
            n_deps.put(dep, count+1);
            // record all unique course nodes
            if (!courses.contains(src))
                courses.add(src);
            if (!courses.contains(dep))
                courses.add(dep);
        }

        // find the root node(s) of the graph, such nodes that none depends
        Stack<Integer> workset = new Stack<Integer>();
        for (Integer key : graphs.keySet()) {
            if (!n_deps.containsKey(key))
                workset.push(key);
        }

        // definitely not schedulable if no root found in the graph
        if (workset.isEmpty())
            return false;

        HashSet<Integer> visited = new HashSet<Integer>();
        HashSet<Integer> finished = new HashSet<Integer>();
        while (!workset.isEmpty()) {
            Integer key = workset.peek();
            if (visited.contains(key)) {
                workset.pop();
                finished.add(key);
                continue;
            }

            visited.add(key);
            List<Integer> edges = graphs.get(key);
            if (edges == null) {
                continue;
            }
            for (Integer dep : edges) {
                // cycle detected
                if (!finished.contains(dep) && visited.contains(dep))
                    return false;
                workset.add(dep);
            }
        }

        // all seen course nodes must have been visited and finished
        return courses.size() == finished.size();
    }

    // Implementation #2 uses BFS
    public static boolean canFinish(int numCourses, int[][] prerequisites) {
        if (prerequisites.length == 0)
            return true;

        // graph representation with adjacency list
        List<List<Integer>> graph = new ArrayList<List<Integer>>(numCourses);
        for (int i = 0; i < numCourses; ++i)
            graph.add(new ArrayList<Integer>());

        int totalEdges = 0;
        int[] indegree = new int[numCourses];
        for (int[] e : prerequisites) {
            graph.get(e[0]).add(e[1]);
            indegree[e[1]] += 1;
            totalEdges += 1;
        }

        ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
        for (int i = 0; i < numCourses; ++i)
            if (indegree[i] == 0)
                queue.add(i);

        // do a BFS with queue, only nodes with no in-edges
        while (!queue.isEmpty()) {
            Integer node = queue.poll();
            for (Integer edge : graph.get(node.intValue())) {
                indegree[edge.intValue()] -= 1;
                if (indegree[edge.intValue()] == 0)
                    queue.add(edge);
                totalEdges -= 1;
            }
        }

        return (totalEdges == 0) ? true : false;
    }


    /**
     * Q-210 Given the total number of courses and a list of prerequisite pairs, return the ordering of courses you
     * should take to finish all courses.
     */
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        if (numCourses <= 0)
            return new int[0];

        List<List<Integer>> graphs = new ArrayList<List<Integer>>(numCourses);
        for (int i = 0; i < numCourses; ++i)
            graphs.add(new ArrayList<Integer>());

        int[] indegree = new int[numCourses];
        for (int[] e : prerequisites) {
            indegree[e[0]]++;
            graphs.get(e[1]).add(e[0]);
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; ++i) {
            if (indegree[i] == 0)
                queue.add(i);
        }

        ArrayList<Integer> topo = new ArrayList<>();
        while (!queue.isEmpty()) {
            Integer s = queue.poll();
            for (Integer d : graphs.get(s)) {
                indegree[d]--;
                if (indegree[d] == 0)
                    queue.add(d);
            }
            topo.add(s);
        }

        if (topo.size() < numCourses)
            return new int[0];
        int[] result = new int[numCourses];
        int idx = 0;
        for (Integer c : topo)
            result[idx++] = c;
        return result;
    }

    public static void test() {
        System.out.println("Q-207 Course Schedule");
        int[][] a0 = {{0,1},{1,0}}; // false
        int[][] a1 = {{1,0},{2,0},{2,6},{3,2},{4,1},{5,3},{6,5},{6,3}}; // false
        System.out.println(canFinish(2, a0));
        System.out.println(canFinish(7, a1));
        int[][] a2 = {{1, 0}, {2, 6},{1,7},{6,4},{7,0},{0,5}}; // true
        System.out.println(canFinish(8, a2));
    }
}

/**
 * 209. Minimum Size Subarray Sum
 *
 * Given an array of n positive integers and a positive integer s, find the minimal length of a contiguous subarray
 * of which the sum >= s. If there isn't one, return 0 instead.
 */
class MinimumSizeSubarraySum {
    public int minSubArrayLen(int s, int[] nums) {
        if (nums.length == 0)
            return 0;
        int result = nums.length+1;
        for (int l = 0, r = 0, sum = nums[0]; r < nums.length;) {
            if (sum >= s) {
                if (r-l+1 < result)
                    result = r-l+1;
                sum -= nums[l];
                l++;
            } else {
                r++;
                if (r < nums.length)
                    sum += nums[r];
            }
        }
        return result != nums.length+1 ? result : 0;
    }
}

/**
 * Q-213 House Robber
 */
class HouseRobber2 {
    private static int robIt(int[] nums, int start, int end) {
        int best1 = nums[start], best2 = start+1 < nums.length ? nums[start+1] : 0;
        for (int i = start+2; i < end; ++i) {
            int s = nums[i] + best1;
            best1 = Math.max(best1, best2);
            best2 = s;
        }
        return Math.max(best1, best2);
    }

    // II, houses on a circle
    public static int rob(int[] nums) {
        if (nums.length == 0)
            return 0;
        if (nums.length == 1)
            return nums[0];
        int best1 = robIt(nums, 0, nums.length-1), best2 = robIt(nums, 1, nums.length);
        return Math.max(best1, best2);
    }

    public static void test() {
        System.out.println("Q-213 House Robber");
        System.out.println(rob(new int[]{2,3,2}));
    }
}

/**
 * Q-215 Kth Largest Number in a Array
 */
class KthLargestNumberInArray {
    private static void swap(int[] nums, int i, int j) {
        int t = nums[i];
        nums[i] = nums[j];
        nums[j] = t;
    }

    private static int partition(int[] nums, int left, int right) {
        int pivot = nums[left], pos = left;
        swap(nums, left, right);
        for (int i = left; i <= right; ++i) {
            if (nums[i] > pivot) {
                swap(nums, pos++, i);
            }
        }
        swap(nums, pos, right);
        return pos;
    }

    public static int findKthLargest(int[] nums, int k) {
        int l = 0, r = nums.length-1;
        while (true) {
            int p = partition(nums, l, r);
            if (p == k-1) return nums[p];
            if (p > k-1) r = p-1;
            else l = p+1;
        }
    }

    public static void test() {
        System.out.println("Q-215 Kth Largest Number in a Array");
        System.out.println(findKthLargest(new int[]{3,2,1,5,6,4}, 2));
        System.out.println(findKthLargest(new int[]{3,1,2,4}, 2));
    }
}

/**
 * Q-218 The Skyline Problem
 */
class Skyline {
    public static List<List<Integer>> getSkyline(int[][] buildings) {
        int[][] sideHeights = new int[buildings.length*2][];
        for (int i = 0; i < buildings.length; ++i) {
            sideHeights[i*2] = new int[]{buildings[i][0], 0-buildings[i][2]};
            sideHeights[i*2+1] = new int[]{buildings[i][1], buildings[i][2]};
        }
        Arrays.sort(sideHeights, new Comparator<int[]>() {
            @Override
            public int compare(int[] a1, int[] a2) {
                if (a1[0] == a2[0] && a1[1] == a2[1])
                    return 0;
                else if (a1[0] < a2[0] || (a1[0] == a2[0] && a1[1] < a2[1]))
                    return -1;
                return 1;
            }
        });
        System.out.println(Arrays.deepToString(sideHeights));

        List<List<Integer>> result = new ArrayList<>();
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(10, Collections.reverseOrder());
        maxHeap.add(0); // needed for showing key point at height 0
        for (int i = 0, currMax = 0; i < sideHeights.length; ++i) {
            int[] p = sideHeights[i];
            if (p[1] < 0) { // left side
                int h = 0-p[1];
                if (h > maxHeap.peek()) {
                    result.add(Arrays.asList(new Integer[]{p[0], h}));
                    currMax = h;
                }
                maxHeap.add(h); // sort build heights
            } else { // right side
                maxHeap.remove(p[1]);
                if (p[1] == currMax && currMax != maxHeap.peek()) {
                    currMax = maxHeap.peek();
                    result.add(Arrays.asList(new Integer[]{p[0], currMax}));
                }
            }
        }
        System.out.println(result);
        return result;
    }

    public static void test() {
        System.out.println("Q-218 The Skyline Problem");
        int[][] t = {{2,9,10},{3,7,15},{5,12,12},{15,20,10},{19,24,8}}; // [[2, 10], [3, 15], [7, 12], [12, 0], [15, 10], [20, 8], [24, 0]]
        getSkyline(t);
        int[][] t2 = {{0,2,3},{2,5,3}}; // [[0,3],[5,0]]
        getSkyline(t2);
    }
}

/**
 * Q-220: Contains Duplicates III
 *
 * Given an array of integers, find out whether there are two distinct indices
 * i and j in the array such that the difference between nums[i] and nums[j] is
 * at most t and the difference between i and j is at most k.
 * */
class ContainsDuplicates {
    private static boolean pairExists(int[] nums, int k, int t) {
        if (nums == null || nums.length == 0)
            return false;

        TreeSet<Integer> n_distances = new TreeSet<Integer>();
        n_distances.add(nums[0]);

        for (int i = 1; i < nums.length; ++i) {
            // move forward the window of k elements
            if (i - k > 0) {
                n_distances.remove(nums[i-k-1]);
            }

            if (t >= 0 && n_distances.contains(nums[i]))
                return true;

            Integer lv = n_distances.lower(nums[i]);
            if (lv != null && Math.abs((long)lv.intValue() - (long)nums[i]) <= t)
                return true;
            Integer hv = n_distances.higher(nums[i]);
            if (hv != null && Math.abs((long)hv.intValue() - (long)nums[i]) <= t)
                return true;
            n_distances.add(nums[i]);
        }
        return false;
    }

    public static void run() {
        System.out.println("------- Contains Duplicates ---------");
        int[] nums = {2,100,200,50,300,5,400,53,66,78,50};
        System.out.printf("%s\n", pairExists(nums, 5, 4) ? "true" : "false");
        System.out.printf("%s\n", pairExists(nums, 5, 2) ? "true" : "false");
        System.out.printf("%s\n", pairExists(nums, 5, 3) ? "true" : "false");
        int[] nums1 = {-1,-1};
        System.out.printf("%s\n", pairExists(nums1, 1, 0) ? "true" : "false");
        System.out.printf("%s\n", pairExists(nums1, 1, -1) ? "true" : "false");
        int[] nums2 = {-1,2147483647};
        System.out.printf("%s\n", pairExists(nums2, 1, 2147483647) ? "true" : "false");
    }
}

/**
 * Q-229 Majority Element 2
 *
 * Given an integer array of size n, find all elements that appear more than ⌊ n/3 ⌋ times.
 */
class MajorityElement2 {
    public List<Integer> majorityElement(int[] nums) {
        List<Integer> result = new ArrayList<>();
        if (nums.length < 2) {
            for (int n : nums)
                result.add(n);
            return result;
        }
        int[] cands = new int[2];
        int[] counts = new int[2];
        for (int i = 0; i < nums.length; ++i) {
            if (counts[0] > 0 && cands[0] == nums[i]) {
                counts[0]++;
            } else if (counts[1] > 0 && cands[1] == nums[i]) {
                counts[1]++;
            } else if (counts[0] == 0) {
                cands[0] = nums[i];
                counts[0] = 1;
            } else if (counts[1] == 0) {
                cands[1] = nums[i];
                counts[1] = 1;
            } else {
                counts[0]--;
                counts[1]--;
            }
        }
        counts[0] = counts[1] = 0;
        for (int i = 0; i < nums.length; ++i) {
            if (cands[0] == nums[i])
                counts[0]++;
            else if (cands[1] == nums[i])
                counts[1]++;
        }
        if (counts[0] > nums.length/3)
            result.add(cands[0]);
        if (counts[1] > nums.length/3)
            result.add(cands[1]);
        return result;
    }
}

/**
 * 238. Product of Array Except Self
 */
class ProductOfArrayExceptSelf {
    public int[] productExceptSelf(int[] nums) {
        int[] result = new int[nums.length];
        for (int i = 0, p = 1; i < nums.length; ++i) {
            result[i] = p;
            p *= nums[i];
        }
        for (int i = nums.length-1, p = 1; i >= 0; --i) {
            result[i] *= p;
            p *= nums[i];
        }
        return result;
    }
}

/**
 * Q-241 Different Ways of Add Parentheses
 */
class DifferentWaysOfAddParenthese {
    public static List<Integer> diffWaysToCompute(String input) {
        List<Integer> result = new ArrayList<>();
        boolean isNumber = true;
        for (int i = 0; i <input.length(); ++i) {
            char c = input.charAt(i);
            if (c == '+' || c == '-' || c == '*') {
                isNumber = false;
                List<Integer> lr = diffWaysToCompute(input.substring(0, i));
                List<Integer> rr = diffWaysToCompute(input.substring(i+1));
                for (Integer ln : lr) {
                    for (Integer rn : rr) {
                        if (c == '+')
                            result.add(ln.intValue() + rn.intValue());
                        else if (c == '-')
                            result.add(ln.intValue() - rn.intValue());
                        else
                            result.add(ln.intValue() * rn.intValue());
                    }
                }
            }
        }
        if (isNumber) {
            result.add(Integer.parseInt(input));
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-241 Different Ways to Add Parentheses");
        System.out.println(diffWaysToCompute("2-1-1"));
        System.out.println(diffWaysToCompute("2*3-4*5"));
    }
}

/**
 * Q-243 Shortest Word Distance
 */
class ShortestWordDistance {
    public static int shortestDistance1(String[] words, String word1, String word2) {
        int result = words.length+1;
        for (int i = 0, p1 = -words.length, p2 = -words.length; i < words.length; ++i) {
            if (words[i].equals(word1)) {
                p1 = i;
                if (p1 - p2 < result)
                    result = p1-p2;
            } else if (words[i].equals(word2)) {
                p2 = i;
                if (p2 - p1 < result)
                    result = p2-p1;
            }
        }
        return result;
    }

    public static int shortestDistance3(String[] words, String word1, String word2) {
        if (!word1.equals(word2))
            return shortestDistance1(words, word1, word2);

        int result = words.length+1;
        for (int i = 0, prev = -words.length; i < words.length; ++i) {
            if (!words[i].equals(word1)) continue;
            if (i-prev < result)
                result = i - prev;
            prev = i;
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-243 Shortest Word Distance");
        String[] strs = {"practice","makes","perfect","coding","makes"};
        System.out.println(shortestDistance1(strs, "practice", "coding")); // 3
        System.out.println(shortestDistance1(strs, "makes", "coding")); // 1
        System.out.println(shortestDistance3(strs, "makes", "makes")); // 3
    }
}

/**
 * Q-253 Meeting Rooms II
 *
 * Given an array of meeting time intervals consisting of start and end times [[s1,e1],[s2,e2],...] (si < ei),
 * find the minimum number of conference rooms required.
 */
class MeetingRooms2 {
    public static int minMeetingRooms(int[][] intervals) {
        int[] starts = new int[intervals.length];
        int[] ends = new int[intervals.length];
        for (int i = 0; i < intervals.length; ++i) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        Arrays.sort(starts);
        Arrays.sort(ends);

        // track the max number of overlapping intervals at any point
        int result = 0, overlap = 0;
        for (int si = 0, ei = 0; si < starts.length;) {
            if (starts[si] < ends[ei]) {
                if (++overlap > result)
                    result = overlap;
                si++;
            } else {
                overlap--;
                ei++;
            }
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-253 Meeting Rooms II");
        int[][] intervals = {{0,30},{5,10},{15,20}};
        System.out.println(minMeetingRooms(intervals)); // 2
        int[][] intervals2 = {{13,15},{1,13}};
        System.out.println(minMeetingRooms(intervals2)); // 1
    }
}

/**
 * Q-255 Verify Preorder Sequence in Binary Search Tree
 */
class VerifyPreorderSequenceInBST {
    public boolean verifyPreorder(int[] nums) {
        int low = Integer.MIN_VALUE;
        Stack<Integer> stack = new Stack<>();
        for (int n : nums) {
            if (n < low)
                return false;
            while (!stack.empty() && stack.peek() < n) {
                low = stack.pop();
            }
            stack.push(n);
        }
        return true;
    }
}

/**
 * Q-260 Single Number 3
 *
 * Given an array of numbers nums, in which exactly two elements appear only once and all the other elements appear
 * exactly twice. Find the two elements that appear only once.
 */
class SingleNumber3 {
    public int[] singleNumber(int[] nums) {
        int xor = 0;
        for (int n : nums)
            xor ^= n;
        int rightmost = xor & -xor; // get the rightmost 1
        int first = 0;
        for (int n : nums)
            if ((n & rightmost) != 0)
                first ^= n;
        int second = xor ^ first;
        return new int[]{first, second};
    }
}

/**
 * Q-269 Alien Dictionary
 *
 * There is a new alien language which uses the latin alphabet. However, the order among letters are unknown to you.
 * You receive a list of non-empty words from the dictionary, where words are sorted lexicographically by the rules
 * of this new language. Derive the order of letters in this language.
 */
class AlienDictionary {
    public static String alienOrder(String[] words) {
        Set<Character> chars = new HashSet<>();
        Map<Character, Integer> indegree = new HashMap<>();
        for (String w : words) {
            for (char c : w.toCharArray()) {
                chars.add(c);
                indegree.put(c, 0);
            }
        }
        Map<Character, List<Character>> edges = new HashMap<>();
        for (int i = 0; i < words.length-1; ++i) {
            String w1 = words[i], w2 = words[i+1];
            for (int p1 = 0, p2 = 0; p1 < w1.length() && p2 < w2.length(); p1++, p2++) {
                char c1 = w1.charAt(p1), c2 = w2.charAt(p2);
                if (w1.charAt(p1) != w2.charAt(p2)) {
                    List<Character> e = edges.containsKey(c1) ? edges.get(c1) : new ArrayList<>();
                    e.add(c2);
                    edges.put(c1, e);
                    int count2 = indegree.containsKey(c2) ? indegree.get(c2)+1 : 1;
                    indegree.put(c2, count2);
                    break;
                }
            }
        }

        StringBuilder result = new StringBuilder("");
        Deque<Character> queue = new ArrayDeque<>();
        for (Character c : chars) {
            if (indegree.get(c) == 0) {
                queue.add(c);
                // char with 0 in-edge are sorted to the head, because OJ expects so
                result.append(c);
            }
        }
        while (!queue.isEmpty()) {
            Character c = queue.pop();
            if (edges.containsKey(c)) {
                for (Character d : edges.get(c)) {
                    int count = indegree.get(d) - 1;
                    if (count == 0) {
                        queue.push(d);
                        result.append(d);
                    }
                    indegree.put(d, count);
                }
            }
        }
        // detect existence of cycle, nodes in cycle will never reach 0 in-edge
        String alphabets = result.toString();
        return alphabets.length() == chars.size() ? alphabets : "";
    }

    public static void test() {
        System.out.println("Q-269 Alien Dictionary");
        System.out.println(alienOrder(new String[]{"wrt","wrf","er", "ett", "rftt"})); // wertf
        System.out.println(alienOrder(new String[]{"z","z"})); // z
        System.out.println(alienOrder(new String[]{"ab","adc"})); // abcd
    }
}

/**
 * Q-271 Encode and Decode Strings
 *
 * Design an algorithm to encode a list of strings to a string. The encoded string is then sent over the network
 * and is decoded back to the original list of strings.
 */
class EncodeAndDecodeStrings {

    // Encodes a list of strings to a single string.
    public static String encode(List<String> strs) {
        StringBuilder encoded = new StringBuilder("");
        encoded.append(Integer.toString(strs.size()));
        encoded.append(',');
        for (String s : strs) {
            encoded.append(Integer.toString(s.length()));
            encoded.append(',');
        }
        for (String s : strs) {
            encoded.append(s);
        }
        return encoded.toString();
    }

    // Decodes a single string to a list of strings.
    public static List<String> decode(String s) {
        int idx = s.indexOf(',');
        int total = Integer.parseInt(s.substring(0, idx));
        int[] lens = new int[total];
        for (int i = 0; i < total; ++i) {
            int p = s.indexOf(',', idx+1);
            String ns = s.substring(idx+1, p);
            lens[i] = Integer.parseInt(ns);
            idx = p;
        }
        idx++; // skip the last ','
        System.out.println(Arrays.toString(lens));

        List<String> result = new ArrayList<>();
        for (int i = 0; i < total; ++i) {
            int l = lens[i];
            result.add(s.substring(idx, idx + l));
            idx += l;
        }
        System.out.println(result.toString());
        return result;
    }

    public static void test() {
        System.out.println("Q-271 Encode and Decode Strings");
        String[] ts = {"this","is,not a","test","leetcode"};
        decode(encode(Arrays.asList(ts)));
        //System.out.println();
    }
}

/**
 * Q-273 Integer to English Words
 *
 * Convert a non-negative integer to its english words representation. Given input is guaranteed to be less
 * than 2^31 - 1.
 */
class IntegerToEnglishWords {
    static String[] teens = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    static String[] tens = {"Zero", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
    static String[] thousands = {"", " Thousand", " Million", " Billion"};

    public static String numberToWords(int num) {
        if (num == 0) return tens[0];
        Stack<String> s = new Stack<>();
        for (int i = 0, n = num; n > 0; i++, n /= 1000) {
            int n3 = n%1000;
            if (n3 != 0)
                s.push(convert(n3) + thousands[i]);
        }
        StringBuilder result = new StringBuilder("");
        while (!s.empty())
            result.append(s.pop()).append(" ");
        return result.toString().trim();
    }

    public static String convert(int num) {
        StringBuilder result = new StringBuilder("");
        int h = num/100;
        if (h > 0)
            result.append(teens[h]).append(" Hundred ");
        int r = num%100;
        if (r > 0) {
            int t = r/10, d = r%10;
            if (r >= 20)
                result.append(tens[t]).append(" ").append(teens[d]);
            else
                result.append(teens[r]);
        }
        return result.toString().trim();
    }

    public static void test() {
        System.out.println("Q-273 Integer to English Words");
        System.out.println(numberToWords(0));
        System.out.println(numberToWords(5));
        System.out.println(numberToWords(17));
        System.out.println(numberToWords(200));
        System.out.println(numberToWords(503));
        System.out.println(numberToWords(12345));
        System.out.println(numberToWords(1234567));
        System.out.println(numberToWords(1000000));
    }
}

/**
 * Q-274 H-Index
 *
 * Given an array of citations (each citation is a non-negative integer) of a researcher, write a function to compute
 * the researcher's h-index. According to the definition of h-index on Wikipedia: "A scientist has index h if h of
 * his/her N papers have at least h citations each, and the other N − h papers have no more than h citations each."
 *
 * H-Index 2:
 *
 * Given an array of citations sorted in ascending order (each citation is a non-negative integer) of a researcher,
 * write a function to compute the researcher's h-index.
 */
class HIndex {
    public static int hIndex(int[] citations) {
        // wiki solution
        Arrays.sort(citations);
        for (int l = 0, r = citations.length-1; l < r; ++l, --r) {
            int t = citations[l];
            citations[l] = citations[r];
            citations[r] = t;
        }
        // descending order of citations, break at the first index which fails the citation check
        for (int i = 0; i < citations.length; ++i) {
            if (i >= citations[i])
                return i;
        }
        return citations.length;
    }

    public static int hIndex2(int[] citations) {
        int l = 0, r = citations.length-1, N = citations.length;
        // can't use (l < r)
        while (l <= r) {
            int m = l +(r-l)/2;
            if (citations[m] >= N-m) r = m-1;
            else l = m+1;
        }
        return N-l;
    }

    public static void test () {
        System.out.println("Q-274 H Index");
        System.out.println(hIndex(new int[]{3,0,6,1,5}));
        System.out.println(hIndex2(new int[]{0, 1, 3, 5, 6}));
        System.out.println(hIndex2(new int[]{0}));
    }
}

/**
 * Q-282 Expression Add Operators
 *
 * Given a string that contains only digits 0-9 and a target value, return all possibilities to add binary operators
 * (not unary) +, -, or * between the digits so they evaluate to the target value.
 */
class ExpressionAddOperators {
    private static void helper(String num, final long target, long currValue, String expr, long last, List<String> result) {
        if (num.isEmpty() && target == currValue) {
            result.add(expr);
            return;
        }
        for (int i = 0; i < num.length(); ++i) {
            String ns = num.substring(0, i+1);
            if (ns.length() > 1 && ns.charAt(0) == '0') return; // skip "00"
            String num2 = num.substring(i+1);
            long num1 = Long.parseLong(ns);
            if (expr.isEmpty()) {
                helper(num2, target, num1, ns, num1, result);
            } else {
                helper(num2, target, currValue+num1, expr + "+" + ns, num1, result);
                helper(num2, target, currValue-num1, expr + "-" + ns, -num1, result);
                // 235 - prev iteration: 2-3, now: 2-3*5
                helper(num2, target, (currValue-last)+last*num1, expr + '*' + ns, last*num1, result);
            }
        }
    }

    public static List<String> addOperators(String num, int target) {
        List<String> result = new ArrayList<>();
        helper(num, target, 0, "", 0, result);
        return result;
    }

    public static void test() {
        System.out.println("Q-282 Expression Add Operators");
        System.out.println(addOperators("123", 6)); // ["1+2+3", "1*2*3"]
        System.out.println(addOperators("232", 8)); // ["2*3+2", "2+3*2"]
        System.out.println(addOperators("105", 5)); // ["1*0+5","10-5"]

    }
}

/**
 * Q-286 Walls and Gates
 */
class WallsAndGates {
    private void dfs(int[][] rooms, int x, int y, int distance) {
        if (x < 0 || x >= rooms.length || y < 0 || y >= rooms[0].length || distance > rooms[x][y])
            return;
        rooms[x][y] = distance;
        dfs(rooms, x-1, y, distance+1);
        dfs(rooms, x, y-1, distance+1);
        dfs(rooms, x+1, y, distance+1);
        dfs(rooms, x, y+1, distance+1);
    }

    public void wallsAndGates(int[][] rooms) {
        for (int i = 0; i < rooms.length; ++i) {
            for (int j = 0; j < rooms[0].length; ++j) {
                if (rooms[i][j] == 0) // gate
                    dfs(rooms, i, j, 0);
            }
        }
    }
}

/**
 * Q-287 Find the Duplicate Number
 *
 * Given an array nums containing n + 1 integers where each integer is between 1 and n (inclusive), prove that at
 * least one duplicate number must exist. Assume that there is only one duplicate number, find the duplicate one.
 */
class FindDuplicateNumber {
    public static int findDuplicate(int[] nums) {
        // can't modify the array so can't sort
        int l = 0, r = nums.length-1;
        while (l < r) {
            int m = l + (r-l)/2, cnt  = 0;
            for (int i = 0; i < nums.length; ++i) {
                if (nums[i] <= m)
                    cnt++;
            }
            if (cnt <= m) l = m+1;
            else r = m;
        }
        return r;
    }

    public static void test() {
        System.out.println("Q-287 Find the Duplicate Number");
        System.out.println(findDuplicate(new int[]{1,1,4,2,3}));
        System.out.println(findDuplicate(new int[]{2,3,4,1,1}));
    }
}

/**
 * Q-289 Game of Life
 */
class GameOfLife {
    // compute the next state (after one update) of the board given its current state
    public static void gameOfLife(int[][] board) {
        int M = board.length, N = board[0].length;
        int[] rowd = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] cold = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < M; ++i) {
            for (int j = 0; j < N; ++j) {
                int cnt = 0;
                for (int k = 0; k < 8; ++k) {
                    int nr = i+rowd[k], nc = j+cold[k];
                    // 0 -> -1 is re-generate, 1 -> 2 is die
                    if (nr >= 0 && nr < M && nc >= 0 && nc < N && board[nr][nc] > 0)
                        cnt++;
                }
                if (board[i][j] == 0 && cnt == 3) board[i][j] = -1;
                if (board[i][j] == 1 && (cnt < 2 || cnt > 3)) board[i][j] = 2;
            }
        }
        for (int i = 0; i < M; ++i) {
            for (int j = 0; j < N; ++j) {
                if (board[i][j] == -1) board[i][j] = 1;
                else if (board[i][j] == 2) board[i][j] = 0;
            }
        }
    }

    public static void test() {
        System.out.println("Q-289 Game of Life");
        int[][] m = {{0,1,0},{0,0,1},{1,1,1},{0,0,0}};
        gameOfLife(m);
    }
}

/** Q-290 Word Pattern */
class WordPattern {
    public static boolean wordPattern(String pattern, String str) {
        if (pattern == null && str == null)
            return true;
        else if (pattern == null || str == null)
            return false;

        String strs[] = str.split(" ");
        if (pattern.length() != strs.length)
            return false;

        HashMap<Character, StringBuffer> pmap = new HashMap<Character, StringBuffer>();
        for (int i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            if (!pmap.containsKey(c)) {
                pmap.put(c, new StringBuffer(Integer.toString(i)));
            } else {
                pmap.get(c).append(Integer.toString(i));
            }
        }

        HashMap<String, StringBuffer> smap = new HashMap<String, StringBuffer>();
        for (int i = 0; i < strs.length; ++i) {
            String s = strs[i];
            if (!smap.containsKey(s)) {
                smap.put(s, new StringBuffer(Integer.toString(i)));
            } else {
                smap.get(s).append(Integer.toString(i));
            }
        }

        for (int i = 0; i < pattern.length(); ++i) {
            String ps = pmap.get(pattern.charAt(i)).toString();
            String ss = smap.get(strs[i]).toString();
            if (!ps.equals(ss))
                return false;
        }
        return true;
    }
}

/**
 * Q-295 Find Median from Data Stream
 */
class FindMedianFromDataStream {
    PriorityQueue<Integer> firstHalf = new PriorityQueue<>(10, Collections.reverseOrder()); // first half
    PriorityQueue<Integer> secondHalf = new PriorityQueue<>(10); // second half

    /** initialize your data structure here. */
    public FindMedianFromDataStream() {}

    public void addNum(int num) {
        if (firstHalf.size() == 0) {
            firstHalf.add(num);
            return;
        } else if (secondHalf.size() == 0) {
            if (num > firstHalf.peek())
                secondHalf.add(num);
            else {
                secondHalf.add(firstHalf.poll());
                firstHalf.add(num);
            }
            return;
        }
        // invariance is firstHalf size is equal or 1 greater than secondHalf
        if (firstHalf.size() == secondHalf.size()) {
            if (num > secondHalf.peek()) {
                firstHalf.add(secondHalf.poll());
                secondHalf.add(num);
            } else {
                firstHalf.add(num);
            }
        } else {
            if (num >= firstHalf.peek())
                secondHalf.add(num);
            else {
                secondHalf.add(firstHalf.poll());
                firstHalf.add(num);
            }
        }
    }

    public double findMedian() {
        return firstHalf.size() > secondHalf.size() ? firstHalf.peek()*1.0 : (firstHalf.peek()+secondHalf.peek())*0.5;
    }
}

/**
 * Q-296 Best meeting point
 *
 * A group of two or more people wants to meet and minimize the total travel distance. You are given a 2D grid of
 * values 0 or 1, where each 1 marks the home of someone in the group. The distance is calculated using Manhattan
 * Distance, where distance(p1, p2) = |p2.x - p1.x| + |p2.y - p1.y|.
 */
class BestMeetingPoint {
    private int minTotalDistance(List<Integer> coordinates) {
        Collections.sort(coordinates);
        int total = 0;
        for (int l = 0, r = coordinates.size()-1; l < r; l++, r--) {
            total += r-l;
        }
        return total;
    }

    public int bestMeet(int[][] grid) {
        ArrayList<Integer> rows = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        for (int i = 0; i < grid.length; ++i) {
            for (int j = 0; j < grid[0].length; ++j) {
                if (grid[i][j] == 1) {
                    rows.add(i);
                    cols.add(j);
                }
            }
        }
        return minTotalDistance(rows) + minTotalDistance(cols);
    }
}

/**
 * Q-301 Remove Invalid Parentheses
 *
 * Remove the minimum number of invalid parentheses in order to make the input string valid. Return all possible
 * results. The input string may contain letters other than the parentheses '(' and ')'.
 */
class RemoveInvalidParentheses {
    public static List<String> removeInvalidParentheses(String s) {
        List<String> result = new ArrayList<>();
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        queue.add(s);
        visited.add(s);
        boolean found = false;
        while (!queue.isEmpty()) {
            String str = queue.poll();
            if (valid(str)) {
                result.add(str);
                found = true;
            }
            // found the valid with minimal removal, no need of search with more removals
            if (found) continue;
            for (int i = 0; i < str.length(); ++i) {
                if (str.charAt(i) == '(' || str.charAt(i) == ')') {
                    String str2 = "" + str.substring(0, i) + str.substring(i+1);
                    if (!visited.contains(str2)) {
                        queue.add(str2);
                        visited.add(str2);
                    }
                }
            }
        }
        return result;
    }

    // check if the string has valid parentheses
    private static boolean valid(String s) {
        int left = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '(') left++;
            else if (s.charAt(i) == ')')
                if (--left < 0) return false;
        }
        return left == 0;
    }

    public static void test() {
        System.out.println("Q-301 Remove Invalid Parentheses");
        System.out.println(removeInvalidParentheses("()())()")); // ()()(), (())()
        System.out.println(removeInvalidParentheses(")(")); // ""
        System.out.println(removeInvalidParentheses("(a)())()")); // (a())(), (a)()()
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
 * Q-280 Wiggle Sort
 *
 * Given an unsorted array nums, reorder it in-place such that nums[0] <= nums[1] >= nums[2] <= nums[3] ...
 *
 * Q-324 Wiggle Sort 2
 *
 * Given an unsorted array nums, reorder it such that nums[0] < nums[1] > nums[2] < nums[3]....
 */
class WiggleSort {
    public static void wiggleSort1(int[] nums) {
        Arrays.sort(nums);
        for (int i = 1; i < nums.length-1; i += 2) {
            int t = nums[i];
            nums[i] = nums[i+1];
            nums[i+1] = t;
        }
        System.out.println(Arrays.toString(nums));
    }

    public static void wiggleSort(int[] nums) {
        int[] c = Arrays.copyOf(nums, nums.length);
        Arrays.sort(c);
        for (int i = 0, j = nums.length%2 == 0 ? nums.length/2-1 : nums.length/2, k = nums.length-1;
             i < nums.length; ++i) {
            if (i % 2 == 0)
                nums[i] = c[j--];
            else
                nums[i] = c[k--];
        }
        System.out.println(Arrays.toString(nums));
    }

    public static void test() {
        System.out.println("Q-324 Wiggle Sort 2");
        wiggleSort1(new int[]{1,5,2,3,6,4,7});
        wiggleSort(new int[]{1,5,1,1,6,4});
    }
}

/**
 * Q-325 Maximum Size Subarray Sum Equals K
 *
 * Given an array nums and a target value k, find the maximum length of a subarray that sums to k. If there isn't
 * one, return 0 instead.
 */
class MaximumSizeSubarraySumEqualsK {
    public static int maxSubArrayLen(int[] nums, int k) {
        int result = 0;
        Map<Integer, Integer> sumPos = new HashMap<>();
        for (int i = 0, sum = 0; i < nums.length; ++i) {
            sum += nums[i];
            if (sum == k)
                result = i + 1;
            else if (sumPos.containsKey(Integer.valueOf(sum - k)))
                result = Math.max(result, i - sumPos.get(Integer.valueOf(sum - k)));
            if (!sumPos.containsKey(Integer.valueOf(sum)))
                sumPos.put(Integer.valueOf(sum), i); // remember the shortest prefix sum
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-325 Maximum Size Subarray Sum Equals K");
        System.out.println(maxSubArrayLen(new int[]{-2,-1,2,1}, 1)); // 2
        System.out.println(maxSubArrayLen(new int[]{1,0,-1}, -1)); // 2
        System.out.println(maxSubArrayLen(new int[]{1,1,0}, 1)); // 2
    }
}

/** Q-331 Verify Preorder Serialization of Binary Tree */
class VerifyPreorderSerializationBinaryTree {
    public static boolean isValidSerialization(String preorder) {
        return true;
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
 * Q-334 Increasing Triplet Subsequence
 */
class IncreasingTripletSubsequence {
    public static boolean increasingTriplet(int[] nums) {
        if (nums.length < 3)
            return false;
        int m1 = nums[0], m2 = Integer.MAX_VALUE, count = 1;
        for (int i = 1; i < nums.length; ++i) {
            if (nums[i] > m2) {
                return true;
            } else if (nums[i] > m1) {
                m2 = nums[i];
                if (count < 2) {
                    count++;
                }
            } else {
                m1 = nums[i];
            }
        }
        return false;
    }

    public static void test() {
        System.out.println("Q-334 Increasing Triplet Subsequence");
        System.out.println(increasingTriplet(new int[]{4,3,1,2,5}));
    }
}

/**
 * Q-340 Longest Substring with At Most K Distinct Characters
 *
 * Given a string, find the length of the longest substring T that contains at most k distinct characters.
 */
class LongestSubstringWithAtMostKDistinctCharacters {
    public static int lengthOfLongestSubstringKDistinct(String s, int k) {
        int result = 0;
        Map<Character, Integer> countMap = new HashMap<>();
        for (int i = 0, j = 0; i < s.length(); ++i) {
            Character c = s.charAt(i);
            int count = countMap.containsKey(c) ? countMap.get(c) + 1 : 1;
            countMap.put(c, count);
            while (countMap.size() > k) {
                Character lc = s.charAt(j++);
                count = countMap.get(lc) - 1;
                if (count == 0) countMap.remove(lc);
                else countMap.put(lc, count);
            }
            if (i-j+1 > result) result = i-j+1;
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-340 Longest Substring with At Most K Distinct Characters");
        System.out.println(lengthOfLongestSubstringKDistinct("eceba", 2)); // 3
        System.out.println(lengthOfLongestSubstringKDistinct("aa", 1)); // 2
    }
}

/** Q-349 Intersection of Two Arrays */
class IntersectionOfTwoArrays {
    public int[] intersection(int[] nums1, int[] nums2) {
        HashSet<Integer> num1set = new HashSet<>();
        for (int n : nums1)
            num1set.add(n);
        // each element in the result must be unique
        HashSet<Integer> intersect = new HashSet<>();
        for (int n : nums2)
            if (num1set.contains(n))
                intersect.add(n);
        // the stream transformation is slower (5ms) than the verbose code (2ms) in leetcode OJ
        int[] result = intersect.stream().mapToInt(i -> i).toArray();
        /*int[] result = new int[intersect.size()];
        int i = 0;
        for (Integer n : intersect)
            result[i++] = n;*/
        return result;
    }
}

/**
 * Q-380 Insert Delete GetRandom O(1)
 */
class InsertDeleteGetRandomO1 {
    // ReentrantLock
    private List<Integer> values = new ArrayList<>();
    private Map<Integer, Integer> valueMap = new HashMap<>(); // val -> values[] index

    /** Initialize your data structure here. */
    public InsertDeleteGetRandomO1() {}

    /** Inserts a value to the set. Returns true if the set did not already contain the specified element. */
    public boolean insert(int val) {
        if (!valueMap.containsKey(val)) {
            valueMap.put(val, values.size());
            values.add(val);
            return true;
        }
        return false;
    }

    /** Removes a value from the set. Returns true if the set contained the specified element. */
    public boolean remove(int val) {
        if (valueMap.containsKey(val)) {
            final int index = valueMap.get(val);
            if (index == values.size()-1) {
                values.remove(values.size()-1);
            } else {
                values.set(index, values.remove(values.size() - 1));
                valueMap.put(values.get(index), index);
            }
            valueMap.remove(val);
            return true;
        }
        return false;
    }

    /** Get a random element from the set. */
    public int getRandom() {
        final int index = new Random().nextInt(values.size());
        return values.get(index);
    }
}

/**
 * Q-426 Convert Binary Search Tree to Sorted Doubly Linked List
 */
class ConvertBinarySearchTreeToSortedDoublyLinkedList {
    public Node treeToDoublyList(Node root) {
        if (root == null) return null;
        Node left = treeToDoublyList(root.left);
        Node right = treeToDoublyList(root.right);
        Node head = left != null ? left : root;
        if (left != null) {
            left.left.right = root;
            root.left = left.left;
        }
        Node tail = right != null ? right.left : root;
        if (right != null) {
            right.left = root;
            root.right = right;
        }
        head.left = tail;
        tail.right = head;
        return head;
    }
}

/** Q-436 Find Right Interval */
class FindRightInterval {
    public static int[] findRightInterval(Interval[] intervals) {
        int[] starts = new int[intervals.length];
        HashMap<Integer, Integer> startIntervalMap = new HashMap<Integer, Integer>();
        // sort interval starts, and map start to interval
        for (int i = 0; i < intervals.length; ++i) {
            starts[i] = intervals[i].start;
            startIntervalMap.put(intervals[i].start, i);
        }
        Arrays.sort(starts); // sort all interval start indices
        int[] result = new int[intervals.length];
        for (int i = 0; i < intervals.length; ++i) {
            int j;
            for (j = 0; j < starts.length; ++j) {
                if (starts[j] >= intervals[i].end) {
                    result[i] = startIntervalMap.get(starts[j]);
                    break;
                }
            }
            if (j >= starts.length)
                result[i] = -1;
        }
        System.out.println(Arrays.toString(result));
        return result;
    }

    public static void test() {
        System.out.println("Q-436 Find Right Interval");
        Interval[] i1 = new Interval[]{new Interval(1,4), new Interval(2,3), new Interval(3,4)}; // [-1,2,-1]
        Interval[] i2 = new Interval[]{new Interval(3,4), new Interval(2,3), new Interval(1,2)}; // [-1,0,1]
        findRightInterval(i2);
    }
}


public class LeetcodeOne {
    public static void main(String[] args) {
        SimplifyPath.run();
        LongestSubstringWithoutRepeatingChars.test();
        FindFirstLastPosSortedArray.test();
        ThreeSum.test();
        LetterComboOfPhoneNumber.run();
        CombinationSum.test();
        Permutations.test();
        Subsets.test();
        MergeIntervals.test();
        AddBinary.test();
        JumpGame.test();
        GrayCode.test();
        SortColors.test();
        RemoveDuplicatesFromSortedArray.test();
        RestoreIpAddress.test();
        GasStation.test();
        WordBreak.run();
        FindPeakElement.test();
        LargestNumber.test();
        HappyNumber.test();
        CourseSchedule.test();
        RepeatedDNASequence.test();
        HouseRobber2.test();
        KthLargestNumberInArray.test();
        Skyline.test();
        DifferentWaysOfAddParenthese.test();
        MeetingRooms2.test();
        AlienDictionary.test();
        EncodeAndDecodeStrings.test();
        HIndex.test();
        ExpressionAddOperators.test();
        FindDuplicateNumber.test();
        IntegerToEnglishWords.test();
        GameOfLife.test();
        RemoveInvalidParentheses.test();
        WiggleSort.test();
        MaximumSizeSubarraySumEqualsK.test();
        ReconstructItinerary.run();
        IncreasingTripletSubsequence.test();
        LongestSubstringWithAtMostKDistinctCharacters.test();
        FindRightInterval.test();
        ShortestWordDistance.test();
    }
}

