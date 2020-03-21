/**
 * Leetcode questions 800 - *, (list)
 */

import java.lang.Math;
import java.util.*;

/**
 * Q-973 K Closest Points to Origin
 *
 * We have a list of points on the plane. Find the K closest points to the origin (0, 0). The distance between two
 * points on a plane is the Euclidean distance
 */
class KClosestPointsToOrigin {
    public static int[][] kClosest(int[][] points, int K) {
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(K, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                double d1 = Math.sqrt(o1[0]*o1[0] + o1[1]*o1[1]), d2 = Math.sqrt(o2[0]*o2[0] + o2[1]*o2[1]);
                if (d1 < d2) return -1;
                else if (d1 > d2) return 1;
                return 0;
            }
        });

        for (int[] p : points) {
            minHeap.add(p);
        }
        int[][] result = new int[K][2];
        for (int i = 0; i < K; ++i) {
            int[] p = minHeap.remove();
            System.out.print(Arrays.toString(p) + ",");
            result[i] = p;
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-973. K Closest Points to Origin");
        kClosest(new int[][]{{3,3}, {5,-1}, {-2,4}}, 2); // [[3,3],[-2,4]]
    }
}

/**
 * Q-986 Interval List Intersections
 */
class IntervalListIntersections {
    public static int[][] intervalIntersection(int[][] A, int[][] B) {
        List<int[]> result = new ArrayList<>();

        int i = 0, j = 0;
        while (i < A.length && j < B.length) {
            if (A[i][0] > B[j][1]) { // no overlap
                //result.add(new int[]{B[j][0], B[j][1]});
                j++;
            } else if (B[j][0] > A[i][1]) { // no overlap
                //result.add(new int[]{A[i][0], A[i][1]});
                i++;
            } else if (B[j][1] == A[i][1]) { // both have same right ends
                result.add(new int[]{Math.max(A[i][0], B[j][0]), A[i][1]});
                i++; j++;
            } else if (A[i][0] == B[j][0]) { // both have same left ends
                result.add(new int[]{A[i][0], Math.min(A[i][1], B[j][1])});
                if (A[i][1] < B[j][1]) i++;
                else j++;
            } else { // overlap
                result.add(new int[]{Math.max(A[i][0], B[j][0]), Math.min(A[i][1], B[j][1])});
                if (A[i][1] < B[j][1]) i++;
                else j++;
            }
        }
        int[][] results = new int[result.size()][];
        for (i = 0; i < result.size(); ++i)
            results[i] = result.get(i);
        System.out.println(Arrays.deepToString(results));
        return results;
    }

    public static void test() {
        System.out.println("\nQ-986 Interval List Intersections");
        int[][] a1 = {{0,2},{5,10},{13,23},{24,25}};
        int[][] b1 = {{1,5},{8,12},{15,24},{25,26}};
        intervalIntersection(a1, b1); // [[1, 2], [5, 5], [8, 10], [15, 23], [24, 24], [25, 25]]

        int[][] a2 = {{3,5},{9,20}};
        int[][] b2 = {{4,5},{7,10},{11,12},{14,15},{16,20}};
        intervalIntersection(a2, b2); // [[4, 5], [9, 10], [11, 12], [14, 15], [16, 20]]
    }
}

/**
 * Q-987 Vertical Order Traversal of a Binary Tree
 *
 * Similar to Question 314, but this uses (x-1,y-1) for left child, (x+1,y-1) for right child.
 */
class VerticalOrderTraversalOfBinaryTree {
    private static void dfs(TreeNode root, int x, int y, Map<Integer, Map<Integer, List<Integer>>> treeMap) {
        if (root == null) return;
        Map<Integer, List<Integer>> ymap = treeMap.containsKey(x) ? treeMap.get(x) : new TreeMap<>();
        treeMap.put(x, ymap);
        List<Integer> vlist = ymap.containsKey(y) ? ymap.get(y) : new ArrayList<>();
        ymap.put(y, vlist);
        vlist.add(root.val);
        dfs(root.left, x-1, y-1, treeMap);
        dfs(root.right, x+1, y-1, treeMap);
    }

    public List<List<Integer>> verticalTraversal(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Map<Integer, Map<Integer, List<Integer>>> treeMap = new TreeMap<>();
        dfs(root, 0, 0, treeMap);
        for (Map.Entry<Integer, Map<Integer,  List<Integer>>> xe : treeMap.entrySet()) {
            List<Integer> xvlist = new ArrayList<>();
            for (Map.Entry<Integer, List<Integer>> ye : xe.getValue().entrySet()) {
                List<Integer> yvlist = ye.getValue();
                // sort because smaller value should be first
                Collections.sort(yvlist);
                // no idea why it has to insert to the head
                xvlist.addAll(0, yvlist);
            }
            result.add(xvlist);
        }
        return result;
    }
}

/**
 * Q-994 Rotting Oranges
 */
class RottingOranges {
    public static int orangesRotting(int[][] grid) {
        int fresh = 0;
        Queue<int[]> bfs = new ArrayDeque<>();
        for (int i = 0; i < grid.length; ++i) {
            for (int j = 0; j < grid[0].length; ++j) {
                if (grid[i][j] == 1) fresh++;
                else if (grid[i][j] == 2) bfs.add(new int[]{i,j});
            }
        }

        int[][] diffs = {{0,-1},{-1,0},{0,1},{1,0}};
        int step = 0;
        while (!bfs.isEmpty() && fresh > 0) {
            int total = bfs.size();
            for (int i = 0; i < total; ++i) {
                int[] rot = bfs.poll();
                for (int j = 0; j < diffs.length; ++j) {
                    int r = rot[0] + diffs[j][0], c = rot[1] + diffs[j][1];
                    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != 1) continue;
                    grid[r][c] = 2;
                    bfs.add(new int[]{r,c});
                    fresh--;
                }
            }
            step++;
        }

        return fresh == 0 ? step : -1;
    }

    public static void test() {
        System.out.println("Q-994 Rotting Oranges");
        int[][] g1 = {{2,1,1},{1,1,0},{0,1,1}}; // 4
        System.out.println(orangesRotting(g1));
        int[][] g2 = {{2,1,1},{0,1,1},{1,0,1}};
        System.out.println(orangesRotting(g2)); // -1
    }
}

/**
 * Q-1007 Minimum Domino Rotations For Equal Row
 */
class MinimumDominoRotationsForEqualRow {
    public static int minDominoRotations(int[] A, int[] B) {
        int i, a, b;
        for (i = 0, a = 0, b = 0; i < A.length; ++i) {
            if (A[i] == A[0]) a++;
            if (B[i] == A[0]) b++;
            if (A[i] != A[0] && B[i] != A[0]) break;
        }
        if (i == A.length)
            return Math.min(A.length-a, A.length-b);
        for (i = 0, a = 0, b = 0; i < A.length; ++i) {
            if (A[i] == B[0]) a++;
            if (B[i] == B[0]) b++;
            if (A[i] != B[0] && B[i] != B[0]) break;
        }
        if (i == A.length)
            return Math.min(A.length-a, A.length-b);
        return -1;
    }

    public static void test() {
        System.out.println("Q-1007 Minimum Domino Rotations For Equal Row");
        System.out.println(minDominoRotations(new int[]{2,1,2,4,2,2}, new int[]{5,2,6,2,3,2})); // 2
        System.out.println(minDominoRotations(new int[]{3,6,3,3,4}, new int[]{3,5,1,2,3})); // -1
        System.out.println(minDominoRotations(new int[]{1,2,1,1,1,2,2,2}, new int[]{2,1,2,2,2,2,2,2})); // 1
    }
}

/**
 * Q-1060 Missing Element in Sorted Array
 */
class MissingElementInSortedArray {
    public static int missingElement(int[] nums, int k) {
        int len = nums.length;
        // the number of missing less than k
        int missing = nums[len-1] - nums[0] + 1 - len;
        if (missing < k)
            return nums[len-1] + (k-missing);
        int l = 0, r = len-1;
        while (l < r-1) { // l and r adjacent, missing is always 0
            int m = l + (r-l)/2;
            missing = nums[m] - nums[l] - (m-l);
            if (missing >= k)
                r = m;
            else {
                l = m;
                k -= missing;
            }
        }
        return nums[l] + k;
    }

    public static void test() {
        System.out.println("Q-1060 Missing Element in Sorted Array");
        int[] a0 = {4,5,7};
        System.out.println(missingElement(a0, 3)); // 9
        int[] a1 = {4,7,9,11};
        System.out.println(missingElement(a1, 1)); // 5
        System.out.println(missingElement(a1, 3)); // 8
        System.out.println(missingElement(a1, 4)); // 10
    }
}

/**
 * Q-1130 Minimum Cost Tree From Leaf Values
 */
class  MinimumCostTreeFromLeafValues {
    public int mctFromLeafValues(int[] arr) {
        return 0;
    }
}

/**
 * Q-1249 Minimum Remove to Make Valid Parentheses
 *
 * Given a string s of '(' , ')' and lowercase English characters. Your task is to remove the minimum number of
 * parentheses ( '(' or ')', in any positions ) so that the resulting parentheses string is valid and return any
 * valid string.
 */
class MinimumRemoveToMakeValidParentheses {
    private static boolean valid(String s) {
        int left = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '(') left++;
            else if (s.charAt(i) == ')') left--;
            if (left < 0) return false;
        }
        return left == 0 ? true : false;
    }

    // BFS with queue would OOM
    public static String minRemoveToMakeValid2(String s) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(s);
        visited.add(s);
        while (!queue.isEmpty()) {
            String w = queue.poll();
            if (valid(w))
                return w;
            for (int i = 0; i < w.length(); ++i) {
                if (w.charAt(i) == '(' || w.charAt(i) == ')') {
                    String next = "" + w.substring(0, i) + w.substring(i+1);
                    if (!visited.contains(next)) {
                        queue.add(next);
                        visited.add(next);
                    }
                }
            }
        }
        return "";
    }

    public static String minRemoveToMakeValid(String s) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0, left = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '(') left++;
            else if (c == ')') {
                if (left == 0) continue;
                left--;
            }
            sb.append(c);
        }
        String s1 = sb.toString();
        sb.setLength(0);
        for (int i = s1.length()-1, right = 0; i >= 0; --i) {
            char c = s1.charAt(i);
            if (c == ')') right++;
            else if (c == '(') {
                if (right == 0) continue;
                right--;
            }
            sb.append(c);
        }
        return sb.reverse().toString();
    }

    public static void test() {
        System.out.println("Q-1249 Minimum Remove to Make Valid Parentheses");
        System.out.println(minRemoveToMakeValid("lee(t(c)o)de)"));
        System.out.println(minRemoveToMakeValid("a)b(c)d")); // ab(c)d
        System.out.println(minRemoveToMakeValid(")()))))))())((()j)j))q()k)()()((d))()w((z()(())uh)"));
    }
}

public class LeetcodeThree {
    public static void main(String[] args) {
        System.out.println("Leetcode 3");
        KClosestPointsToOrigin.test();
        IntervalListIntersections.test();
        RottingOranges.test();
        MinimumDominoRotationsForEqualRow.test();
        MissingElementInSortedArray.test();
        MinimumRemoveToMakeValidParentheses.test();
    }
}