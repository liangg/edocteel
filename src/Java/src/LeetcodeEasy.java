/**
 * Leetcode Questions - easy level, Tree
 */

import apple.laf.JRSUIUtils;

import java.util.*;

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

/** Q-1 Two Sum */
class TwoSum {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> posMap = new HashMap<>();
        for (int l = 0; l < nums.length; ++l) {
            int d = target - nums[l];
            if (posMap.containsKey(d))
                return new int[]{posMap.get(d), l};
            posMap.put(nums[l], l);
        }
        return new int[0];
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

    public boolean isBalanced_OLD(TreeNode root) {
        if (root == null)
            return true;
        int[] pair = new int[2];
        pair[0] = pair[1] = 1;
        return balanced(root, 1, pair, 0);
    }

    private int getDepth(TreeNode root, int depth) {
        if (root == null) return depth;
        int ld = getDepth(root.left, depth+1);
        if (ld == -1)
            return -1;
        int rd = getDepth(root.right, depth+1);
        if (rd == -1)
            return -1;
        if (Math.abs(ld-rd) > 1)
            return -1;
        return Math.max(ld, rd);
    }

    public boolean isBalanced(TreeNode root) {
        if (root == null)
            return true;
        // get the max depth, unless -1 if not balanced
        return getDepth(root, 0) != -1;
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
 * Q-112 Path Sum
 */
class PathSum {
    private static boolean helper(TreeNode root, int sum, int currSum) {
        if (root == null) return false;
        if (root.left == null && root.right == null) return currSum+root.val == sum;
        return helper(root.left, sum, currSum+root.val) || helper(root.right, sum, currSum+root.val);
    }

    public static boolean hasPathSum(TreeNode root, int sum) {
        if (root == null) return false;
        return helper(root, sum, 0);
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

/**
 * Q-124 Binary Tree Maximum Path Sum
 *
 * Given a non-empty binary tree, find the maximum path sum. For this problem, a path is defined as any sequence of
 * nodes from some starting node to any node in the tree along the parent-child connections. The path must contain
 * at least one node and does not need to go through the root.
 */
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

/** Q-173 Binary Search Tree Iterator */
class BSTIterator {
    private Stack<TreeNode> stack = new Stack<>();

    public BSTIterator(TreeNode root) {
        TreeNode n = root;
        while (n != null) {
            stack.push(n);
            n = n.left;
        }
    }

    /** @return the next smallest number */
    public int next() {
        TreeNode n = stack.pop();
        TreeNode next = n.right;
        while (next != null) {
            stack.push(next);
            next = next.left;
        }
        return n.val;
    }

    /** @return whether we have a next smallest number */
    public boolean hasNext() {
        return !stack.empty();
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

/**
 * Q-161 One Edit Distance
 */
class OneEditDistance {
    public static boolean isOneEditDistance(String s, String t) {
        if (Math.abs(s.length() - t.length()) > 1)
            return false;
        for (int i = 0; i < Math.min(s.length(), t.length()); ++i) {
            if (s.charAt(i) != t.charAt(i)) {
                if (s.length() == t.length()) return s.substring(i+1).equals(t.substring(i+1));
                else if (s.length() > t.length()) return s.substring(i+1).equals(t.substring(i));
                else return s.substring(i).equals(t.substring(i+1));
            }
        }
        return Math.abs(s.length()-t.length()) == 1;
    }

    public static void test() {
        System.out.println("Q-161 One Edit Distance");
        System.out.println(isOneEditDistance("acb", "ab")); // true
        System.out.println(isOneEditDistance("love", "lv")); // false
        System.out.println(isOneEditDistance("meet", "meat")); // true
        System.out.println(isOneEditDistance("meet", "meet")); // false
    }
}

/**
 * Q-167 Two Sum 2
 */
class TwoSum2 {
    public int[] twoSum(int[] numbers, int target) {
        for (int l = 0, r = numbers.length-1; l < r; ) {
            int s = numbers[l] + numbers[r];
            if (s == target)
                // return answer index is non-zero based
                return new int[]{l+1, r+1};
            else if (s > target)
                r--;
            else
                l++;
        }
        return new int[]{};
    }
}


/**
 * Q-172 Factorial Trailing Zeroes
 *
 * Given an integer n, return the number of trailing zeroes in n!.
 */
class FactorialTrailingZeroes {
    public int trailingZeroes(int n) {
        int result = 0;
        // count number of 10, i.e. number of 5
        while (n > 0) {
            result += n/5;
            n /= 5;
        }
        return result;
    }
}


/**
 * Q-186 Reverse Words in a String II
 */
class ReverseWordsInString2 {
    private static void reverse(char[] s, int begin, int end) {
        for (int l = begin, r = end; l < r; ++l, --r) {
            char t = s[l];
            s[l] = s[r];
            s[r] = t;
        }
    }

    public static void reverseWords(char[] s) {
        reverse(s, 0, s.length-1);
        System.out.print(Arrays.toString(s));
        int l = 0, r = 0;
        for (; r < s.length; ++r) {
            if (s[r] == ' ') {
                if (l < r) reverse(s, l, r-1);
                l = r+1;
            }
        }
        if (l < s.length) reverse(s, l, s.length-1);
        System.out.print(Arrays.toString(s));
    }

    public static void test() {
        System.out.println("Q-186 Reverse Words in a String II");
        String s = "the  sky is a blue";
        reverseWords(s.toCharArray());
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

/** Q-206 Reverse Linked List */
class ReverseLinkedList {
    public ListNode reverseList(ListNode head) {
        if (head == null)
            return head;
        ListNode newHead = head, tail = head;
        for (ListNode p = head.next, next; p!= null; p = next) {
            next = p.next;
            p.next = newHead;
            newHead = p;
        }
        tail.next = null;
        return newHead;
    }
}

/**
 * Q-222 Count Complete Tree Node
 */
class CountCompleteTreeNode {
    public static int countNodes(TreeNode root) {
        if (root == null) return 0;
        int ldepth = 0;
        for (TreeNode n = root; n != null; n = n.left, ldepth++);
        int rdepth = 0;
        for (TreeNode n = root; n != null; n = n.right, rdepth++);
        if (ldepth == rdepth)
            return (int)Math.pow(2, ldepth)-1;
        int lc = countNodes(root.left), rc = countNodes(root.right);
        return  lc + rc + 1;
    }
}

/** Q-226 Invert Binary Tree */
class InvertBinaryTree {
    public TreeNode invertTree(TreeNode root) {
        if (root == null)
            return null;
        TreeNode left = invertTree(root.left);
        TreeNode right = invertTree(root.right);
        root.left = right;
        root.right = left;
        return root;
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
        return last.val;
    }

    // in-order traversal
    public static int kthSmallest2(TreeNode root, int k) {
        if (root == null)
            return 0;
        Stack<TreeNode> stack = new Stack<TreeNode>();
        int count = 0;
        TreeNode p = root;
        while (p != null || !stack.empty()) {
            for (; p != null; p = p.left)
                stack.push(p);
            p = stack.pop();
            if (count == k-1)
                return p.val;
            count++;
            p = p.right;
        }
        return 0;
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
        System.out.printf("kth = %d\n", kthSmallest2(n9, 3)); // 5
        System.out.printf("kth = %d\n", kthSmallest2(n9, 6)); // 14
    }
}

/** Q-235 Lowest Common Ancestor of a Binary Search Tree */
class LowestCommonAncestorOfBinarySearchTree {
    private TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null)
            return null;
        if (root.val < p.val)
            return lca(root.right, p, q);
        if (root.val > q.val)
            return lca(root.left, p, q);
        // LCA can be one of p and q
        return root;
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        return p.val < q.val ? lca(root, p, q) : lca(root, q, p);
    }
}

/**
 * Q-250 Count Univalue Subtrees
 */
class CountUnivalueSubtrees {
    private static boolean univalue(TreeNode root, int[] book) {
        if (root == null) return true;
        if (root.left == null && root.right == null) {
            book[0]++;
            return true;
        }
        boolean left = univalue(root.left, book);
        boolean right = univalue(root.right, book);
        if (left && (root.left == null || root.val == root.left.val) &&
            right && (root.right == null || root.val == root.right.val)) {
            book[0]++;
            return true;
        }
        return false;
    }

    public static int countUnivalSubtrees(TreeNode root) {
        int[] book = new int[1];
        univalue(root, book);
        return book[0];
    }

    public static void test() {
        System.out.println("Q-250 Count Univalue Subtrees");
        TreeNode n1 = new TreeNode(5);
        TreeNode n2 = new TreeNode(1);
        TreeNode n3 = new TreeNode(5);
        TreeNode n4 = new TreeNode(5);
        TreeNode n5 = new TreeNode(5);
        TreeNode n6 = new TreeNode(5);
        n1.left = n2; n1.right = n5;
        n2.left = n3; n2.right = n4;
        n5.right = n6;
        System.out.println(countUnivalSubtrees(n1)); // 4
    }
}

/**
 * Q-270 Closest Binary Search Tree Value
 */
class ClosestBinarySearchTreeValue {
    public int closestValue(TreeNode root, double target) {
        TreeNode best = root;
        for (TreeNode n = root; n != null; ) { // check a root and its in-order neightbours
            if (Math.abs(best.val - target) < Math.abs(n.val - target))
                best = n;
            if (n.val > target) n = n.left;
            else n = n.right;
        }
        return best.val;
    }
}

/** Q-283 Move Zeroes */
class MoveZeroes {
    public static void moveZeroes(int[] nums) {
        for (int l = 0, r = 0; r < nums.length; r++) {
            if (nums[r] != 0) {
                int t = nums[l];
                nums[l] = nums[r];
                nums[r] = t;
                l++;
            }
        }
        System.out.println(Arrays.toString(nums));
    }

    public static void test() {
        System.out.println("Q-283 Move Zeroes");
        moveZeroes(new int[]{0,1,0,3,12});
        moveZeroes(new int[]{1,0,3,0,4,12});
    }
}

/**
 * Q-285 Inorder Successor in BST
 */
class InorderSuccessorInBST {
    private void inorder(TreeNode root, TreeNode p, TreeNode[] result) {
        if (root == null) return;
        inorder(root.left, p, result);
        if (result[0] == p && result[1] == null) { // result[0] predecessor
            result[1] = root;
            return;
        }
        result[0] = root;
        inorder(root.right, p, result);
    }

    public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
        TreeNode[] result = new TreeNode[2];
        inorder(root, p, result);
        return result[1];
    }
}

/**
 * Q-298 Binary Tree Longest Consecutive Sequence
 *
 * Q-549 Binary Tree Longest Consecutive Sequence II
 */
class BinaryTreeLongestConsecutiveSequence {
    private void longest(TreeNode root, int par, int len, int[] result) {
        if (root == null) return;
        if (root.val == par+1)
            len += 1;
        else
            len = 1;
        if (len > result[0]) result[0] = len;
        longest(root.left, root.val, len, result);
        longest(root.right, root.val, len, result);
    }

    // Q-298 Binary Tree Longest Consecutive Sequence, strict parent-child tree path
    public int longestConsecutive(TreeNode root) {
        int[] result = new int[1];
        if (root == null) return result[0];
        longest(root, root.val, 0, result);
        return result[0];
    }

    // Q-549 Binary Tree Longest Consecutive Sequence II
    public int longestConsecutive2(TreeNode root) {
        if (root == null) return 0;
        int plen = longest2(root, 1) + longest2(root, -1) + 1;
        return Math.max(plen, Math.max(longestConsecutive2(root.left), longestConsecutive2(root.right)));
    }

    private int longest2(TreeNode root, int diff) {
        if (root == null) return 0;
        int left = longest2(root.left, diff);
        int right = longest2(root.right, diff);
        int pl = 0;
        if (root.left != null && root.val+diff == root.left.val)
            pl = left+1;
        if (root.right != null && root.val+diff == root.right.val)
            pl = Math.max(pl, right+1);
        return pl;
    }
}

/**
 * Q-314 Binary Tree Vertical Order Traversal
 */
class BinaryTreeVerticalOrderTraversal {
    private static class Pair {
        public TreeNode node;
        public int index;
        Pair(TreeNode node, int index) {
            this.node = node;
            this.index = index;
        }
    }

    public List<List<Integer>> verticalOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Map<Integer, List<Integer>> treeMap = new TreeMap<>();
        Queue<Pair> queue = new ArrayDeque<>();
        queue.add(new Pair(root, 0));
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; ++i) {
                Pair p = queue.poll();
                List<Integer> vl = treeMap.containsKey(Integer.valueOf(p.index)) ? treeMap.get(Integer.valueOf(p.index)):
                    new ArrayList<>();
                vl.add(p.node.val);
                treeMap.put(Integer.valueOf(p.index), vl);
                if (p.node.left != null)
                    queue.add(new Pair(p.node.left, p.index-1));
                if (p.node.right != null)
                    queue.add(new Pair(p.node.right, p.index+1));
            }
        }
        for (Map.Entry<Integer, List<Integer>> e : treeMap.entrySet())
            result.add(e.getValue());
        return result;
    }
}

/**
 * Q-333 Largest BST
 *
 * Given a binary tree, find the largest subtree which is a Binary Search Tree (BST), where largest means subtree with
 * largest number of nodes in it.
 */
class LargestBST {
    private static boolean valid(TreeNode root, long max, long min) {
        if (root == null)
            return true;
        if (root.val >= max || root.val <= min)
            return false;
        return valid(root.left, root.val, min) && valid(root.right, max, root.val);
    }

    private static int count(TreeNode root) {
        if (root == null)
            return 0;
        return count(root.left) + count(root.right) + 1;
    }

    public static int largestBSTSubtree(TreeNode root) {
        if (root == null)
            return 0;
        if (valid(root, Long.MAX_VALUE, Long.MIN_VALUE))
            return count(root);
        return Math.max(largestBSTSubtree(root.left), largestBSTSubtree(root.right));
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
class HouseRobber3 {
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

/**
 * Q-339 Nested List Weight Sum
 *
 * Given a nested list of integers, return the sum of all integers in the list weighted by their depth. Each element
 * is either an integer, or a list -- whose elements may also be integers or other lists.
 */
class NestedListWeightSum {
    interface NestedInteger {
        // @return true if this NestedInteger holds a single integer, rather than a nested list.
        public boolean isInteger();

        // @return the single integer that this NestedInteger holds, if it holds a single integer
        // Return null if this NestedInteger holds a nested list
        public Integer getInteger();

        // Return null if this NestedInteger holds a single integer
        public List<NestedInteger> getList();
    }

    private int helper(List<NestedInteger> nestedList, int depth) {
        int sum = 0;
        for (NestedInteger ni : nestedList) {
            if (ni.isInteger()) sum += depth*ni.getInteger();
            else sum += helper(ni.getList(), depth+1);
        }
        return sum;
    }

    public int depthSum(List<NestedInteger> nestedList) {
        return helper(nestedList, 1);
    }
}

/** Q-345 Reverse Vowels of a String */
class ReverseVowelsOfString {
    public static String reverseVowels(String s) {
        Character[] v = {'a','e','i','o','u','A','E','I','O','U'};
        Set<Character> vowels = new HashSet<>();
        vowels.addAll(Arrays.asList(v));
        char[] schars = s.toCharArray();
        for (int l = 0, r = schars.length-1; l < r; l++, r--) {
            if (vowels.contains(schars[l]) && vowels.contains(schars[r])) {
                char c = schars[l];
                schars[l] = schars[r];
                schars[r] = c;
            } else if (vowels.contains(schars[l]))
                l--;
            else if (vowels.contains(schars[r]))
                r++;
        }
        return new String(schars);
    }

    public static void test() {
        System.out.println("Q-345 Reverse Vowels of a String");
        System.out.println(reverseVowels("leetcode"));
    }
}

/**
 * Q-359 Logger Rate Limiter
 *
 * Design a logger system that receive stream of messages along with its timestamps, each message should be printed
 * if and only if it is not printed in the last 10 seconds. Given a message and a timestamp (in seconds granularity),
 * return true if the message should be printed in the given timestamp, otherwise returns false.
 */
class LoggerRateLimiter {
    private Map<String, Integer> messageStore = new HashMap<>();

    public boolean shouldPrintMessage(int timestamp, String message) {
        int MSG_RETENTION = 10;
        if (messageStore.containsKey(message)) {
            if (messageStore.get(message) + MSG_RETENTION > timestamp)
                return false;
        }
        messageStore.put(message, timestamp);
        return true;
    }
}

/** Q-383 Ransom Note */
class RansomNote {
    public static boolean canConstruct(String ransomNote, String magazine) {
        int[] counts = new int[256];
        for (char c : magazine.toCharArray())
            counts[c]++;
        for (char c : ransomNote.toCharArray()) {
            if (counts[c] <= 0) return false;
            counts[c] -= 1;
        }
        return true;
    }
}

/** Q-392 Is Subsequence */
class IsSubsequence {
    public boolean isSubsequence(String s, String t) {
        int p1 = 0, p2 = 0;
        for (p1 = 0, p2 = 0; p1 < s.length() && p2 < t.length(); p2++) {
            if (s.charAt(p1) == t.charAt(p2))
                p1++;
        }
        return p1 == s.length();
    }
}

/** Q-415 Add Strings */
class AddStrings {
    public static String addStrings(String num1, String num2) {
        StringBuilder sum = new StringBuilder("");
        int i = num1.length()-1, j = num2.length()-1, carryover = 0;
        for (; i >= 0 && j >= 0; --i, --j) {
            int d1 = num1.charAt(i) - '0', d2 = num2.charAt(j) - '0';
            int s = d1+d2+carryover;
            sum.append(s%10);
            carryover = s/10;
        }
        for (; i >= 0; --i) {
            int s = num1.charAt(i)-'0'+carryover;
            sum.append(s%10);
            carryover = s/10;
        }
        for (; j >= 0; --j) {
            int s = num2.charAt(j)-'0'+carryover;
            sum.append(s%10);
            carryover = s/10;
        }
        if (carryover > 0) sum.append(carryover);
        return sum.reverse().toString();
    }

    public static void test() {
        System.out.println("Q-415 Add Strings");
        System.out.println(addStrings("568", "2892")); // 3460
    }
}

/** Q-441 Arranging Coins */
class ArrangeCoins {
    public static int arrangeCoins(int n) {
        int result = 0;
        while (n >= result)
            n -= result++;
        return result-1;
    }

    public static void test() {
        System.out.println(arrangeCoins(1));
        System.out.println(arrangeCoins(5));
        System.out.println(arrangeCoins(6));
        System.out.println(arrangeCoins(8));
    }
}

/**
 * Q-443 String Compression
 */
class StringCompression {
    public static int compress(char[] chars) {
        char curr = chars[0];
        int count = 1, w = 0;
        for (int i = 1; i < chars.length; ++i) {
            if (chars[i] == curr) {
                count++;
            } else {
                chars[w++] = curr;
                if (count > 1) {
                    String cstr = Integer.toString(count);
                    for (char cc : cstr.toCharArray()) chars[w++] = cc;
                }
                curr = chars[i];
                count = 1;
            }
        }
        chars[w++] = curr;
        if (count > 1) {
            String cstr = Integer.toString(count);
            for (char cc : cstr.toCharArray()) chars[w++] = cc;
        }
        return w;
    }

    public static void test() {
        System.out.println("String Compression");
        System.out.println(compress("abbbbbbbbbbbbb".toCharArray()));
    }
}

/**
 * Q-449 Serialize and Deserialize BST
 *
 * Design an algorithm to serialize and deserialize a binary search tree. There is no restriction on how your
 * serialization/deserialization algorithm should work. You just need to ensure that a binary search tree can
 * be serialized to a string and this string can be deserialized to the original tree structure.
 */
class SerializeAndDeserializeBST {

    private static void serializeHelper(TreeNode root, StringBuilder serialized) {
        if (root == null) {
            serialized.append("# ");
            return;
        }
        serialized.append(Integer.toString(root.val));
        serialized.append(" ");
        serializeHelper(root.left, serialized);
        serializeHelper(root.right, serialized);
    }

    // Encodes a tree to a single string.
    public static String serialize(TreeNode root) {
        StringBuilder serialized = new StringBuilder("");
        serializeHelper(root, serialized);
        return serialized.toString();
    }

    private static TreeNode deserializeHelper(String[] data, int index, int[] position) {
        if (index == data.length || data[index].equals("#")) {
            position[0] = index;
            return null;
        }
        TreeNode n = new TreeNode(Integer.parseInt(data[index]));
        n.left = deserializeHelper(data, index+1, position);
        n.right = deserializeHelper(data, position[0]+1, position);
        return n;
    }

    // Decodes your encoded data to tree.
    public static TreeNode deserialize(String data) {
        String[] split = data.split(" ");
        int[] position = new int[1];
        TreeNode root = deserializeHelper(split, 0, position);
        return root;
    }

    public static void test() {
        System.out.println("Q-449 Serialize and Deserialize BST");
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
        String serialized = serialize(n9);
        System.out.println(serialized); // 9 5 3 2 # # # # 15 12 # 14 # # 16 # #
        TreeNode root = deserialize(serialized);
        System.out.println(serialize(root));
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

/** Q-448 Find All Numbers Disappeared in an Array */
class FindAllNumbersDisappearedInArray {
    public static List<Integer> findDisappearedNumbers(int[] nums) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nums.length; ++i) {
            if (nums[i] == i+1) continue;
            int v = nums[i];
            while (nums[v-1] != v) {
                int t = nums[v-1];
                nums[v-1] = v;
                nums[i] = t;
                v = nums[i];
            }
        }
        for (int i = 0; i < nums.length; ++i)
            if (nums[i] != i+1)
                result.add(i+1);
        System.out.println(Arrays.toString(nums));
        return result;
    }

    public static void test() {
        System.out.println("Q-448 Find All Numbers Disappeared in an Array");
        System.out.println(findDisappearedNumbers(new int[]{3,1,3,5,4}));
        System.out.println(findDisappearedNumbers(new int[]{4,3,2,7,8,2,3,1})); // [5,6]
    }
}

/** Q-459 Repeated Substring Pattern */
class RepeatedSubstringPattern {
    public static boolean repeatedSubstringPattern(String s) {
        for (int i = s.length()/2; i > 0; --i) {
            if (s.length() % i != 0)
                continue;
            String first = s.substring(0, i);
            boolean found = true;
            for (int j = i; j < s.length(); j += i) {
                String str = s.substring(j, j+i);
                if (!str.equals(first)) {
                    found = false;
                    break;
                }
            }
            if (found)
                return found;
        }
        return false;
    }

    public static void test() {
        System.out.println("Q-459 Repeated Substring Pattern");
        System.out.println(repeatedSubstringPattern("abcabcabc"));
    }
}

/**
 * Q-463 Island Perimeter
 */
class IslandPerimeter {
    public int islandPerimeter(int[][] grid) {
        return 0;
    }
}

/**
 * Q-476 Number Complement
 *
 * Given a positive integer, output its complement number. The complement strategy is to flip the bits of its binary
 * representation.
 */
class NumberComplement {
    public static int findComplement(int num) {
        int mask = 0;
        for (int i = 31; i >= 0; --i) {
            if (mask != 0)
                mask |= (1 <<i);
            else if (((1 << i) & num) != 0)
                mask |= (1 <<i);
        }
        return num ^ mask;
    }

    public static void test() {
        System.out.println("Q-475 Number Complement");
        System.out.println(findComplement(5));
    }
}

/** Q-496 Next Greater Element I */
class NextGreaterElement1 {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> greaterRight = new HashMap<>();
        Stack<Integer> stack = new Stack<>();
        for (int n : nums2) {
            while (!stack.empty() && n > stack.peek()) {
                greaterRight.put(stack.peek(), n);
                stack.pop();
            }
            stack.add(n);
        }
        // note that num1 and num2 have no duplicates
        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; ++i) {
            if (greaterRight.containsKey(nums1[i]))
                result[i] = greaterRight.get(nums1[i]);
            else
                result[i] = -1;
        }
        return result;
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
    private static int dfs(TreeNode root, ArrayList<Integer> result, int[] maxCount, Map<Integer, Integer> sumCount) {
        if (root == null) return 0;
        int left = dfs(root.left, result, maxCount, sumCount);
        int right = dfs(root.right, result, maxCount, sumCount);
        int sum = root.val + left + right;
        int cnt = 1;
        if (sumCount.containsKey(sum)) {
            cnt = sumCount.get(sum) + 1;
        }
        if (cnt > maxCount[0]) {
            result.clear();
            result.add(sum);
            maxCount[0] = cnt;
        } else if (cnt == maxCount[0]) {
            result.add(sum);
        }
        sumCount.put(sum, cnt);
        return sum;
    }

    public static int[] findFrequentTreeSum(TreeNode root) {
        ArrayList<Integer> result = new ArrayList<>();
        int[] maxCount = new int[1];
        Map<Integer, Integer> sumCount = new HashMap<>();
        dfs(root, result, maxCount, sumCount);
        int[] res = new int[result.size()];
        int i = 0;
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

/** Q-530 Minimum Absolute Difference in BST */
class MinimumAbsoluteDifferenceInBST {
    private void inorder(TreeNode root, List<Integer> inordered) {
        if (root == null)
            return;
        inorder(root.left, inordered);
        inordered.add(root.val);
        inorder(root.right, inordered);
    }

    public int getMinimumDifference(TreeNode root) {
        List<Integer> inordered = new ArrayList<>();
        inorder(root, inordered);
        int result = Integer.MAX_VALUE;
        for (int i = 1; i < inordered.size(); ++i) {
            if (Math.abs(inordered.get(i)-inordered.get(i-1)) < result)
                result = Math.abs(inordered.get(i)-inordered.get(i-1));
        }
        return result;
    }
}

/** Q-538 Convert BST to Greater Tree */
class ConvertBstToGreaterTree {
    private void inorder(TreeNode root, int[] sum) {
        if (root == null)
            return;
        inorder(root.right, sum);
        root.val += sum[0];
        sum[0] = root.val;
        inorder(root.left, sum);
    }

    public TreeNode convertBST(TreeNode root) {
        int[] sum = new int[1];
        inorder(root, sum);
        return root;
    }
}

/** Q-543 Diameter of Binary Tree */
class DiameterOfBinaryTree {
    private int path(TreeNode root, int[] state) {
        if (root.left == null && root.right == null)
            return 0;
        int lp = 0;
        if (root.left != null)
            lp = 1 + path(root.left, state);
        int rp = 0;
        if (root.right != null)
            rp = 1 + path(root.right, state);
        if (lp + rp > state[0])
            state[0] = lp + rp;
        return lp > rp ? lp : rp;
    }

    public int diameterOfBinaryTree(TreeNode root) {
        int[] state = new int[1];
        if (root != null)
            path(root, state);
        return state[0];
    }
}

/**
 * Q-545 Boundary of Binary Tree
 */
class BoundaryOfBinaryTree {
    private static void left(TreeNode root, List<Integer> result) {
        if (root == null || (root.left == null && root.right == null)) return; // skip leaf
        result.add(root.val);
        left(root.left != null ? root.left : root.right, result);
    }

    private static void right(TreeNode root, List<Integer> result) {
        if (root == null || (root.left == null && root.right == null)) return; // skip leaf
        right(root.right != null ? root.right : root.left, result);
        result.add(root.val);
    }

    private static void leaves(TreeNode root, List<Integer> result) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            result.add(root.val);
            return;
        }
        leaves(root.left, result);
        leaves(root.right, result);
    }

    public static List<Integer> boundaryOfBinaryTree(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        if (root.left != null || root.right != null) result.add(root.val);
        left(root.left, result);
        leaves(root, result);
        right(root.right, result);
        return result;
    }

    public static void test() {
        System.out.println("Q-545 Boundary of Binary Tree");
        TreeNode n1 = new TreeNode(1);
        TreeNode n2 = new TreeNode(2);
        TreeNode n3 = new TreeNode(3);
        TreeNode n4 = new TreeNode(4);
        n1.right = n2;
        n2.left = n3;
        n2.right = n4;
        System.out.println(boundaryOfBinaryTree(n1)); // [1,3,4,2]
    }
}

/** Q-551 Student Attendance Record I */
class StudentAttendanceRecord1 {
    public boolean checkRecord(String s) {
        int absent = 0, late = 0;
        for (char c : s.toCharArray()) {
            if (c == 'A') {
                if (++absent >= 2)
                    return false;
                late = 0;
            } else if (c == 'L') {
                if (++late >= 3)
                    return false;
            } else {
                late = 0;
            }
        }
        return true;
    }
}

/**
 * Q-557 Reverse Words in a String 3
 */
class ReverseWordsInString {
    public static String reverseWords(String s) {
        char[] schars = s.toCharArray();
        for (int l = 0, r = 0; r < s.length();) {
            if (Character.isWhitespace(schars[l])) {
                l++;
                r++;
                continue;
            }
            while (r < s.length() && !Character.isWhitespace(schars[r]))
                r++;
            for (int ll = l, rr = r-1; ll < rr; ++ll, --rr) {
                char t = schars[rr];
                schars[rr] = schars[ll];
                schars[ll] = t;
            }
            l = r;
        }
        return new String(schars);
    }

    public static void test() {
        System.out.println("Q-557 Reverse Words in a String");
        System.out.println(reverseWords(" Let's  rock this town "));
        System.out.println(reverseWords(" Let's  rock this town"));
    }
}

/**
 * Q-563 Binary Tree Tilt
 *
 * The tilt of a tree node is defined as the absolute difference between the sum of all left subtree node values
 * and the sum of all right subtree node values. Null node has tilt 0. The tilt of the whole tree is defined as
 * the sum of all nodes' tilt.
 */
class BinaryTreeTilt {
    private static void tilt(TreeNode node, int[] pair) {
        if (node == null) {
            pair[0] = pair[1] = 0;
            return;
        }
        tilt(node.left, pair);
        int leftSum = pair[0];
        int leftTilt = pair[1];
        tilt(node.right, pair);
        int rightSum = pair[0];
        int rightTilt = pair[1];
        pair[0] = node.val + leftSum + rightSum;
        pair[1] = leftTilt + pair[1] + Math.abs(leftSum - rightSum);
    }

    public static int findTilt(TreeNode root) {
        int[] pair = new int[2];
        tilt(root, pair);
        return pair[1];
    }
}

/** Q-566 Reshape the Matrix */
class ReshapeMatrix {
    public int[][] matrixReshape(int[][] nums, int r, int c) {
        if (nums.length * nums[0].length != r*c)
            return nums;
        int origc = nums[0].length;
        int[][] result = new int[r][c];
        for (int i = 0; i < r; ++i) {
            for (int j = 0; j < c; ++j) {
                int k = i*c + j;
                result[i][j] = nums[k/origc][k%origc];
            }
        }
        return result;
    }
}

/** Q-581 Shortest Unsorted Continuous Subarray */
class ShortestUnsortedContinuousSubarray {
    public static int findUnsortedSubarray(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        int l = 0, r = nums.length-1;
        for (; l < nums.length && nums[l] == sorted[l]; ++l);
        for (; r >= l && nums[r] == sorted[r]; --r);
        return r - l + 1;
    }

    public static void test() {
        System.out.println("Q-581 Shortest Unsorted Continuous Subarray");
        System.out.println(findUnsortedSubarray(new int[]{2,6,4,8,10,9,15})); // 5
        System.out.println(findUnsortedSubarray(new int[]{1,2,3,4})); // 0
    }
}

/** Q-594 Longest Harmonious Subsequence */
class LongestHarmoniousSubsequence {
    public static int findLHS(int[] nums) {
        if (nums.length == 0)
            return 0;
        int[] clone = nums.clone();
        Arrays.sort(clone);
        int result = 0, curr = clone[0], last = clone[0], lastCount = 0, count = 1;
        for (int i = 1; i < clone.length; ++i) {
            if (clone[i] == clone[i-1]) {
                count++;
            } else {
                if (curr-last == 1) {
                    if (lastCount + count > result)
                        result = lastCount + count;
                }
                last = curr;
                lastCount = count;
                curr = clone[i];
                count = 1;
            }
        }
        if (curr-last == 1) {
            if (lastCount + count > result)
                result = lastCount + count;
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-594 Longest Harmonious Subsequence");
        System.out.println(findLHS(new int[]{1,3,2,2,5,2,3,7})); // 5
        System.out.println(findLHS(new int[]{1,2,2,1})); // 4
        System.out.println(findLHS(new int[]{1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0}));
    }
}

/** Q-605 Can Place Flowers */
class CanPlaceFlowers {
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        return false;
    }
}

/** Q-606 Construct String from Binary Tree */
class ConstructStringFromBinaryTree {
    public String tree2str(TreeNode t) {
        if (t == null) return "";
        StringBuilder result = new StringBuilder("");
        result.append(Integer.toString(t.val));
        if (t.left != null) {
            result.append('(');
            result.append(tree2str(t.left));
            result.append(')');
        }
        if (t.right != null) {
            if (t.left == null) result.append("()");
            result.append('(');
            result.append(tree2str(t.right));
            result.append(')');
        }
        return result.toString();
    }
}

/** Q-645 Set Mismatch */
class SetMismatch {
    public static int[] findErrorNums(int[] nums) {
        // nums has 1 - N
        for (int i = 0; i < nums.length; ++i) {
            while (nums[i] != nums[nums[i]-1]) {
                int t = nums[i], idx = nums[i]-1;
                nums[i] = nums[idx];
                nums[idx] = t;
            }
        }
        for (int i = 0; i < nums.length; ++i) {
            if (nums[i]-1 != i)
                return new int[]{nums[i], i+1};
        }
        return new int[]{-1,-1};
    }

    public static void test() {
        System.out.println("Q-645 Set Mismatch");
        System.out.println(Arrays.toString(findErrorNums(new int[]{4,2,1,2})));
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

/** Q-653 Two Sum IV - Input is a BST */
class TwoSumInBST {
    private boolean helper(TreeNode root, int k, HashSet<Integer> memo) {
        if (root == null)
            return false;
        int v = k - root.val;
        if (memo.contains(v))
            return true;
        memo.add(root.val);
        return helper(root.left, k, memo) || helper(root.right, k, memo);
    }

    public boolean findTarget(TreeNode root, int k) {
        HashSet<Integer> memo = new HashSet<>();
        return helper(root, k, memo);
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

/** Q-662 Maximum Width of Binary Tree */
class MaximumWidthOfBinaryTree {
    private static class NodeIndex {
        public TreeNode node;
        public int index;

        public NodeIndex(TreeNode n, int i) {
            node = n;
            index = i;
        }
    }

    public int widthOfBinaryTree(TreeNode root) {
        if (root == null)
            return 0;
        int result = 1;
        Queue<NodeIndex> queue = new ArrayDeque<>();
        queue.add(new NodeIndex(root, 0));
        while (!queue.isEmpty()) {
            int left = queue.peek().index, right = 0, counts = queue.size();
            for (int i  = 0; i < counts; ++i) {
                NodeIndex ni = queue.remove();
                right = ni.index;
                if (ni.node.left != null)
                    queue.add(new NodeIndex(ni.node.left, 2*ni.index+1));
                if (ni.node.right != null)
                    queue.add(new NodeIndex(ni.node.right, 2*ni.index+2));
            }
            if (right-left+1 > result)
                result = right-left+1;
        }
        return result;
    }
}

/** Q-665 Non-decreasing Array */
class NondecreasingArray {
    public boolean checkPossibility(int[] nums) {
        boolean changed = false;
        for (int i = 1; i < nums.length; ++i) {
            if (nums[i] < nums[i-1]) {
                if (changed) return false;
                if (i == 1 || nums[i-2] <= nums[i]) nums[i-1] = nums[i]; // 4，2，3 or -1，4，2，3
                else nums[i] = nums[i-1]; // 2，3，3，2，4
                changed = true;
            }
        }
        return true;
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

/** Q-686 Repeated String Match */
class RepeatedStringMatch {
    public static int repeatedStringMatch(String A, String B) {
        String t = A;
        for (int i = 1; i <= (B.length()/A.length() + 2); ++i) {
            if (t.indexOf(B) != -1) return i;
            t += A;
        }
        return -1;
    }

    public static void test() {
        System.out.println("Q-686 Repeated String Match");
        System.out.println(repeatedStringMatch("abc", "cabcabca"));
    }
}

/**
 * Q-687 Longest Univalue Path
 *
 * Given a binary tree, find the length of the longest path where each node in the path has the same value.
 */
class LongestUnivaluePath {
    private void helper(TreeNode root, int[] pair) {
        if (root == null) {
            pair[0] = pair[1] = 0;
            return;
        }
        helper(root.left, pair);
        int left0 = pair[0];
        int left1 = pair[1];
        helper(root.right, pair);
        int right0 = pair[0];
        int right1 = pair[1];
        int left = (root.left != null && root.val == root.left.val) ? left0+1 : 0;
        int right = (root.right != null && root.val == root.right.val) ? right0+1 : 0;
        pair[0] = left > right ? left : right;
        pair[1] = left1 > right1 ? left1 : right1;
        if (left+right > pair[1])
            pair[1] = left+right;
    }

    public int longestUnivaluePath(TreeNode root) {
        // pair[0] is max path ends with the node, pair[1] is the max path in its sub-tree
        int[] pair = new int[2];
        helper(root, pair);
        return pair[1];
    }
}


/** Q-693 Binary Number with Alternating Bits */
class BinaryNumberWithAlternatingBits {
    public boolean hasAlternatingBits(int n) {
        int b = n & 1, n1 = n;
        while ((n1 & 1) == b) {
            b = b ^ 1;
            n1 = n1 >> 1;
        }
        return n1 == 0;
    }
}

/** Q-696 Count Binary Substrings */
class CountBinarySubstrings {
    public static int countBinarySubstrings(String s) {
        int result = 0;
        if (s.length() == 0)
            return result;
        char[] sc = s.toCharArray();
        int[] counts = new int[2];
        counts[sc[0]-'0'] = 1;
        for (int i = 1; i < s.length(); ++i) {
            int n = sc[i] - '0';
            if (sc[i] == sc[i-1])
                counts[n]++;
            else
                counts[n] = 1;
            if (counts[n] <= counts[(n+1)%2])
                result++;
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-696 Count Binary Substrings");
        System.out.println(countBinarySubstrings("00110011")); // 6
    }
}

/** Q-697 Degree of an Array */
class DegreeOfAnArray {
    public static int findShortestSubArray(int[] nums) {
        int degree = 0;
        Map<Integer, List<Integer>> map = new HashMap<>(); // [count, begin, end]
        for (int i = 0; i < nums.length; ++i) {
            List<Integer> entry;
            if (map.containsKey(nums[i])) {
                entry = map.get(nums[i]);
                entry.set(0, entry.get(0)+1);
                entry.set(2, i);
            } else {
                entry = new ArrayList<Integer>();
                entry.add(1);
                entry.add(i);
                entry.add(i);
                map.put(nums[i], entry);
            }
            if (entry.get(0) > degree)
                degree = entry.get(0);
        }
        int result = nums.length + 1;
        for (Map.Entry<Integer, List<Integer>> e : map.entrySet()) {
            List<Integer> v = e.getValue();
            if (v.get(0) == degree) {
                if (v.get(2)-v.get(1)+1 < result)
                    result = v.get(2)-v.get(1)+1;
            }
        }
        return result;
    }

    public static void test() {
        System.out.println("Q-697 Degree of an Array");
        System.out.println(findShortestSubArray(new int[]{1,2,2,3,1,4,2})); // 6
    }
}

/**
 * Q-700 Search in a Binary Search Tree
 */
class SearchInBinarySearchTree {
    public TreeNode searchBST(TreeNode root, int val) {
        for (TreeNode n = root; n != null; ) {
            if (n.val == val) return n;
            if (n.val > val) n = n.left;
            else n = n.right;
        }
        return null;
    }
}

/**
 * Q-701 Insert into a Binary Search Tree
 */
class InsertIntoBinarySearchTree {
    public TreeNode insertIntoBST(TreeNode root, int val) {
        TreeNode par = null;
        for (TreeNode n = root; n != null; ) {
            par = n;
            if (n.val > val) n = n.left;
            else n = n.right;
        }
        TreeNode n = new TreeNode(val);
        if (par == null) return n;
        if (par.val > val) par.left = n;
        else par.right = n;
        return root;
    }
}

/** Q-724 Find Pivot Index */
class FindPivotIndex {
    public int pivotIndex(int[] nums) {
        int sum = 0;
        for (int n : nums) sum += n;
        for (int i = 0, prefixSum = 0; i < nums.length; ++i) {
            if (sum - prefixSum - nums[i] == prefixSum)
                return i;
            prefixSum += nums[i];
        }
        return -1;
    }
}

/** Q-733 Flood Fill */
class FloodFill {
    public int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
        if (image[sr][sc] == newColor)
            return image;
        int initColor = image[sr][sc];
        int nrow = image.length, ncol = image[0].length;
        Queue<Integer[]> queue = new ArrayDeque<>();
        queue.add(new Integer[]{sr, sc});
        while (!queue.isEmpty()) {
            Integer[] crd = queue.poll();
            int r = crd[0], c = crd[1];
            image[r][c] = newColor;
            if (r - 1 >= 0 && image[r - 1][c] == initColor) queue.add(new Integer[]{r - 1, c});
            if (c + 1 < ncol && image[r][c + 1] == initColor) queue.add(new Integer[]{r, c + 1});
            if (r + 1 < nrow && image[r + 1][c] == initColor) queue.add(new Integer[]{r + 1, c});
            if (c - 1 >= 0 && image[r][c - 1] == initColor) queue.add(new Integer[]{r, c - 1});
        }
        return image;
    }
}

/** Q-744 Find Smallest Letter Greater Than Target */
class FindSmallestLetterGreaterThanTarget {
    public static char nextGreatestLetter(char[] letters, char target) {
        int l = 0, r = letters.length-1;
        while (l < r) {
            int m = l + (r-l)/2 + 1;
            if (letters[m] > target)
                r = m-1;
            else
                l = m;
        }
        if (l == 0 && letters[l] > target)
            return letters[0];
        return l < letters.length-1 ? letters[l+1] : letters[0];
    }

    public static void test() {
        System.out.println("Q-744 Find Smallest Letter Greater Than Target");
        System.out.println(nextGreatestLetter(new char[]{'c','f','j'}, 'b')); // c
        System.out.println(nextGreatestLetter(new char[]{'c','f','j'}, 'c')); // f
        System.out.println(nextGreatestLetter(new char[]{'c','f','j'}, 'd')); // f
        System.out.println(nextGreatestLetter(new char[]{'c','f','j'}, 'g')); // j
        System.out.println(nextGreatestLetter(new char[]{'a','b','b','b','c','e'}, 'b')); // c
        System.out.println(nextGreatestLetter(new char[]{'a','b','b','b','c','e'}, 'd')); // e
        System.out.println(nextGreatestLetter(new char[]{'a','b','b','b','c','e'}, 'f')); // a

    }
}

/** Q-747. Largest Number At Least Twice of Others */
class LargestNumberAtLeastTwiceOfOthers {
    public int dominantIndex(int[] nums) {
        if (nums.length == 0)
            return -1;
        if (nums.length == 1)
            return 0;
        int m1 = nums[0] > nums[1] ? 0 : 1, m2 = nums[0] > nums[1] ? 1 : 0;
        for (int i = 2; i < nums.length; ++i) {
            if (nums[i] <= nums[m2]) continue;
            m2 = i;
            if (nums[i] > nums[m1]) {
                m2 = m1;
                m1 = i;
            }
        }
        return nums[m1]-nums[m2] >= nums[m2] ? m1 : -1;
    }
}

/**
 * Q-751 IP to CIDR
 *
 * Given a start IP address ip and a number of ips we need to cover n, return a representation of the range as a list
 * (of smallest possible length) of CIDR blocks. A CIDR block is a string consisting of an IP, followed by a slash,
 * and then the prefix length. For example: "123.45.67.89/20". That prefix length "20" represents the number of common
 * prefix bits in the specified range.
 */
class IPToCIDR {
    public List<String> ipToCIDR(String ip, int n) {
        return null;
    }
}

/**
 * Q-766 Toeplitz Matrix
 *
 * A matrix is Toeplitz if every diagonal from top-left to bottom-right has the same element. Now given an M x N
 * matrix, return True if and only if the matrix is Toeplitz.
 */
class ToeplitzMatrix {
    public boolean isToeplitzMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length-1; ++i)
            for (int j = 0; j < matrix[0].length-1; ++j)
                if (matrix[i][j] != matrix[i+1][j+1])
                    return false;
        return true;
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

/**
 * Q-811 Subdomain Visit Count
 */
class SubdomainVisitCount {
    public List<String> subdomainVisits(String[] cpdomains) {
        Map<String, Integer> domCounts = new HashMap<>();
        for (String dom : cpdomains) {
            String[] parts = dom.split(" ");
            int cnt = Integer.valueOf(parts[0]);
            domCounts.put(parts[1], domCounts.containsKey(parts[1]) ? domCounts.get(parts[1])+cnt : cnt);
            for (int i = 0; i < parts[1].length(); ++i) {
                if (parts[1].charAt(i) == '.') {
                    String sdom = parts[1].substring(i+1);
                    int visits = domCounts.containsKey(sdom) ? domCounts.get(sdom)+cnt : cnt;
                    domCounts.put(sdom, visits);
                }
            }
        }
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> e : domCounts.entrySet()) {
            result.add(Integer.toString(e.getValue().intValue()) + " " + e.getKey());
        }
        return result;
    }
}

/** Q-824 Goat Latin */
class GoatLatin {
    public static String toGoatLatin(String S) {
        Character[] vc = {'a','e','i','o','u','A','E','I','O','U'};
        Set<Character> vowels = new HashSet<>(Arrays.asList(vc));
        String[] words = S.split(" ");
        StringBuilder result = new StringBuilder("");
        StringBuilder as = new StringBuilder("a");
        for (String w : words) {
            if (vowels.contains(w.charAt(0)))
                result.append(w).append("ma");
            else
                result.append(w.substring(1)).append(w.charAt(0)).append("ma");
            result.append(as).append(" ");
            as.append('a');
        }
        result.setLength(result.length()-1);
        return result.toString();
    }

    public static void test() {
        System.out.println("Q-824 Goat Latin");
        System.out.println(toGoatLatin("I speak Goat Latin"));
    }
}

/** Q-849 Maximize Distance to Closest Person */
class MaximizeDistanceToClosestPerson {
    public int maxDistToClosest(int[] seats) {
        int result = 0, start = 0;
        for (int i = 0; i < seats.length; ++i) {
            if (seats[i] == 1) {
                if (start == 0)
                    result = i - start;
                else if ((i - start + 1)/2 > result)
                    result = (i - start + 1)/2;
                start = i+1; // 1 2 3 4 5 6
            }
        }
        if (seats.length - start > result)
            result = seats.length - start;
        return result;
    }
}

/**
 * Q-852 Peak Index in a Mountain Array
 */
class PeakIndexInMountainArray {
    public static int peakIndexInMountainArray(int[] A) {
        int l = 0, r = A.length-1;
        while (l < r) { // A.length >= 3
            int m = l + (r-l)/2;
            if (A[m] < A[m+1])
                l = m+1;
            else
                r = m;
        }
        return l;
    }

    public static void test() {
        System.out.println(peakIndexInMountainArray(new int[]{0,1,3,4,5,6,3,2}));
    }
}

/** Q-953 Verifying an Alien Dictionary */
class VerifyingAnAlienDictionary {
    public static boolean isAlienSorted(String[] words, String order) {
        Map<Character, Integer> charOrder = new HashMap<>();
        for (int i = 0; i < order.length(); ++i)
            charOrder.put(order.charAt(i), i);
        for (int i = 0; i < words.length-1; ++i) {
            String w1 = words[i], w2 = words[i+1];
            boolean commonPrefix = true;
            for (int j = 0; j < w1.length() && j < w2.length(); ++j) {
                if (w1.charAt(j) != w2.charAt(j)) {
                    commonPrefix = false;
                    if (charOrder.get(w1.charAt(j)) > charOrder.get(w2.charAt(j)))
                        return false;
                    break;
                }
            }
            // "app" < "apple"
            if (commonPrefix && w2.length() < w1.length())
                return false;
        }
        return true;
    }

    public static void test() {
        System.out.println("Q-953 Verifying an Alien Dictionary");
        System.out.println(isAlienSorted(new String[]{"hello","leetcode"}, "hlabcdefgijkmnopqrstuvwxyz")); // true
        System.out.println(isAlienSorted(new String[]{"word","world","row"}, "worldabcefghijkmnpqstuvxyz")); // false
        System.out.println(isAlienSorted(new String[]{"apple","app"}, "abcdefghijklmnopqrstuvwxyz")); // false
        System.out.println(isAlienSorted(new String[]{"kuvp","q"}, "ngxlkthsjuoqcpavbfdermiywz")); // true
    }
}

/** Q-1119 Remove Vowels from a String */
class RemoveVowelsFromString {
    public static String removeVowels(String S) {
        Character[] v = {'a','e','i','o','u','A','E','I','O','U'};
        Set<Character> vowels = new HashSet<>();
        vowels.addAll(Arrays.asList(v));
        StringBuilder result = new StringBuilder("");
        for (char c : S.toCharArray())
            if (!vowels.contains(Character.valueOf(c))) result.append(c);
        return result.toString();
    }

    public static void test() {
        System.out.println("Q-1119 Remove Vowels from a String");
        System.out.println(removeVowels("leetcodeisacommunityforcoders")); // "ltcdscmmntyfrcdrs"
        System.out.println(removeVowels("aeiou"));
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
        System.out.println("Leetcode Easy Level and Tree");
        OneEditDistance.test();
        KthSmallestElements.run();
        CountUnivalueSubtrees.test();
        MoveZeroes.test();
        HouseRobber3.run();
        AddStrings.test();
        ArrangeCoins.test();
        SerializeAndDeserializeBST.test();
        FindAllNumbersDisappearedInArray.test();
        RepeatedSubstringPattern.test();
        NumberComplement.test();
        MostFrequentSubtreeSum.test();
        BoundaryOfBinaryTree.test();
        ReverseWordsInString.test();
        FindDuplicateSubtree.test();
        LetterCasePermutation.test();
        PeakIndexInMountainArray.test();
        ShortestUnsortedContinuousSubarray.test();
        LongestHarmoniousSubsequence.test();
        SetMismatch.test();
        RepeatedStringMatch.test();
        CountBinarySubstrings.test();
        DegreeOfAnArray.test();
        FindSmallestLetterGreaterThanTarget.test();
        VerifyingAnAlienDictionary.test();
    }
}