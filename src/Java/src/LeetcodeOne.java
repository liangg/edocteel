/**
 * Leetcode Questions - medium level 1 - 300
 *
 * Including intervals questions
 */

import java.util.*;

class Interval {
    int start;
    int end;
    Interval() { start = 0; end = 0; }
    Interval(int s, int e) { start = s; end = e; }
    public String toString() {
        return "(" + start + "," + end + ")";
    }
}

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
    permSeearch(nums, 0, vivisted, sol, result);
    return result;
  }

  private static void permSeearch(int[] nums, int level, boolean[] visited, List<Integer> sol, List<List<Integer>> result) {
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
      permSeearch(nums, level+1, visited, sol, result);
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


class PermutationUnique
{
    private static void gen_permutation(int[] num, Integer[] perm_num, int idx,
                                        HashMap<Integer, Integer> num_counts,
                                        ArrayList<ArrayList<Integer>> result)
    {
        if (idx == num.length) {
            ArrayList<Integer> perm = new ArrayList<Integer>(Arrays.asList(perm_num));
            result.add(perm);
            return;
        }

    /* iterate each disintct value in the hashmap */
        for (Map.Entry<Integer, Integer> entry : num_counts.entrySet()) {
            Integer elem = entry.getKey();
            Integer val = entry.getValue();
            int cnt = val.intValue();
            if (cnt > 0) {
                perm_num[idx] = elem;
                num_counts.put(elem, new Integer(cnt-1));
                gen_permutation(num, perm_num, idx+1, num_counts, result);
                num_counts.put(elem, val);
            }
        }
    }

    public static ArrayList<ArrayList<Integer>> permuteUnique(int[] num) {
        HashMap<Integer, Integer> num_counts = new HashMap<Integer, Integer>(2*num.length);
        Integer[] perm_num = new Integer[num.length];
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

    /* count the number of appearance of each number */
        for (int i = 0; i < num.length; ++i) {
            Integer elem = new Integer(num[i]);
            int cnt = 0;
            if (num_counts.containsKey(elem) == true)
                cnt = num_counts.get(elem);
            num_counts.put(elem, new Integer(cnt+1));
        }

        gen_permutation(num, perm_num, 0, num_counts, result);
        return result;
    }

    public static void run() {
        System.out.println("-------- Permutation Unique ---------");
        int[] v0 = new int[] {2,-1,-1};
        ArrayList<ArrayList<Integer>> result = permuteUnique(v0);
        for (ArrayList<Integer> perm : result) {
            System.out.printf("(");
            for (Integer elem : perm)
                System.out.printf("%d", elem.intValue());
            System.out.printf(")");
        }
        System.out.printf("\n");
    }
}

/** Q-75 Sort Colors */
class SortColors {
    private static void swap(int[] nums, int l, int r) {
        int t = nums[l];
        nums[l] = nums[r];
        nums[r] = t;
    }

    public static void sortColors(int[] nums) {
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

    public static void test() {
        System.out.println("Q-75 Sort Colors");
        int[] t1 = {1,0,0,2,1,2,0,2};
        SortColors.sortColors(t1);
        int[] t2 = {2,2};
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

    public ArrayList<ArrayList<Integer>> subsets(int[] S) {
        boolean[] solution = new boolean[S.length];
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>(S.length*2);
        Arrays.sort(S);
        gen_subsets(S, 0, solution, result);
        return result;
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
    private static void copyInPlace(int[] nums, int write, int start, int count) {
        if (count <= 0)
            return;
        for (int i = 0; i < count; ++i) {
            nums[write+i] = nums[start+i];
        }
    }

    public static int removeDuplicates(int[] nums) {
        int result = 0;
        if (nums.length == 0)
            return result;

        int num = nums[nums.length-1], count = 1;
        int copyStart = nums.length;
        for (int i = nums.length-2; i >= 0; --i) {
            if (num == nums[i]) {
                count += 1;
                continue;
            }
            copyInPlace(nums, i + (count > 1 ? 3 : 2), copyStart, result);
            copyStart = i+1;
            result += (count > 1 ? 2 : 1);
            num = nums[i];
            count = 1;
        }
        copyInPlace(nums, (count > 1 ? 2 : 1), copyStart, result);
        result += (count > 1 ? 2 : 1);
        System.out.println(Arrays.toString(nums));
        return result;
    }

    public static void test() {
        System.out.println("Q-80. Remove Duplicates from Sorted Array II ");
        int[] t1 = new int[] {0,0,0,1,1,1,1,2,3,3,3,4,4,4};
        System.out.println(removeDuplicates(t1));
    }
}

/**
 * Q-93 Restore IP Address
 *
 * If we only cares for the number of possible IP addresses, it can be solved by iterative algorithm using memoization
 * matrix.
 */
class RestoreIpAddress {
    private static void restore_ip(String s, int idx, int partIdx, int ipNumbers[],
                                   List<String> result) {
        if (partIdx > 4)
            return;
        if (partIdx == 4) {
            if (idx == s.length()) {
                StringBuilder ipStr = new StringBuilder();
                for (int num : ipNumbers) {
                    ipStr.append(String.valueOf(num)).append(".");
                }
                ipStr.setLength(ipStr.length()-1);
                result.add(ipStr.toString());
            }
            return;
        }

    /* IP segment number starting with 0 is not allowed, except single '0',
     * so stop after first 0 */
        int number = 0;
        boolean firstIsZero = false;
        for (int i = 0; i < 3 && idx + i < s.length() && !firstIsZero; ++i) {
            int digit = Character.digit(s.charAt(idx+i), 10);
            number = number*10 + digit;
            if (!firstIsZero && number == 0)
                firstIsZero = true;
            if (number > 255)
                break;
            ipNumbers[partIdx] = number;
            restore_ip(s, idx+i+1, partIdx+1, ipNumbers, result);
        }
    }

    private static void restoreIpAddresses(String s) {
        final List<String> result = new ArrayList<String>();
        int ipNumbers[] = new int[4];
        restore_ip(s, 0, 0, ipNumbers, result);
        System.out.print(s + " -> ");
        for (String ipstr : result)
            System.out.print(ipstr + ", ");
        System.out.print("\n");
    }

    public static void run() {
        System.out.println("----------- Restore IP Addresses --------------");
        restoreIpAddresses("25525511135");
        restoreIpAddresses("123562559");
        restoreIpAddresses("0000");
        restoreIpAddresses("010010");
    }
}

/** Q-105 Construct Binary Tree from Preorder and Inorder Traversal */
class ConstructTreeFromPreorderInorder {
    private TreeNode build(int[] preorder, int preleft, int preright, int[] inorder, int inleft, int inright) {
        if (preleft > preright || inleft > inright)
            return null;
        // find the inorder position index of the root node
        int iidx;
        for (iidx = inleft; iidx <= inright && preorder[preleft] != inorder[iidx]; ++iidx);
        TreeNode root = new TreeNode(preorder[preleft]);
        root.left = build(preorder, preleft+1, preleft + iidx - inleft, inorder, inleft, iidx-1);
        root.right = build(preorder, preleft + iidx - inleft + 1, preright, inorder, iidx+1, inright);
        return root;
    }

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length == 0)
            return null;
        return build(preorder, 0, preorder.length-1, inorder, 0, inorder.length-1);
    }
}

/** Q-106 Construct Binary Tree from Postorder and Inorder Traversal */
class ConstructTreeFromPostorderInorder {
    private TreeNode build(int[] inorder, int inleft, int inright, int[] postorder, int postleft, int postright) {
        if (postleft > postright || inleft > inright)
            return null;
        // find the inorder position index of the root node
        int iidx;
        for (iidx = inleft; iidx <= inright && postorder[postright] != inorder[iidx]; ++iidx);
        TreeNode root = new TreeNode(postorder[postright]);
        root.left = build(inorder, inleft, iidx-1, postorder, postleft, postright - (inright-iidx) - 1);
        root.right = build(inorder, iidx+1, inright, postorder, postright - (inright-iidx), postright-1);
        return root;
    }

    public TreeNode buildTree(int[] inorder, int[] postorder) {
        if (inorder.length == 0)
            return null;
        return build(inorder, 0, inorder.length-1, postorder, 0, postorder.length-1);
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
 * Q-139: Word Break
 */
class WordBreak {
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

/** Q-162 Find Peak Element */
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


/** Q-199 Binary Tree Right Side View */
class BinaryTreeRightSideView {
    public List<Integer> rightSideView(TreeNode root) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        if (root == null)
            return result;
        ArrayDeque<TreeNode> queue = new ArrayDeque<TreeNode>();
        queue.add(root);
        while (!queue.isEmpty()) {
            result.add(queue.peek().val);
            // traverse the "nlvl" nodes on the same level
            int nlvl = queue.size();
            for (int i = 0; i < nlvl; ++i) {
                TreeNode n = queue.poll();
                if (n.right != null)
                    queue.add(n.right);
                if (n.left != null)
                    queue.add(n.left);
            }
        }
        return result;
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
        return null;
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

/** Q-331 Verify Preorder Serialization of Binary Tree */
class VerifyPreorderSerializationBinaryTree {
    public static boolean isValidSerialization(String preorder) {
        return true;
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
    LongestSubstringWithoutRepeatingChars.test();
    FindFirstLastPosSortedArray.test();
    ThreeSum.test();
    LetterComboOfPhoneNumber.run();
    CombinationSum.test();
    Permutations.test();
    MergeIntervals.test();
    JumpGame.test();
    SortColors.test();
    RemoveDuplicatesFromSortedArray.test();
    GasStation.test();
    FindPeakElement.test();
    LargestNumber.test();
    CourseSchedule.test();
    RepeatedDNASequence.test();
    ShuffleArray.test();
    LexicographicalNumbers.test();
    FindRightInterval.test();
  }
}

