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
        //System.out.println(minDominoRotations(new int[]{2,1,2,4,2,2}, new int[]{5,2,6,2,3,2})); // 2
        //System.out.println(minDominoRotations(new int[]{3,6,3,3,4}, new int[]{3,5,1,2,3})); // -1
        System.out.println(minDominoRotations(new int[]{1,2,1,1,1,2,2,2}, new int[]{2,1,2,2,2,2,2,2})); // 1
    }
}

public class LeetcodeThree {
    public static void main(String[] args) {
        System.out.println("Leetcode 3");
        KClosestPointsToOrigin.test();
        MinimumDominoRotationsForEqualRow.test();
    }
}