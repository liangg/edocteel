/**
 * Leetcode Questions - easy level, Tree
 */

import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}

class Node {
    int val;
    Node left;
    Node right;
    Node next;
}

class MorrisTraversal {
    void traverse(TreeNode root) {
        TreeNode n = root;
        while (n != null) {
            if (n.left != null) {
                // find the rightmost leaf in left subtree, which right-link to this node
                TreeNode p = n.left;
                while (p.right != null && p.right != n) {
                    p = p.right;
                }

                // reach here via the in-order right-link i.e. n is a sub-tree root and p is the
                // rightmost leaf in its left subtree
                if (p.right != null) {
                    p.right = null; // reset the right-link
                    System.out.print(n.val);
                    n = n.right;
                } else {
                    p.right = n;
                    n = n.left;
                }
            } else {
                System.out.print(n.val);
                n = n.right;
            }
        }
    }
}


/** 94. Binary Tree Inorder Traversal */
class BinaryTreeInorderTraversal {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode p = root;
        while (p != null || !stack.empty()) {
            for (; p != null; p = p.left)
                stack.push(p);
            p = stack.pop();
            result.add(p.val);
            p = p.right;
        }
        return result;
    }
}

/** Q-98 Validate Binary Search Tree */
class ValidateBinarySearchTree {
    private boolean valid(TreeNode root, long max, long min) {
        if (root == null)
            return true;
        if (root.val >= max || root.val <= min)
            return false;
        return valid(root.left, root.val, min) && valid(root.right, max, root.val);
    }

    public boolean isValidBST(TreeNode root) {
        // must use Long to check root node with Integer.MAX_VALUE
        return valid(root, Long.MAX_VALUE, Long.MIN_VALUE);
    }
}


/** Q-102 Binary Tree Level Order Traversal */
class BinaryTreeLevelOrderTraversal {
    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        if (root == null)
            return result;
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        List<TreeNode> nextLevel = new ArrayList<>();
        List<Integer> level = new ArrayList<>();
        while (!queue.isEmpty() || !nextLevel.isEmpty()) {
            if (queue.isEmpty()) {
                result.add(new ArrayList<Integer>(level));
                for (TreeNode n : nextLevel)
                    queue.add(n);
                nextLevel.clear();
                level.clear();
                continue;
            }
            TreeNode n = queue.poll();
            level.add(n.val);
            if (n.left != null)
                nextLevel.add(n.left);
            if (n.right != null)
                nextLevel.add(n.right);
        }
        if (!level.isEmpty())
            result.add(new ArrayList<Integer>(level));
        return result;
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
        // partition preorder and inorder for left subtree and right subtree
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
        root.left = build(inorder, inleft, iidx-1, postorder, postleft, postleft+iidx-inleft-1);
        root.right = build(inorder, iidx+1, inright, postorder, postleft+iidx-inleft, postright-1);
        return root;
    }

    public TreeNode buildTree(int[] inorder, int[] postorder) {
        if (inorder.length == 0)
            return null;
        return build(inorder, 0, inorder.length-1, postorder, 0, postorder.length-1);
    }
}

/** Q-109 Convert Sorted List to Binary Search Tree */
class ConvertSortedListToBinarySearchTree {
    private TreeNode convert(ListNode head, ListNode end) {
        if (head == end)
            return null;
        ListNode p1, p2;
        for (p1 = head, p2 = head; p2 != end && p2.next != end; ) {
            p1 = p1.next;
            p2 = p2.next.next;
        }
        TreeNode r = new TreeNode(p1.val);
        r.left = convert(head, p1);
        r.right = convert(p1.next, end);
        return r;
    }

    public TreeNode sortedListToBST(ListNode head) {
        return convert(head, null);
    }
}

/**
 * Q-110 Balanced Binary Tree
 *
 * Given a binary tree, determine if it is height-balanced. I.e. the depth of the two subtrees
 * of every node never differ by more than 1
 */
class BalancedBinaryTree
{
    private boolean balanced(TreeNode root, int depth, int[] pair, int pidx) {
        if (root.left == null && root.right == null) {
            pair[pidx] = depth;
            return true;
        }

        int localp[] = new int[2];
        localp[0] = localp[1] = depth;

        boolean left_bal = true;
        if (root.left != null)
            left_bal = balanced(root.left, depth+1, localp, 0);
        boolean right_bal = true;
        if (root.right != null && left_bal)
            right_bal = balanced(root.right, depth+1, localp, 1);
        boolean height_bal = true;
        if (root.left != null && root.right != null && Math.abs(localp[1] - localp[0]) > 1)
            height_bal = false;
        pair[pidx] = localp[0] > localp[1] ? localp[0] : localp[1];
        return left_bal && right_bal && height_bal;
    }

    public boolean isBalanced(TreeNode root) {
        if (root == null)
            return true;
        int[] pair = new int[2];
        pair[0] = pair[1] = 1;
        return balanced(root, 1, pair, 0);
    }
}

class BinaryTreePostorderTraversal
{
    static class Pair {
        public TreeNode node;
        public boolean finished;
        public Pair(TreeNode n) {
            node = n;
            finished = false;
        }
    }

    public ArrayList<Integer> postorderTraversal(TreeNode root) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        if (root == null)
            return result;
        Stack<Pair> stk = new Stack<Pair>();
        stk.push(new Pair(root));
        while (!stk.empty()) {
            Pair wp = stk.peek();
            TreeNode node = wp.node;
            if (wp.finished) {
                result.add(node.val);
                stk.pop();
            } else {
                wp.finished = true;
                if (node.right != null)
                    stk.push(new Pair(node.right));
                if (node.left != null)
                    stk.push(new Pair(node.left));
            }
        }
        return result;
    }
}

/**
 * Q-116 Populate Next Right Pointer in Perfect Binary Tree Node
 *
 * You are given a perfect binary tree where all leaves are on the same level, and every parent has two children.
 *
 * Q-117 Populate Next Right Pointer in Each Node II
 *
 * Populate each next pointer to point to its next right node. If there is no next right node, the next pointer should
 * be set to NULL.
 */
class PopulateBinaryTreeNextRightPointer
{
    // Q-116, basically level order traversal
    public Node connect(Node root) {
        if (root == null)
            return null;
        Deque<Node> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Deque<Node> next = new ArrayDeque<>();
            for (Node prev = null; queue.size() > 0;) {
                Node n = queue.poll();
                if (prev != null)
                    prev.next = n;
                prev = n;

                if (n.left != null) {
                    next.add(n.left);
                    next.add(n.right);
                }
            }
            queue = next;
        }
        return root;
    }

    // Q-117 use constant space
    public void connect2(Node root) {
        Node nextLevelFirst = root;
        while (nextLevelFirst != null) {
            Node prev = null;
            Node n = nextLevelFirst;
            for (nextLevelFirst = null; n != null; n = n.next) {
                if (n.left != null) {
                    if (prev != null) {
                        prev.next = n.left;
                        prev = n.left;
                    } else {
                        prev = n.left;
                    }
                    if (nextLevelFirst == null) {
                        nextLevelFirst = n.left;
                    }
                }
                if (n.right != null) {
                    if (prev != null) {
                        prev.next = n.right;
                        prev = n.right;
                    } else {
                        prev = n.right;
                    }
                    if (nextLevelFirst == null) {
                        nextLevelFirst = n.right;
                    }
                }
            } // end for loop
        } // end while loop
    }
}

class BinaryTreeMaxPathSum
{
    private int pathSum(TreeNode root, int maxsum[]) {
        if (root == null)
            return 0;
        int leftSum = pathSum(root.left, maxsum);
        int rightSum = pathSum(root.right, maxsum);
        int larger = leftSum > rightSum ? leftSum : rightSum;
        int sum = root.val + leftSum + rightSum;
        if (sum > maxsum[0])
            maxsum[0] = sum;
        larger = larger < 0 ? 0 : larger;
        if (root.val + larger > maxsum[0])
            maxsum[0] = root.val + larger;
        return root.val + larger;
    }

    public int maxPathSum(TreeNode root) {
        int maxsum[] = new int[] {Integer.MIN_VALUE};
        pathSum(root, maxsum);
        return maxsum[0];
    }
}

/** Q-144 Binary Tree Preorder Traversal */
class BinaryTreePreorderTraversal {
    public List<Integer> preorderTraversal(TreeNode root) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        if (root == null)
            return result;
        Stack<TreeNode> stack = new Stack<TreeNode>();
        stack.push(root);
        while (!stack.empty()) {
            TreeNode n = stack.pop();
            result.add(n.val);
            if (n.right != null)
                stack.push(n.right);
            if (n.left != null)
                stack.push(n.left);
        }
        return result;
    }
}

/** Q-230 Kth Smallest Elements in BST */
class KthSmallestElements
{
    public static int kthSmallest(TreeNode root, int k) {
        if (root == null)
            return 0;

        HashSet<TreeNode> visited = new HashSet<TreeNode>();
        Stack<TreeNode> nodeStack = new Stack<TreeNode>();
        nodeStack.push(root);

        TreeNode last = null;
        for (int count = 0; count < k;) {
            TreeNode node = nodeStack.peek();
            if (visited.contains(node)) {
                nodeStack.pop();
                last = node;
                count++;
                if (node.right != null) {
                    nodeStack.push(node.right);
                }
            } else {
                /* add all nodes on the path to smaller elements */
                for (TreeNode n = node.left; n != null; n = n.left) {
                    nodeStack.push(n);
                    visited.add(n);
                }
                visited.add(node);
            }
        }

        //TreeNode kth = nodeStack.peek();
        return last.val;
    }

    public static void run() {
        System.out.println("Q-230 Kth Smallest Elements in BST");
        TreeNode n15 = new TreeNode(15);
        TreeNode n16 = new TreeNode(16);
        TreeNode n12 = new TreeNode(12);
        n15.right = n16;
        n15.left = n12;
        TreeNode n14 = new TreeNode(14);
        n12.right = n14;
        TreeNode n9 = new TreeNode(9); // root
        n9.right = n15;
        TreeNode n5 = new TreeNode(5);
        n9.left = n5;
        TreeNode n3 = new TreeNode(3);
        TreeNode n2 = new TreeNode(2);
        n5.left = n3;
        n3.left = n2;
        System.out.printf("kth = %d\n", kthSmallest(n9, 3)); // 5
        System.out.printf("kth = %d\n", kthSmallest(n9, 6)); // 14
    }
}

/**
 * Q-337: House Robber III
 *
 * The thief has found himself a new place for his thievery again. There is only one entrance
 * to this area, called the "root." Besides the root, each house has one and only one parent
 * house. After a tour, the smart thief realized that "all houses in this place forms a binary
 * tree". It will automatically contact the police if two directly-linked houses were broken
 * into on the same night. Determine the maximum amount of money the thief can rob tonight
 * without alerting the police.
 */
class HouseRobber {
    static class ResultPair {
        public int root_sum;
        public int other_best;
        public ResultPair(int sum1, int sum2) {
            root_sum = sum1;
            other_best = sum2;
        }
    }

    private static ResultPair rob_house(TreeNode root) {
        if (root == null)
            return new ResultPair(0, 0);

        ResultPair left = rob_house(root.left);
        ResultPair right = rob_house(root.right);

        int sum1 = root.val + left.other_best + right.other_best;
        int left_best = left.other_best > left.root_sum ? left.other_best : left.root_sum;
        int right_best = right.other_best > right.root_sum ? right.other_best : right.root_sum;
        return new ResultPair(sum1, left_best + right_best);
    }

    private static int rob(TreeNode root) {
        ResultPair result = rob_house(root);
        return result.other_best > result.root_sum ? result.other_best : result.root_sum;
    }

    public static void run() {
        System.out.println("------- Rob House III -------");
        TreeNode n0 = new TreeNode(3); // root
        TreeNode n1 = new TreeNode(4);
        TreeNode n2 = new TreeNode(5);
        n0.right = n2;
        n0.left = n1;
        TreeNode n3 = new TreeNode(1);
        TreeNode n4 = new TreeNode(3);
        n1.left = n3;
        n1.right = n4;
        TreeNode n9 = new TreeNode(1);
        n2.right = n9;
        System.out.printf("rob house %d\n", rob(n0));
    }
}

/** Q-450 Delete Node in BST */
class DeleteNodeInBST {
    public static TreeNode deleteNode(TreeNode root, int key) {
        if (root == null)
            return null;
        if (root.val > key)
            root.left = deleteNode(root.left, key);
        else if (root.val < key)
            root.right = deleteNode(root.right, key);
        else {
            if (root.left == null && root.right == null) // deleted node is leaf
                return null;
            if (root.right == null) // replace deleted with its left child
                return root.left;
            // leftmost node in right child subtree
            TreeNode curr = root.right;
            while (curr.left != null) {
                curr = curr.left;
            }
            root.val = curr.val;
            curr.val = key;
            root.right = deleteNode(root.right, key);
        }
        return root;
    }
}

/**
 * Q-501 Find the Mode in Binary Search Tree
 */
class FindModeInBinarySearchTree {
    private void inorder(TreeNode root, HashMap<Integer, Integer> map, int[] maxCount) {
        if (root == null)
            return;
        inorder(root.left, map, maxCount);
        int val = 1;
        if (map.containsKey(root.val))
            val = map.get(root.val)+1;
        map.put(root.val, val);
        if (maxCount[0] < val)
            maxCount[0] = val;
        inorder(root.right, map, maxCount);
    }

    public int[] findMode(TreeNode root) {
        List<Integer> result = new ArrayList<Integer>();
        int[] maxCount = new int[]{0};
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        inorder(root, map, maxCount);
        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            if (e.getValue() == maxCount[0])
                result.add(e.getKey());

        }
        int i = 0;
        int[] res = new int[result.size()];
        for (Integer m : result)
            res[i++] = m.intValue();
        return res;
    }
}

/**
 * Q-508 Most Frequent Subtree Sum (data structure)
 *
 * Given the root of a tree, you are asked to find the most frequent subtree sum. The subtree sum of a node is
 * defined as the sum of all the node values formed by the subtree rooted at that node (including the node
 * itself). So what is the most frequent subtree sum value? If there is a tie, return all the values with the
 * highest frequency in any order.
 */
class MostFrequentSubtreeSum {
    static class ValueEntry {
        public int freq;
        public int sum;

        ValueEntry(int f, int v) { this.freq = f; this.sum = v; }
    }

    private static int treeSum(TreeNode root, Map<Integer, Integer> valMap, Set<ValueEntry> valSet) {
        if (root == null)
            return 0;
        int left = treeSum(root.left, valMap, valSet);
        int right = treeSum(root.right, valMap, valSet);
        int s = left + right + root.val;
        int v = 1;
        if (valMap.containsKey(s)) {
            v = valMap.get(s) + 1;
        }
        valMap.put(s, v);
        ValueEntry oldEntry = new ValueEntry(v-1, s);
        valSet.remove(oldEntry);
        valSet.add(new ValueEntry(v, s));
        return s;
    }

    /* use a balanced binary search tree to track frequent subtree sum values */
    public static int[] findFrequentTreeSum(TreeNode root) {
        Map<Integer, Integer> valMap = new HashMap<Integer, Integer>();
        TreeSet<ValueEntry> valSet = new TreeSet<ValueEntry>(new Comparator<ValueEntry>() {
            @Override
            public int compare(ValueEntry v1, ValueEntry v2) {
                if (v1.sum == v2.sum && v1.freq == v2.freq)
                    return 0;
                return v1.freq >= v2.freq ? 1 : -1;
            }
        });
        treeSum(root, valMap, valSet);
        ArrayList<Integer> result = new ArrayList<Integer>();
        int valFreq = 0;
        Iterator<ValueEntry> iterator = valSet.descendingIterator();
        while (iterator.hasNext()) {
            ValueEntry ve = iterator.next();
            if (result.size() == 0) {
                valFreq = ve.freq;
            }
            if (ve.freq < valFreq)
                break;
            result.add(ve.sum);
        }
        int i = 0;
        int[] res = new int[result.size()];
        for (Integer v : result)
            res[i++] = v;
        return res;
    }

    public static void test() {
        System.out.println("Most Frequent Subtree Sum");
        TreeNode n0 = new TreeNode(3);
        TreeNode n1 = new TreeNode(-3);
        TreeNode n2 = new TreeNode(3);
        n0.left = n1;
        n0.right = n2;
        TreeNode n3 = new TreeNode(1);
        TreeNode n4 = new TreeNode(-3);
        n2.left = n3;
        n2.right = n4;
        int[] res = findFrequentTreeSum(n0);
        for (int v : res) {
            System.out.print(v);
            System.out.print(",");
        }
        System.out.println();
    }
}

/** Q-515 Find Largest Value in Each Tree Row */
class FindLargestValueInEachTreeRow {
    public static List<Integer> largestValues(TreeNode root) {
        List<Integer> result = new ArrayList<Integer>();
        if (root == null)
            return result;

        ArrayDeque<TreeNode> queue = new ArrayDeque<TreeNode>();
        queue.add(root);
        TreeNode nextFirst = null;
        int currLvl = Integer.MIN_VALUE;
        while (!queue.isEmpty()) {
            TreeNode n = queue.poll();
            if (n == nextFirst) {
                result.add(currLvl);
                currLvl = Integer.MIN_VALUE;
                nextFirst = null;
            }
            if (nextFirst == null)
                nextFirst = n.left != null ? n.left : n.right;
            if (n.val > currLvl)
                currLvl = n.val;
            if (n.left != null)
                queue.add(n.left);
            if (n.right != null)
                queue.add(n.right);
        }
        result.add(currLvl);
        return result;
    }
}

/** Q-652 Find Duplicate Subtree */
class FindDuplicateSubtree {
    private static String find(TreeNode root, HashMap<String, Integer> subtreeMap, ArrayList<TreeNode> result) {
        // encode a subtree structure using post-order serialization
        StringBuilder serial = new StringBuilder();
        serial.append(root.left != null ? find(root.left, subtreeMap, result) : "null");
        serial.append(root.right != null ? find(root.right, subtreeMap, result) : "null");
        serial.append(root.val);
        String val = serial.toString().trim();
        int count = 1;
        if (subtreeMap.containsKey(val)) {
            if (subtreeMap.get(val) == 1)
                result.add(root);
            count = subtreeMap.get(val).intValue() + 1;
        }
        subtreeMap.put(val, count);
        return val;
    }

    public static List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        ArrayList<TreeNode> result = new ArrayList<TreeNode>();
        if (root == null)
            return result;
        HashMap<String, Integer> subtreeMap = new HashMap<String, Integer>();
        find(root, subtreeMap, result);
        System.out.println(Arrays.toString(result.toArray()));
        return result;
    }

    public static void test() {
        System.out.println("Q-652 Find Duplicate Tree");
        TreeNode n0 = new TreeNode(1), n1 = new TreeNode(2), n2 = new TreeNode(3), n3 = new TreeNode(4), n4 = new TreeNode(2),
                n5 = new TreeNode(4), n6 = new TreeNode(4);
        n0.left = n1; n0.right = n2; n1.left = n3; n2.left = n4; n2.right = n5; n4.left = n6;
        findDuplicateSubtrees(n0);

        TreeNode n10 = new TreeNode(0), n11 = new TreeNode(0), n12 = new TreeNode(0), n13 = new TreeNode(0), n14 = new TreeNode(0),
                n15 = new TreeNode(0), n16 = new TreeNode(0), n17 = new TreeNode(0), n18 = new TreeNode(0);
        n10.left = n11; n10.right = n12; n11.left = n13; n12.right = n14; n13.left = n15; n13.right = n16; n14.left = n17; n14.right = n18;
        findDuplicateSubtrees(n10);
    }
}

/** Q-654 Maximum Binary Tree */
class MaximumBinaryTree {
    private TreeNode construct(int[] nums, int l, int r) {
        if (l > r)
            return null;
        int maxnum = l;
        for (int i = l+1; i <= r; ++i)
            if (nums[i] > nums[maxnum])
                maxnum = i;
        TreeNode root = new TreeNode(nums[maxnum]);
        root.left = construct(nums, l, maxnum-1);
        root.right = construct(nums, maxnum+1, r);
        return root;
    }

    public TreeNode constructMaximumBinaryTree(int[] nums) {
        if (nums.length == 0)
            return null;
        return construct(nums, 0, nums.length-1);
    }
}

/** Q-669 Trim Binary Search Tree */
class TrimBinarySearchTree {
    public TreeNode trimBST(TreeNode root, int L, int R) {
        if (root == null) return null;
        if (root.val < L)
            return trimBST(root.right, L, R);
        if (root.val > R)
            return trimBST(root.left, L, R);
        root.left = trimBST(root.left, L, R);
        root.right = trimBST(root.right, L, R);
        return root;
    }
}

/**
 * Q-671 Second Minimum Node In a Binary Tree
 *
 * Given a non-empty special binary tree consisting of nodes with the non-negative value, where each node in this tree
 * has exactly two or zero sub-node. If the node has two sub-nodes, then this node's value is the smaller value among
 * its two sub-nodes. Given such a binary tree, you need to output the second minimum value in the set made of all the
 * nodes' value in the whole tree. If no such second minimum value exists, output -1 instead.
 */
class SecondMinimumNodeInBinaryTree {
    private int nextSmaller(TreeNode root, int parentVal) {
        if (root == null)
            return -1;
        if (root.val != parentVal)
            return root.val;
        int left = nextSmaller(root.left, parentVal);
        int right = nextSmaller(root.right, parentVal);
        if (left != -1 && right != -1)
            return left < right ? left : right;
        return left != -1 ? left : right;
    }

    public int findSecondMinimumValue(TreeNode root) {
        if (root == null) return -1;
        int left = nextSmaller(root.left, root.val);
        int right = nextSmaller(root.right, root.val);
        if (left != -1 && right != -1)
            return left < right ? left : right;
        return left != -1 ? left : right;
    }
}

/** Q-784 Letter Case Permutation */
class LetterCasePermutation {
    public static void helper(char[] str, int index, HashSet<String> result) {
        if (index >= str.length) return;
        if (!Character.isLetter(str[index])) {
            helper(str, index + 1, result);
            return;
        }

        if (Character.isUpperCase(str[index])) {
            str[index] = Character.toLowerCase(str[index]);
            result.add(String.valueOf(str));
        }
        helper(str, index + 1, result);
        if (Character.isLowerCase(str[index])) {
            str[index] = Character.toUpperCase(str[index]);
            result.add(String.valueOf(str));
        }
        helper(str, index + 1, result);
    }

    public static List<String> letterCasePermutation(String S) {
        ArrayList<String> result = new ArrayList<String>();
        if (S == null) return result;
        HashSet<String> perms = new HashSet<String>();
        perms.add(S);
        char[] str = S.toCharArray();
        helper(str, 0, perms);
        result.addAll(perms);
        System.out.println(Arrays.toString(result.toArray()));
        return result;
    }

    public static void test() {
        System.out.println("Q-784 Letter Case Permutation");
        letterCasePermutation("");
        letterCasePermutation("a1b2");
        letterCasePermutation("12345");
    }
}

class BinarySearch {
    public static int search(int[] nums, int target) {
        if (nums.length == 0) return -1;
        int l = 0, r = nums.length-1;
        while (l < r) {
            int m = l + (r-l)/2;
            if (nums[m] >= target)
                r = m;
            else
                l = m+1;
        }
        return nums[r] == target ? r : -1;
    }

    public static void test() {
        int[] a = {-1,0,3,5,9,12};
        System.out.println(search(a, 9));
    }
}


public class LeetcodeEasy {

    public static void main(String[] args) {
        System.out.println("Leetcode Easy Level");
        KthSmallestElements.run();
        HouseRobber.run();
        MostFrequentSubtreeSum.test();
        FindDuplicateSubtree.test();
        LetterCasePermutation.test();
    }

}