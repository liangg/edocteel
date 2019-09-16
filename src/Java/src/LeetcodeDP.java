/**
 * Leetcode DP problems
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Q-5 Longest Palindromic Substring
 */
class LongestPalindromeSubstring {
    public static String longestPalindrome(String s) {
        int N = s.length();
        boolean[][] dp = new boolean[N][N];
        for (int i = 0; i < N; ++i)
        dp[i][i] = true;
        int l = 0, r = 0;
        for (int i = 1; i < N; ++i) { // substring length
            for (int j = 0; j < N-i; ++j) { // substring left char
                if (s.charAt(j) == s.charAt(j+i)) {
                    if (j+1 <= j+i-1) {
                        if (dp[j+1][j+i-1])
                            dp[j][j+i] = true;
                    } else {
                        dp[j][j+i] = true;
                    }
                    // maintain longest substring
                    if (dp[j][j+i] && i+1 > (r-l+1)) {
                        l = j;
                        r = j+i;
                    }
                }
            }
        }
        return s.substring(l,r+1);
    }

    public static void test() {
        System.out.println("Q-5 Longest Palindromic Substring");
        System.out.println(longestPalindrome("babad"));
    }
}

/**
 * Q-62 Unique Paths
 *
 * A robot is located at the top-left corner of a m x n grid (marked 'Start' in the diagram below). The robot can only
 * move either down or right at any point in time. The robot is trying to reach the bottom-right corner of the grid
 * (marked 'Finish' in the diagram below). How many possible unique paths are there?
 */
class UniquePaths {
  public int uniquePaths(int m, int n) {
    int dp[][] = new int[m][n];
    dp[0][0] = 1;
    for (int i = 0; i < m; ++i) {
      for (int j = 0; j < n; ++j) {
        if (i > 0)
          dp[i][j] += dp[i-1][j];
        if (j > 0)
          dp[i][j] += dp[i][j-1];
      }
    }
    return dp[m-1][n-1];
  }
}

/**
 * Q-72 Edit Distance
 */
class EditDistance
{
  public int minDistance(String word1, String word2) {
    int len1 = word1.length();
    int len2 = word2.length();
    int[][] dp = new int[len1+1][len2+1];

    dp[0][0] = 0;
    for (int i = 1; i <= len1; ++i)
      dp[i][0] = i;
    for (int i = 1; i <= len2; ++i)
      dp[0][i] = i;

    for (int i = 1; i <= len1; ++i) {
      for (int j = 1; j <= len2; ++j) {
        int cost = dp[i-1][j-1];
        /* update word1[i] with word2[j], if not equal */
        if (word1.charAt(i-1) != word2.charAt(j-1))
          cost += 1;
        /* insert word2[j] to after word1[i] */
        if (cost > (dp[i][j-1]+1))
          cost = dp[i][j-1] + 1;
        /* delete word1[i] and transform word1[0..i-1] to word2[0..j] */
        if (cost > (dp[i-1][j]+1))
          cost = dp[i-1][j] + 1;
        dp[i][j] = cost;
      }
    }
    return dp[len1][len2];
  }
}

/**
 * Q-91 Decode Ways
 */
class DecodeWays {
  public static int numDecodings(String s) {
    int N = s.length();
    if (N == 0)
      return 0;
    int count1 = (s.charAt(N-1) - '0') != 0 ? 1 : 0;
    int count2 = 1;
    for (int i = s.length()-2; i >= 0; --i) {
      int count = 0;
      int d = s.charAt(i) - '0';
      if (d != 0) {
        count = count1;
        if ((d*10 + (s.charAt(i+1) - '0') <= 26)) {
          count += count2;
        }
      }
      count2 = count1;
      count1 = count;
      if (count1 == 0 && count2 == 0) {
        break;
      }
    }
    return count1;
  }

  public static void test() {
    System.out.println("Q-91 Decode Ways");
    numDecodings("10");
  }
}

/** Q-120 Triangle */
class TriangleMinSumPath {
    public int minimumTotal(List<List<Integer>> triangle) {
        List<Integer> memo = new ArrayList<>(triangle.get(triangle.size()-1));
        for (int i = triangle.size()-2; i >= 0; --i) {
            List<Integer> r = triangle.get(i);
            for (int j = 0; j <= i; ++j) {
                memo.set(j, Math.min(memo.get(j), memo.get(j+1)) + r.get(j));
            }
        }
        return memo.get(0);
    }
}

/**
 * Q-131: Palindrome Partition
 *
 * Given a string s, partition s such that every substring of the partition is a palindrome.
 * Return all possible palindrome partitioning of s.
 */
class PalindromePartition {
  public static List<List<String>> partition(String s) {
    int N = s.length();
    if (N == 0) {
      return new ArrayList<List<String>>();
    }

    // memoization M[i][j] is true if s[i, j] is palindrome
    boolean M[][] = new boolean[N][N];
    for (int l = 0; l < N; ++l) {
      for (int i = 0; i < N; ++i) {
        int right = i + l;
        if (right < N) {
          if (s.charAt(i) == s.charAt(right) && (right-i <= 1 || M[i+1][right-1])) {
            M[i][right] = true;
          }
        }
      }
    }

    ArrayList<ArrayList<ArrayList<String>>> suffix_pps = new ArrayList<ArrayList<ArrayList<String>>>();
    // scan backward and construct palindrome partitions at each suffix
    for (int i = N-1; i >= 0; --i) {
      ArrayList<ArrayList<String>> pps = new ArrayList<ArrayList<String>>();
      for (int j = i; j < N; ++j) {
        if (M[i][j] == true) {
          if (j+1 < N) {
            ArrayList<ArrayList<String>> pps_j = suffix_pps.get((N-1)-(j+1)); // reverse positioned
            for (ArrayList<String> al : pps_j) {
              ArrayList<String> list = new ArrayList<String>();
              list.add(s.substring(i,j+1));
              list.addAll(al);
              pps.add(list);
            }
          } else {
            ArrayList<String> list = new ArrayList<String>();
            list.add(s.substring(i,j+1));
            pps.add(list);
          }
        }
      }
      suffix_pps.add(pps);
    }

    List<List<String>> result = new ArrayList<List<String>>();
    result.addAll(suffix_pps.get(N-1));
    for (List<String> al : result) {
      for (String str : al) {
        System.out.printf(str + ", ");
      }
      System.out.println();
    }
    return result;
  }

  public static void run() {
    System.out.println("----------- Palindrome Partition ------------");
    partition("abbacddc");

  }
}

/**
 * Q-132: Palindrome Partitioning II - minimum cuts
 */
class PalindromePartitionMinCut {
  public int minCut(String s) {
    int N = s.length();
    /* M[i,j] = M[i+1, j-1] if S[i] == S[j] */
    boolean[][] mat = new boolean[N][N];
    mat[N-1][N-1] = true;
    for (int i = N-2; i >= 0; --i) {
      for (int j = N-1; j >= i; --j) {
        if (s.charAt(i) == s.charAt(j) && (i+1 > j-1 || mat[i+1][j-1]))
          mat[i][j] = true;
      }
    }
    /* scan backward and compute minimum cut at each positon */
    int[] min_cuts = new int[N];
    for (int i = N-1; i >= 0; --i)
      min_cuts[i] = N-i;
    for (int i = N-2; i >= 0; --i) {
      for (int j = N-1; j >= i; --j) {
        if (mat[i][j] == true) {
          int ncuts = 1 + ((j+1 <= N-1) ? min_cuts[j+1] : 0);
          if (ncuts < min_cuts[i])
            min_cuts[i] = ncuts;
        }
      }
    }
    return min_cuts[0] - 1;
  }
}

class CoinChange {
    /* Recursion with memoization but it exceeds time limit on some testcase */
    private static int makeChange(int[] coins, int amount, int curr, HashMap<Integer, Integer> memo) {
        if (curr < 0 || amount < 0 || amount < coins[curr])
            return -1;
        if (amount == 0)
            return 0;

        int denom = coins[curr];
        int n = amount / denom;
        if (n*denom == amount) {
            if (!memo.containsKey(amount) || memo.get(amount) > n) {
                memo.put(amount, n);
            }
            return memo.get(amount);
        }

        for (int i = n; i >= 0; i -= 1) {
            int c = makeChange(coins, amount-i*denom, curr-1, memo);
            if (c != -1 && (!memo.containsKey(amount) || memo.get(amount) > c+i)) {
                memo.put(amount, c+i);
            }
        }

        return memo.containsKey(amount) ? memo.get(amount) : -1;
    }

    private static int makeChange_dp(int[] coins, int amount) {
        if (amount < coins[0])
            return -1;

        int[] memo = new int[amount+1];
        memo[0] = 0;
        for (int i = 1; i < coins[0]; ++i)
            memo[i] = Integer.MAX_VALUE;

        for (int i = coins[0]; i <= amount; ++i) {
            memo[i] = Integer.MAX_VALUE;
            for (int j = 0; j < coins.length; ++j) {
                int denom = coins[j];
                if (denom <= i && memo[i-denom] != Integer.MAX_VALUE && memo[i] > memo[i-denom] + 1)
                    memo[i] = memo[i-denom] + 1;
            }
        }

        return memo[amount] != Integer.MAX_VALUE ? memo[amount] : -1;
    }

    public static int coinChange(int[] coins, int amount) {
        if (amount == 0)
            return 0;
        HashMap<Integer, Integer> memo = new HashMap<Integer, Integer>();
        Arrays.sort(coins);
        //return makeChange(coins, amount, coins.length-1, memo);
        return makeChange_dp(coins, amount);
    }

    public static void run() {
        System.out.println("------------ Coin Change ------------");
        int[] a1 = {3,1,5};
        System.out.println(coinChange(a1, 12)); // 4
        int[] a2 = {3,1,10};
        System.out.println(coinChange(a2, 13)); // 2
        int[] a3 = {30,24,15,3,1};
        System.out.println(coinChange(a3, 48)); // 2 (24+24)
        int[] a4 = {186,419,83,408};
        System.out.println(coinChange(a4, 6249)); // 20
        int[] a5 = {270,373,487,5,62};
        System.out.println(coinChange(a5, 8121)); // 21
        int[] a6 = {3,7,405,436};
        System.out.println(coinChange(a6, 8839)); // 25
        int[] a7 = {346,29,395,188,155,109};
        System.out.println(coinChange(a7, 9401)); // 26
    }
}


/**
 * Q-188 Best Time to Buy and Sell Stock IV
 *
 * Say you have an array for which the i-th element is the price of a given stock on day i. Design an algorithm to find
 * the maximum profit. You may complete at most k transactions. Note, You may not engage in multiple transactions at
 * the same time (ie, you must sell the stock before you buy again).
 */
class BestTimeBuySellStock4 {
    public int maxProfit(int k, int[] prices) {
        return 0;
    }

    public static void test() {
        System.out.println("Q-188 Best Time to Buy and Sell Stock IV");
        // [2,4,1], k = 2 // = 2
        // [3,2,6,5,0,3], k = 2 // = 7
    }
}

/**
 * Q-583 Delete Operations for 2 Strings (variant of Longest Common Subsequence problem)
 *
 * Given two words word1 and word2, find the minimum number of steps required to make word1 and word2 the same, where
 * in each step you can delete one character in either string.
 */
class DeleteOperationsForTwoStrings {
    public static int minDistance(String word1, String word2) {
        if (word1.isEmpty()) return word2.length();
        if (word2.isEmpty()) return word1.length();
        int[][] dp = new int[word1.length()+1][word2.length()+1];
        for (int i = 1; i <= word1.length(); ++i) {
            for (int j = 1; j <= word2.length(); ++j) {
                if (word1.charAt(i-1) == word2.charAt(j-1))
                    dp[i][j] = dp[i-1][j-1] + 1;
                else {
                    dp[i][j] = dp[i-1][j] > dp[i][j-1] ? dp[i-1][j] : dp[i][j-1];
                }
            }
        }
        return word1.length()+word2.length()-dp[word1.length()][word2.length()]*2;
    }

    public static void test() {
        System.out.println("Q-583 Delete Operations for Two Strings");
        System.out.println(minDistance("seal", "eat")); // 3
    }
}

/**
 * Q-647 Palindromic Substrings
 *
 * Given a string, your task is to count how many palindromic substrings in this string. The substrings with different
 * start indexes or end indexes are counted as different substrings even they consist of same characters.
 */
class PalindromicSubstrings {
  public static int countSubstrings(String s) {
    int N = s.length();
    boolean[][] dp = new boolean[N][N];
    for (int i = 0; i < N; ++i)
      dp[i][i] = true;
    int result = s.length();
    for (int i = 1; i < N; ++i) { // substring length
      for (int j = 0; j < N - i; ++j) { // substring left char
        if (s.charAt(j) == s.charAt(j + i)) {
          if (j + 1 <= j + i - 1) {
            if (dp[j + 1][j + i - 1]) {
              dp[j][j + i] = true;
              result += 1;
            }
          } else {
            dp[j][j + i] = true;
            result += 1;
          }
        }
      }
    }
    return result;
  }

  public static void test() {
    System.out.println("Q-647 Palindromic Substrings");
    System.out.println(countSubstrings("aaa")); // 6
  }
}

/**
 * Q-673 Number of Longest Increasing Subsequence
 */
class NumberOfLongestIncreasingSubsequence {
    public static int findNumberOfLIS(int[] nums) {
        if (nums.length == 0) return 0;
        int best[] = new int[nums.length], length[] = new int[nums.length], longest = 1; // tracks number of LIS of the same length
        best[0] = 1;
        length[0] = 1;
        for (int i = 1; i < nums.length; ++i) {
            best[i] = 1;
            int count = 1;
            for (int j = 0; j < i; ++j) {
                if (nums[i] > nums[j]) {
                    int len = best[j] + 1;
                    if (len > best[i]) {
                        best[i] = len;
                        count = length[j];
                    } else if (len == best[i])
                        count += length[j];
                }
            }
            length[i] = count;
            if (best[i] > longest)
                longest = best[i];
        }
        int result = 0;
        for (int i = 0; i < nums.length; ++i) {
            if (best[i] == longest)
                result += length[i];
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-673 Number of Longest Increasing Subsequence");
        System.out.println(findNumberOfLIS(new int[]{1,3,5,4,7}));
        System.out.println(findNumberOfLIS(new int[]{1,3,2,5,4,7}));
    }
}

/**
 * Q-718 Maximum Length of Repeated Subarray
 *
 * Given two integer arrays A and B, return the maximum length of an subarray that appears in both arrays.
 */
class MaximumLengthOfRepeatedSubarray {
    public static int findLength(int[] A, int[] B) {
        int result = 0;
        int[][] dp = new int[A.length+1][B.length+1];
        for (int i = 1; i <= A.length; ++i) {
            for (int j = 1; j <= B.length; ++j) {
                if (A[i-1] == B[j-1]) {
                    dp[i][j] = dp[i-1][j-1] + 1;
                    if (dp[i][j] > result)
                        result = dp[i][j];
                }
            }
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-718 Maximum Length of Repeated Subarray");
        int[] a1 = new int[]{1,2,3,2,1};
        int[] a2 = new int[]{3,2,1,4,7};
        System.out.println(findLength(a1, a2));
    }
}

/**
 * Q-740 Delete and Earn (variant of house robber)
 *
 * Given an array nums of integers, you can perform operations on the array. In each operation, you pick any nums[i]
 * and delete it to earn nums[i] points. After, you must delete every element equal to nums[i] - 1 or nums[i] + 1. You
 * start with 0 points. Return the maximum number of points you can earn by applying such operations. Note, numbers in
 * nums[] is in the range [1, 10000].
 */
class DeleteAndEarn {
    public static int deleteAndEarn(int[] nums) {
        int[] counts = new int[10001]; // range 0..10000
        for (int n : nums)
            counts[n] += 1;
        int lastTaken = 0, lastIgnored = 0; // previous number taken, previous number ignored
        for (int i = 1; i < 10001; ++i) {
            int currTaken = lastIgnored + i * counts[i];
            int currIgnored = lastTaken > lastIgnored ? lastTaken : lastIgnored;
            lastTaken = currTaken;
            lastIgnored = currIgnored;
        }
        return lastTaken > lastIgnored ? lastTaken : lastIgnored;
    }

    public static void test() {
        System.out.println("Q-740 Delete and Earn");
        System.out.println(deleteAndEarn(new int[]{2, 3, 4})); // 6
        System.out.println(deleteAndEarn(new int[]{2, 2, 3, 3, 3, 4})); // 9
        System.out.println(deleteAndEarn(new int[]{8, 10, 4, 9, 1, 3, 5, 9, 4, 10})); // 37
    }
}

public class LeetcodeDP {
    public static void main(String[] args) {
        LongestPalindromeSubstring.test();
        DeleteOperationsForTwoStrings.test(); // LCS
        PalindromicSubstrings.test();
        MaximumLengthOfRepeatedSubarray.test();
        NumberOfLongestIncreasingSubsequence.test();
        DeleteAndEarn.test();
    }
}

