#
# All questions on Tree, List
#

class TreeNode(object):
    def __init__(self, x):
        self.val = x
        self.left = None
        self.right = None

class ListNode(object):
    def __init__(self, x):
        self.val = x
        self.next = None

# Q-236: LCA in binary tree
class BinaryTreeLCA(object):
    def findLCA(self, root, p, q):
        if root is None:
            return (None, False, False)

        isP = (root is p) # reference equality
        isQ = (root is q)

        left = self.findLCA(root.left, p, q)
        if (left[0] is not None):
            return (left[0], True, True)
        right = self.findLCA(root.right, p, q)
        if (right[0] is not None):
            return (right[0], True, True)

        foundP = left[1] or right[1] or isP
        foundQ = left[2] or right[2] or isQ
        if foundP and foundQ:
            return (root, True, True)
        return (None, foundP, foundQ)

    def lowestCommonAncestor(self, root, p, q):
        result = self.findLCA(root, p, q)
        return result[0]

    @staticmethod
    def test():
        print "LCA"
        lca = BinaryTreeLCA()
        p = TreeNode(1)
        q = TreeNode(2)
        p.left = q
        print lca.lowestCommonAncestor(p,p,q)

BinaryTreeLCA.test()

# Q-257 Binary Tree Path
class BinaryTreePath(object):
    def traverse(self, root, path, result):
        if root is None:
            return
        p = path + "->" + str(root.val)
        if root.left is None and root.right is None:
            result.append(p)
            return
        self.traverse(root.left, p, result)
        self.traverse(root.right, p, result)

    def binaryTreePaths(self, root):
        """
        :type root: TreeNode
        :rtype: List[str]
        """
        result = []
        if root is None:
            return result
        p = str(root.val)
        if root.left is None and root.right is None:
            result.append(p)
        self.traverse(root.left, p, result)
        self.traverse(root.right, p, result)
        return result

    @staticmethod
    def test():
        print "Q-257 Binary Tree Path"
        btp = BinaryTreePath()
        n1 = TreeNode(1)
        print btp.binaryTreePaths(n1)
        n2 = TreeNode(2)
        n3 = TreeNode(3)
        n4 = TreeNode(4)
        n1.left = n2
        n1.right = n3
        n2.right = n4
        print btp.binaryTreePaths(n1)

# Q-328 Odd Even List
class OddEvenList(object):
    def oddEvenList(self, head):
        """
        :type head: ListNode
        :rtype: ListNode
        """
        oddHead, oddTail, evenHead, evenTail = None, None, None, None
        idx, n = 1, head
        while n is not None:
            if idx % 2 != 0:
                if oddTail is None:
                    oddHead, oddTail = n, n
                else:
                    oddTail.next = n
                    oddTail = n
            else:
                if evenTail is None:
                    evenHead, evenTail = n, n
                else:
                    evenTail.next = n
                    evenTail = n
            n = n.next
            idx += 1

        if evenHead is not None:
            oddTail.next = evenHead
            evenTail.next = None
        n = oddHead
        while n is not None:
            print n.val, ","
            n = n.next
        return oddHead

# Q-387 First Unique Character in String
#
# Given a string, find the first non-repeating character in it and return it's index. If it doesn't exist, 
# return -1. You may assume the string contain only lowercase letters.
class FirstUniqueCharInString(object):
    def firstUniqChar(self, s):
        """
        :type s: str
        :rtype: int
        """
        if len(s) == 0:
            return -1
        memo = [len(s) for i in xrange(26)]
        for i in xrange(len(s)):
            pos = ord(s[i]) - ord('a')
            if memo[pos] == len(s):
                memo[pos] = i
            else:
                memo[pos] = -1
        smallest = len(s)
        for i in xrange(26):
            if memo[i] >= 0 and memo[i] < len(s) and memo[i] < smallest:
                smallest = memo[i]
        return -1 if smallest == len(s) else smallest

# Q-392 Is Subsequence (2 pointers)
#
# Given a string s and a string t, check if s is subsequence of t. You may assume that there is only lower 
# case English letters in both s and t. t is potentially a very long (length ~= 500,000) string, and s is a 
# short string (<=100). 
class IsSubsequence():
    def isSubsequence(self, s, t):
        tidx = 0
        for i in xrange(len(s)):
            match = False
            for j in xrange(tidx, len(t)):
                if t[j] == s[i]:
                    match = True
                    tidx = j+1
                    break
                tidx = j+1
            if tidx >= len(t) and not match:                
                return False
        return True

    @staticmethod
    def test():
        print "Is Subsequence"
        sub = IsSubsequence()
        print sub.isSubsequence("b", "c")
        print sub.isSubsequence("ace", "abcde")
        print sub.isSubsequence("axc", "ahbgdc")

IsSubsequence.test()

# Q-437 Path Sum III
# 
# You are given a binary tree in which each node contains an integer value. Find the number 
# of paths that sum to a given value. The path does not need to start or end at the root or 
# a leaf, but it must go downwards (traveling only from parent nodes to child nodes).
class PathSum:
    def path(self, root, sum, prefixSum):
        if root is None:
            return
        prefixSum.append(prefixSum[-1] + root.val)
        for i in xrange(1, len(prefixSum)):
            if prefixSum[-1] - prefixSum[i-1] == sum:
                self.count += 1
        self.path(root.left, sum, prefixSum)
        self.path(root.right, sum, prefixSum)
        prefixSum.pop()

    def pathSum(self, root, sum):
        prefix = [0]
        self.count = 0
        self.path(root, sum, prefix)
        return self.count

# Q-445 Add Two Numbers II
class AddTwoNumbers(object):
    def reverse(self, l):
        head, tail, p = None, None, l
        while p is not None:
            next = p.next
            if head is None:
                p.next = None
                head, tail = p, p
            else:
                p.next = head
                head = p
            p = next
        return head


    def addTwoNumbers(self, l1, l2):
        """
        :type l1: ListNode
        :type l2: ListNode
        :rtype: ListNode
        """
        p1, p2 = self.reverse(l1), self.reverse(l2)
        head, carry = None, 0
        while p1 is not None and p2 is not None:
            p12 = p1.val + p2.val + carry
            n, carry = ListNode(p12 % 10), p12 / 10
            n.next = head
            head = n
            p1, p2 = p1.next, p2.next
        p3 = p1 if p1 is not None else p2
        while p3 is not None:
            s = p3.val + carry
            n, carry = ListNode(s % 10), s / 10
            n.next = head
            head, p3 = n, p3.next
        if carry > 0:
            n = ListNode(carry)
            n.next = head
            head = n
        return head

    @staticmethod
    def test():
        l1,l2,l3,l4,l5,l6,l7 = ListNode(7), ListNode(2), ListNode(4), ListNode(3), ListNode(5), ListNode(6), ListNode(4)
        l1.next = l2
        l2.next = l3
        l3.next = l4
        l5.next = l6
        l6.next = l7
        atn = AddTwoNumbers()
        atn.addTwoNumbers(l1, l5)

# Q Diameter of Binary Tree
class DiameterOfBinaryTree(object):
    def depth(self, root):
        if root.left is None and root.right is None:
            return 0
        left_len = (1 + self.depth(root.left)) if root.left is not None else 0
        right_len = (1 + self.depth(root.right)) if root.right is not None else 0
        if left_len + right_len > self.max_path:
            self.max_path = left_len + right_len
        return left_len if left_len > right_len else right_len

    def diameterOfBinaryTree(self, root):
        self.max_path = 0
        if root is not None:
            self.depth(root)
        return self.max_path

    @staticmethod
    def test():
        print "Diameter of Binary Tree"
        dia = DiameterOfBinaryTree()
        n0 = TreeNode(0)
        n1 = TreeNode(1)
        n2 = TreeNode(2)
        n0.left = n1
        n0.right = n2
        n3 = TreeNode(3)
        n4 = TreeNode(4)
        n1.left = n3
        n1.right = n4
        n5 = TreeNode(5)
        n2.left = n5        
        print dia.diameterOfBinaryTree(n0)
        n10 = TreeNode(0)
        n11 = TreeNode(1)
        n10.left = n11
        n12 = TreeNode(2)
        n11.left = n12
        n13 = TreeNode(3)
        n14 = TreeNode(4)
        n12.left = n13
        n12.right = n14
        print dia.diameterOfBinaryTree(n10)

DiameterOfBinaryTree.test()

# Q: Count Complete Binary Tree Nodes
class CountCompleteBinaryTreeNodes(object):
    # it does not pass large testcase because it iterates all tree nodes
    def countCompleteTreeNodes_nonoptimal(self, root):
        """
        :type root: TreeNode
        :rtype: int
        """
        if root is None:
            return 0

        # find the depth of the tree
        tree_depth = 0
        n = root
        while n is not None:
            tree_depth += 1
            n = n.left

        # find the right most leaf node whose heap index is the count
        count = 0
        stack = [(root, 1, 0)]
        while len(stack) > 0:
            e = stack.pop()
            d = e[1]
            idx = e[2]
            if d == tree_depth:
                count = idx + 1
                break
            n = e[0]
            if (n.left is not None):
                stack.append((n.left, d+1, 2*idx+1))
            if (n.right is not None):
                stack.append((n.right, d+1, 2*idx+2))
        return count

    def countCompleteTreeNodes(self, root):
        """
        :type root: TreeNode
        :rtype: int
        """
        if root is None:
            return 0

        left_depth = 0
        n = root
        while n is not None:
            left_depth += 1
            n = n.left

        right_depth = 0
        n = root
        while n is not None:
            right_depth += 1
            n = n.right

        # optimization - both heights are same so it is complete tree
        if left_depth == right_depth:
            return 2**left_depth - 1

        return self.countCompleteTreeNodes(root.left) + self.countCompleteTreeNodes(root.right) + 1

    @staticmethod
    def test():
        print "Count Complete Tree Nodes"
        n0 = TreeNode(0)
        n1 = TreeNode(1)
        n2 = TreeNode(2)
        n3 = TreeNode(3)
        n0.left = n1
        n0.right = n2
        n1.left = n3
        cc = CountCompleteBinaryTreeNodes()
        print cc.countCompleteTreeNodes(n0)

CountCompleteBinaryTreeNodes.test()

# Q-521 Longest Uncommon Subsequence I & II
#
# Crap question that is hard to understand
class LongestUncommonSubsequence(object):
    def findLUSlength1(self, a, b):
        """
        :type a: str
        :type b: str
        :rtype: int
        """
        return -1 if a == b else max(len(a), len(b))

    def isSubsequence(self, s, t):
        tidx = 0
        for i in xrange(len(s)):
            match = False
            for j in xrange(tidx, len(t)):
                if t[j] == s[i]:
                    match = True
                    tidx = j+1
                    break
                tidx = j+1
            if tidx >= len(t) and not match:                
                return False
        return True

    def findLUSlength(self, strs):
        """
        :type strs: List[str]
        :rtype: int
        """
        counts = {}
        for s in strs:
            counts[s] = 1 if s not in counts else counts[s]+1
        sorted_strs = sorted(strs, key=lambda s:len(s))
        for i in xrange(len(sorted_strs)-1, -1, -1):
            s = sorted_strs[i]
            if counts[s] > 1:
                continue
            for j in xrange(len(sorted_strs)-1, -1, -1):
                if len(sorted_strs[j]) <= len(s):
                    return len(s)
                elif self.isSubsequence(s, sorted_strs[j]):
                    break
        return -1

    @staticmethod
    def test():
        print "Longest Uncommon Subsequence"
        lus = LongestUncommonSubsequence()
        print lus.findLUSlength1("aaa", "aaa") # -1
        print lus.findLUSlength1("aefawfawfawfaw", "aefawfeawfwafwaef") # 17
        print lus.findLUSlength(["aba","cdc", "eae"]) # 3
        print lus.findLUSlength(["aabbcc","aabbcc","b","bc"]) # -1
        print lus.findLUSlength(["aabbcc", "aabbcc","bc","bcc","aabbccc"]) # 7

LongestUncommonSubsequence.test()

# Q-524 Longest Word in Dictionary through Deleting
#
# Given a string and a string dictionary, find the longest string in the dictionary that can be formed by 
# deleting some characters of the given string. If there are more than one possible results, return the 
# longest word with the smallest lexicographical order. If there is no possible result, return the empty 
# string.
class LongestWordInDictionaryThruDeleting(object):
    def isSubsequence(self, s, t):
        tidx = 0
        for i in xrange(len(s)):
            match = False
            for j in xrange(tidx, len(t)):
                if t[j] == s[i]:
                    match = True
                    tidx = j+1
                    break
                tidx = j+1
            if tidx >= len(t) and not match:                
                return False
        return True

    def findLongestWord(self, s, d):
        max_len = 0
        max_words = [""]
        for t in d:
            if self.isSubsequence(t, s):
                if len(t) > max_len:
                    max_len = len(t)
                    max_words = [t]
                elif len(t) == max_len:
                    max_words.append(t)
        max_words = sorted(max_words)
        return max_words[0]

    @staticmethod
    def test():
        print "Longest Word in Dictionary Thru Deleting"
        lwdtd = LongestWordInDictionaryThruDeleting()
        print lwdtd.findLongestWord("abpcplea", ["ale","apple","monkey","plea"])
        print lwdtd.findLongestWord("bab", ["ba","ab","a","b"])

LongestWordInDictionaryThruDeleting.test()

# Q-532 K-diffs Pairs in a Array
#
# Given an array of integers and an integer k, you need to find the number of unique k-diff pairs in the 
# array. Here a k-diff pair is defined as an integer pair (i, j), where i and j are both numbers in the 
# array and their absolute difference is k.
class KDiffsPairsInArray(object):
    def findPairs(self, nums, k):
        """
        :type nums: List[int]
        :type k: int
        :rtype: int
        """
        if k < 0: # absolute diff is positive
            return 0
        targets = {}
        for n in nums:
            targets[n] = 1 if n not in targets else targets[n]+1
        res = 0
        checked = set()
        for n in nums:
            if n in checked:
                continue
            if k == 0:
                res += 0 if targets[n] == 1 else 1
            else:
                res += (0 if n-k not in targets else 1)
                res += (0 if n+k not in targets else 1)
            checked.add(n)
        return res if k == 0 else res/2

    @staticmethod
    def test():
        print "K-diffs Unique Pairs in Array"
        kdp = KDiffsPairsInArray()
        print kdp.findPairs([3,1,4,1,5], 2) # 2 unique pairs
        print kdp.findPairs([1,3,1,5,4], 0) # 1
        print kdp.findPairs([1,1,1,2,1], 1) # 1

KDiffsPairsInArray.test()

# Q-554 Brick Wall (hashmap)
class BrickWall(object):
    def leastBricks(self, wall):
        """
        :type wall: List[List[int]]
        :rtype: int
        """
        edgesCounts = {}
        for i in xrange(len(wall)):
            edge = 0
            for j in xrange(len(wall[i])-1):
                edge += wall[i][j]
                count = 1
                if edgesCounts.has_key(edge):
                    count = edgesCounts.get(edge) + 1
                edgesCounts[edge] = count
        maxCount = 0
        for k,v in edgesCounts.items():
            if v > maxCount:
                maxCount = v
        return len(wall) - maxCount

    @staticmethod
    def test():
        print "Q-554 Brick Wall"
        bw = BrickWall()
        print bw.leastBricks([[1,2,2,1],[3,1,2],[1,3,2],[2,4],[3,1,2],[1,3,1,1]]) # 2

BrickWall.test()

# Q-583 Convert BST to Greater Tree
#
# Given a Binary Search Tree (BST), convert it to a Greater Tree such that every key of the original 
# BST is changed to the original key plus sum of all keys greater than the original key in BST.
class ConvertBstToGreater(object):
    # return subtree sum of unique tree node values
    def greaterTree(self, root, parVal):
        if root is None:
            return 0
        treeSum = root.val
        if root.right is not None:
            sameRight = True if root.val == root.right.val else False
            treeSum += self.greaterTree(root.right, parVal)
            if sameRight:
                treeSum -= root.val
        newRootVal = treeSum + parVal
        if root.left is not None:
            parentSum = newRootVal if root.val != root.left.val else newRootVal - root.val
            treeSum += self.greaterTree(root.left, parentSum)
        root.val = newRootVal
        return treeSum

    def convertBST(self, root):
        """
        :type root: TreeNode
        :rtype: TreeNode
        """
        self.greaterTree(root, 0)
        return root

    @staticmethod
    def test():
        print "Greater Tree"
        n0 = TreeNode(5) # -> 21
        n1 = TreeNode(2) # -> 23
        n2 = TreeNode(5) # -> 21
        n0.left = n1
        n0.right = n2
        n3 = TreeNode(2) # -> 23
        n1.left = n3
        n4 = TreeNode(10) # -> 10
        n2.right = n4
        n5 = TreeNode(6) # -> 16
        n4.left = n5
        n6 = TreeNode(3) # -> 26
        n3.right = n6
        ctg = ConvertBstToGreater()
        print ctg.convertBST(n0).val

ConvertBstToGreater.test()

# Q-331 Verify Preorder Serialization of a Binary Tree
class BinaryTreePreorderTraversal(object):
    def traversePreorder(self, root):
        if root is None:
            print "#,"
            return
        print root.val, ","
        self.traversePreorder(root.left)
        self.traversePreorder(root.right)

    def reconstruct(self, preorder):
        """
        :type preorder: str
        :rtype: root: TreeNode
        """
        stack = [] # [node, whether left child is set]
        serialized = preorder.split(',')
        root = None
        # skip an empty binary tree, i.e. a single "#"
        if len(serialized) == 1 and serialized[0] == "#":
            return root
        for ss in serialized:
            node = None
            if ss != "#":
                node = TreeNode(int(ss))
                # set up the root of the tree
                if root is None:
                    root = node
                    stack.append([node, False])
                    continue
            if len(stack) == 0:
                print "Error: malformed serialization"
                return None
            # link tree node (incl. None for #) with the parent node, and remove the parent
            # once it has been completed right child
            par = stack[-1][0]
            if not stack[-1][1]:
                par.left = node
                stack[-1][1] = True
            else:
                # remove linked tree node from the stack
                par.right = node
                stack.pop()
            if node is not None:
                stack.append([node, False])
        return root

    def isValidSerialization(self, preorder):
        """
        :type preorder: str
        :rtype: bool
        """
        stack = [] # [node, whether left child is set]
        serialized = preorder.split(',')
        # skip an empty binary tree, i.e. a single "#"
        if len(serialized) == 1 and serialized[0] == "#":
            return True
        root = None
        for ss in serialized:
            node = None
            if ss != "#":
                node = int(ss)
                # set up the root of the tree
                if root is None:
                    root = node
                    stack.append([node, False])
                    continue
            if len(stack) == 0:
                return False
            # link tree node (incl. None for #) with the parent node, and remove the parent
            # once it has been completed right child
            par = stack[-1][0]
            if not stack[-1][1]:
                stack[-1][1] = True
            else:
                # remove parent since its right child is linked
                stack.pop()
            if node is not None:
                stack.append([node, False])
        return True if len(stack) == 0 else False

    @staticmethod
    def test():
        print "Binary Tree Preorder Traversal"
        bpt = BinaryTreePreorderTraversal()        
        root = bpt.reconstruct("9,3,4,#,#,1,#,#,2,#,6,#,#")
        print bpt.isValidSerialization("#") # True for empty tree
        print bpt.isValidSerialization("#,#") # False
        print bpt.isValidSerialization("9,3,4,#,#,1,#,#,2,#,6,#,#") # True
        print bpt.isValidSerialization("9,3,4,#,5,1,#,#,2,#") # False
        print bpt.isValidSerialization("9,#,2,#,#,3") # False

BinaryTreePreorderTraversal.test()

# Q-404 Sum of Left Leaves
class SumLeftLeaves:
    def sumLeftLeaves(self, root, leftChild):
        if root == None:
            return 0
        if root.left == None and root.right == None and leftChild:
            return root.val
        sum = self.sumLeftLeaves(root.left, True)
        sum += self.sumLeftLeaves(root.right, False)
        return sum

    def sumOfLeftLeaves(self, root):
        return self.sumLeftLeaves(root, False)

# Q-414 Third Maximum Number
#
# Given a non-empty array of integers, return the third maximum number in this array. If it does 
# not exist, return the maximum number. The time complexity must be in O(n).
class ThirdMaximumNumber(object):
    def thirdMax(self, nums):
        """
        :type nums: List[int]
        :rtype: int
        """
        import sys
        if (len(nums) == 0):
            return 0
        M = [-sys.maxsize for i in xrange(3)]
        for i in xrange(len(nums)):
            if nums[i] >= M[0]:
                if nums[i] != M[0]:
                    M[2] = M[1]
                    M[1] = M[0]
                    M[0] = nums[i]
            elif nums[i] >= M[1]:
                if nums[i] != M[1]:
                    M[2] = M[1]
                    M[1] = nums[i]            
            elif nums[i] >= M[2]:
                M[2] = nums[i]
            print M
        return M[2] if M[2] > -(sys.maxsize) else M[0]

    @staticmethod
    def test():
        print "Q-414 Third Maximum Number"
        tmn = ThirdMaximumNumber()
        print tmn.thirdMax([2,2,3,1]) # 1
        print tmn.thirdMax([2,2,2,2]) # 2
        print tmn.thirdMax([1,2,2,4,3]) # 2

# Q-454 4 Sum II
#
# Given four lists A, B, C, D of integer values, compute how many tuples (i, j, k, l) there are such that 
# A[i] + B[j] + C[k] + D[l] is zero.
class FourSum2(object):
    def fourSumCount(self, A, B, C, D):
        """
        :type A: List[int]
        :type B: List[int]
        :type C: List[int]
        :type D: List[int]
        :rtype: int
        """
        if (len(A) == 0):
            return 0
        N, result = len(A), 0
        AB = {}
        for i in xrange(N): # O(N^2)
            for j in xrange(N):
                ab = A[i] + B[j]
                count = 1
                if AB.has_key(ab):
                    count = AB[ab] + 1
                AB[ab] = count
        for i in xrange(N): # O(N^2)
            for j in xrange(N):
                cd = C[i] + D[j]
                if 0-cd in AB:
                    result += AB[0-cd]
        return result

    @staticmethod
    def test():
        print "Q-454 4Sum II"
        fs = FourSum2()
        print fs.fourSumCount([1,2],[-2,-1],[-1,2],[0,2]) # 2
        print fs.fourSumCount([-1,-1],[-1,1],[-1,1],[1,-1]) # 6

FourSum2.test()

# Q-455 Assign Cookies
#
# Assume you are an awesome parent and want to give your children some cookies. But, you should give 
# each child at most one cookie. Each child i has a greed factor gi, which is the minimum size of a 
# cookie that the child will be content with; and each cookie j has a size s[j]. If sj >= gi, we can 
# assign the cookie j to the child i, and the child i will be content. Your goal is to maximize the 
# number of your content children and output the maximum number. Note: You may assume the greed factor 
# is always positive. You cannot assign more than one cookie to one child.
class AssignCookies(object):
    def findContentChildren(self, g, s):
        """
        :type g: List[int]
        :type s: List[int]
        :rtype: int
        """
        g.sort()
        s.sort()
        # greedy
        result, sidx = 0, 0
        for i in xrange(len(g)): # children greedy factors
            for j in xrange(sidx, len(s)): # pick the smallest cookie
                if s[j] >= g[i]:
                    result += 1
                    sidx = j+1
                    break
        return result

# Q-470 Implement Rand10 Using Rand7 (rejection sampling)
#
# Given a function rand7 which generates a uniform random integer in the range 1 to 7, write a function 
# rand10 which generates a uniform random integer in the range 1 to 10.
class Rand10UsingRand7(object):
    def rand10(self):
        """
        :rtype: int
        """
        n = 0
        while True:
            r = rand7()
            c = rand7()
            n = (r-1)*7 + c
            if n <= 40:
                break
        return 1 + n % 10

# Q-513: Find Bottom Left Tree Value
class FindBottomLeftTreeValue:
    def findBottomLeftValue(self, root):
        import Queue
        if root == None:
            return None

        queue = Queue.Queue()
        queue.put((root, 0))

        leftmost = (root, 0)
        while not queue.empty():
            e = queue.get()
            if e[1] != leftmost[1]:
                leftmost = e
            n = e[0]
            if not (n.left is None):
                queue.put((n.left, e[1]+1))
            if not (n.right is None):
                queue.put((n.right, e[1]+1))
        return leftmost[0].val

    @staticmethod
    def test():
        print "Bottom Left Tree Value"
        fblt = FindBottomLeftTreeValue()
        n0 = TreeNode(0)
        n1 = TreeNode(1)
        n2 = TreeNode(2)
        n0.left = n1
        n0.right = n2
        n3 = TreeNode(3)
        n1.left = n3
        n4 = TreeNode(4)
        n5 = TreeNode(5)
        n2.left = n4
        n2.right = n4
        n6 = TreeNode(6)
        n4.left = n6
        print fblt.findBottomLeftValue(n0)

FindBottomLeftTreeValue.test()


# Q-530 Minimum Absolute Difference in BST
# Q-783 Minimum Distance Between 2 BST Nodes
# 
# Given a binary search tree with non-negative values, find the minimum absolute difference between 
# values of any two nodes.
class MinAbsoluteDiffBST(object):
    def inorder(self, root, nodes):
        if root is None:
            return
        self.inorder(root.left, nodes)
        nodes.append(root.val)
        self.inorder(root.right, nodes)

    def getMinimumDifference(self, root):
        """
        :type root: TreeNode
        :rtype: int
        """
        import sys
        if root is None:
            return 0
        nodes = []
        self.inorder(root, nodes)
        result = sys.maxsize-1
        for i in xrange(1, len(nodes)):
            if nodes[i] - nodes[i-1] < result:
                result = nodes[i] - nodes[i-1]                
        return result

# Q-541 Reverse String II
class ReverseString2(object):
    def reverse(self, s, l, r):
        while l < r:
            t = s[l]
            s[l] = s[r]
            s[r] = t
            l += 1
            r -= 1

    def reverseStr(self, s, k):
        """
        :type s: str
        :type k: int
        :rtype: str
        """
        ls = list(s)
        i = 0
        while i + k < len(s):
            self.reverse(ls, i, i+k-1)
            i += 2*k
        if i < len(s):
            self.reverse(ls, i, len(s)-1)
        return "".join(ls)

    @staticmethod
    def test():
        rs = ReverseString2()
        print rs.reverseStr("abcdefg", 2)

# Q-572 Subtree of Another Tree
class SubtreeOfAnotherTree(object):
    def match(self, r1, r2):
        if r1 is None and r2 is None:
            return True
        if r1 is None or r2 is None:
            return False
        if r1.val != r2.val:
            return False
        return self.match(r1.left, r2.left) and self.match(r1.right, r2.right)

    def isSubtree(self, s, t):
        """
        :type s: TreeNode
        :type t: TreeNode
        :rtype: bool
        """
        if t is None:
            return True
        if s is None:
            return False
        if s.val == t.val:
            if self.match(s, t):
                return True
        return True if self.isSubtree(s.left, t) else self.isSubtree(s.right, t)

# Q-623 Add One Row to Tree
class AddOneRowToTree(object):
    def helper(self, root, v, d, currLevel):
        if root is None:
            return
        if currLevel == d - 1:
            n1 = TreeNode(v)
            n1.left = root.left
            root.left = n1
            n2 = TreeNode(v)
            n2.right = root.right        
            root.right = n2
            return
        self.helper(root.left, v, d, currLevel+1)
        self.helper(root.right, v, d, currLevel+1)

    def addOneRow(self, root, v, d):
        """
        :type root: TreeNode
        :type v: int
        :type d: int
        :rtype: TreeNode
        """
        if d == 1 or root is None:
            newRoot = TreeNode(v)
            newRoot.left = root
            return newRoot
        self.helper(root, v, d, 1)
        return root

# Q-628 Maximum Product of Three Numbers
class MaxProductThreeNumbers(object):
    def maximumProduct(self, nums):
        """
        :type nums: List[int]
        :rtype: int
        """
        if len(nums) < 3:
            return 0
        nums.sort()
        p1 = nums[0]*nums[1]*nums[-1]
        p2 = nums[-1]*nums[-2]*nums[-3]
        return p1 if p1 > p2 else p2

# Q-637 Average of Levels of Binary Tree
class AverageLevelsBinaryTree(object):
    def averageOfLevels(self, root):
        """
        :type root: TreeNode
        :rtype: List[float]
        """
        import Queue
        result = []
        if root is None:
            return result
        
        queue = Queue.Queue()
        queue.put((root, 0)) # (node, level)
        lvl, lvl_count, lvl_sum = 0, 0, 0
        while not queue.empty():
            e = queue.get()
            n = e[0]
            if e[1] != lvl: # level change
                result.append(float(lvl_sum) / float(lvl_count))
                lvl, lvl_count, lvl_sum = e[1], 1, n.val
            else:
                lvl_sum += n.val
                lvl_count += 1        
            if not (n.left is None):
                queue.put((n.left, e[1]+1))
            if not (n.right is None):
                queue.put((n.right, e[1]+1))
        # the last row
        result.append(float(lvl_sum) / float(lvl_count))
        return result


# Q-643 Maximum Average Subarray I
class MaximumAverageSubarray(object):
    def findMaxAverage(self, nums, k):
        """
        :type nums: List[int]
        :type k: int
        :rtype: float
        """
        if len(nums) < k:
            return 0
        maxsum = 0
        for i in xrange(k):
            maxsum += nums[i]
        s = maxsum
        for i in xrange(k, len(nums)):
            s = s - nums[i-k] + nums[i]
            if s > maxsum:
                maxsum = s
            print maxsum
        return float(maxsum)/float(k)

# Q-645 Set Mismatch
#
# The set S originally contains numbers from 1 to n. But unfortunately, due to the 
# data error, one of the numbers in the set got duplicated to another number in the 
# set, which results in repetition of one number and loss of another number. Given 
# an array nums representing the data status of this set after the error. Your task 
# is to firstly find the number occurs twice and then find the number that is missing. 
# Return them in the form of an array.
class SetMismatch(object):
    def findErrorNums(self, nums):
        """
        :type nums: List[int]
        :rtype: List[int]
        """
        result = [None, None]
        for i in xrange(len(nums)):
            index = abs(nums[i]) - 1
            if nums[index] < 0:
                result[0] = index+1
            else:
                nums[index] = 0 - nums[index]
        for i in xrange(len(nums)):
            if nums[i] > 0:
                result[1] = i+1
                break
        return result

    @staticmethod
    def test():
        print "Q-645 Set Mismatch"
        sm = SetMismatch()
        print sm.findErrorNums([2,1,4,5,2])

SetMismatch.test()

# Q-650 2 Keys Keyboard (math)
class TwoKeysKeyboard(object):
    def minSteps(self, n):
        """
        :type n: int
        :rtype: int
        """
        if n == 1:
            return 0
        nsteps = n
        for i in xrange(n-1, 1, -1):
            if n % i == 0:
                ns = self.minSteps(n/i) + i
                if ns < nsteps:
                    nsteps = ns
        return nsteps

    @staticmethod
    def test():
        print "2 Keys Keyboard"
        kk = TwoKeysKeyboard()
        print kk.minSteps(6) # 5 cppcp
        print kk.minSteps(10) # 7 cppppcp
        print kk.minSteps(12) # 7

TwoKeysKeyboard.test()

# Q-655 Print Binary Tree
class PrintBinaryTree(object):
    def treeDepth(self, root, level):
        if root is None:
            return level
        if root.left is None and root.right is None: 
            return level+1
        left = self.treeDepth(root.left, level+1)
        right = self.treeDepth(root.right, level+1)
        return left if left > right else right

    # pass recursive (left, right) that specify the node index range
    def fill(self, root, matrix, level, left, right):
        if root is None:
            return
        col = left + (right-left)/2
        matrix[level][col] = str(root.val)
        self.fill(root.left, matrix, level+1, left, col-1)
        self.fill(root.right, matrix, level+1, col+1, right)

    def printTree(self, root):
        """
        :type root: TreeNode
        :rtype: List[List[str]]
        """
        depth = self.treeDepth(root, 0) # nrows
        if depth == 0:
            return [[]]
        ncols = 2**depth-1
        matrix = [["" for i in xrange(ncols)] for j in xrange(depth)]
        self.fill(root, matrix, 0, 0, ncols-1)
        print matrix
        return matrix

    @staticmethod
    def test():
        print "Q-655 Print Binary Tree"
        pbt = PrintBinaryTree()
        n1,n2,n3,n4,n5,n6,n7 = TreeNode(1), TreeNode(2), TreeNode(5), TreeNode(3),TreeNode(6),TreeNode(4),TreeNode(7)
        n1.left = n2
        n1.right = n3
        n2.left = n4
        n3.left = n5
        n4.left = n6
        n5.right = n7
        pbt.printTree(n1)

# Q-657 Judge Route Circle
class JudgeRouteCircle(object):
    def judgeCircle(self, moves):
        """
        :type moves: str
        :rtype: bool
        """
        counts = [0 for i in xrange(4)]
        for i in xrange(len(moves)):
            if (moves[i] == 'U'):
                counts[0] += 1
            elif (moves[i] == 'D'):
                counts[1] += 1
            elif (moves[i] == 'L'):
                counts[2] += 1
            else:
                counts[3] += 1
        return True if counts[0] == counts[1] and counts[2] == counts[3] else False

    @staticmethod
    def test():
        jrc = JudgeRouteCircle()
        print jrc.judgeCircle("ULLURDDR")
        print jrc.judgeCircle("UULLURDDR")

# Q-659 Split Array into Consecutive Subsequences
#
# You are given an integer array sorted in ascending order (may contain duplicates), you need to split 
# them into several subsequences, where each subsequences consist of at least 3 consecutive integers. 
# Return whether you can make such a split.
class SplitArrayIntoConsecutiveSubsequences(object):
    def isPossible(self, nums):
        """
        :type nums: List[int]
        :rtype: bool
        """
        freq = {}
        for i in xrange(len(nums)):
            cnt = 1 if nums[i] not in freq else freq[nums[i]]+1
            freq[nums[i]] = cnt
        endAt = {}
        for i in xrange(len(nums)):
            n = nums[i]
            if freq[n] == 0:
                continue
            # greedy, append to an existing subsequence        
            if n in endAt and endAt[n] > 0:
                endAt[n] -= 1
                endAt[n+1] = 1 if n+1 not in endAt else endAt[n+1]+1
                freq[n] -= 1
            # check if it can start a new sequence
            elif n+1 in freq and freq[n+1] > 0 and n+2 in freq and freq[n+2] > 0:
                freq[n] -= 1
                freq[n+1] -= 1
                freq[n+2] -= 1
                endAt[n+3] = 1 if n+3 not in endAt else endAt[n+3]+1
            else:
                return False
        return True

    @staticmethod
    def test():
        print "Q-659 Split Array into Consecutive Subsequences"
        sa = SplitArrayIntoConsecutiveSubsequences()
        s0 = [1,2,3,3,4,5] # True
        print sa.isPossible(s0)
        s1 = [1,2,3,3,4,4,5,5] # True
        print sa.isPossible(s1)
        s2 = [1,2,3,4,4,5] # False
        print sa.isPossible(s2)
        s3 = [1,2,3,3,5,6,7] # False
        print sa.isPossible(s3)
        s4 =  [4,5,6,7,7,8,8,9,10,11] # True
        print sa.isPossible(s4)

SplitArrayIntoConsecutiveSubsequences.test()

# Q-662 Maximum Width of Binary Tree
#
# Given a binary tree, write a function to get the maximum width of the given tree. The width of a tree 
# is the maximum width among all levels. The binary tree has the same structure as a full binary tree, 
# but some nodes are null. The width of one level is defined as the length between the end-nodes (the 
# leftmost and right most non-null nodes in the level, where the null nodes between the end-nodes are 
# also counted into the length calculation.
class MaximumWidthOfBinaryTree(object):
    def widthOfBinaryTree(self, root):
        """
        :type root: TreeNode
        :rtype: int
        """
        import Queue
        if root is None:
            return 0
        
        queue = Queue.Queue()
        queue.put((root, 0, 0)) # (node, level, index)
        currLevel, leftmost, right, maxWidth = 0, -1, 0, 1
        while not queue.empty():
            e = queue.get()
            n = e[0]
            if e[1] != currLevel:
                if right-leftmost+1 > maxWidth:
                    maxWidth = right-leftmost+1
                currLevel = e[1]
                leftmost = -1 # reset
            if n.left is not None:
                queue.put((n.left, e[1]+1, e[2]*2+1))
                if leftmost == -1:
                    leftmost = e[2]*2+1
                right = e[2]*2+1
            if n.right is not None:
                queue.put((n.right, e[1]+1, e[2]*2+2))
                if leftmost == -1:
                    leftmost = e[2]*2+2
                right = e[2]*2+2
        return maxWidth

    @staticmethod
    def test():
        print "Q-662 Maximum Width of Binary Tree"
        n1,n2,n3,n4,n5,n6,n7 = TreeNode(1), TreeNode(3), TreeNode(2), TreeNode(5),TreeNode(4),TreeNode(9),TreeNode(8)
        n1.left = n2
        n1.right = n3
        n2.left = n4
        #n2.right = n5
        n3.right = n5
        n4.left = n6
        n5.right = n7
        mwbt = MaximumWidthOfBinaryTree()
        print mwbt.widthOfBinaryTree(n1)

MaximumWidthOfBinaryTree.test()

# Q-680 Valid Palindrome II
class ValidPalindrome2(object):
    def checkPalindrome(self, s, l, r, removed):
        while (l < r):
            if s[l] == s[r]:
                l += 1
                r -= 1
                continue
            if (removed):
                return False
            leftPalin = self.checkPalindrome(s, l+1, r, True)
            rightPalin = self.checkPalindrome(s, l, r-1, True)
            return True if leftPalin or rightPalin else False
        return True 

    def validPalindrome(self, s):
        """
        :type s: str
        :rtype: bool
        """
        return self.checkPalindrome(s, 0, len(s)-1, False)

    @staticmethod
    def test():
        print "Q-680 Valid Palindrome II"
        vp = ValidPalindrome2()
        print vp.validPalindrome("abaca") # False
        print vp.validPalindrome("abbca") # True

ValidPalindrome2.test()

# Q-713 Subarray Product Less Than K
#
# Your are given an array of positive integers nums. Count and print the number of (contiguous) 
# subarrays where the product of all the elements in the subarray is less than k.
class SubarrayProductLessThanK(object):
    def numSubarrayProductLessThanK(self, nums, k):
        """
        :type nums: List[int]
        :type k: int
        :rtype: int
        """
        result, prod = (1, nums[0]) if nums[0] < k else (0, 1)
        l = 0
        for r in xrange(1, len(nums)):
            prod *= nums[r]
            if prod >= k:
                while l <= r and prod >= k:
                    prod /= nums[l]
                    l += 1
            result += r-l+1
        return result

    @staticmethod
    def test():
        print "Q-713 Subarray Product Less Than K"
        spltk = SubarrayProductLessThanK()
        print spltk.numSubarrayProductLessThanK([10,5,2,6], 100) # 8

SubarrayProductLessThanK.test()

# Q-735 Asteriod Collision (stack)
#
# We are given an array asteroids of integers representing asteroids in a row. For each asteroid, 
# the absolute value represents its size, and the sign represents its direction (positive meaning 
# right, negative meaning left). Each asteroid moves at the same speed. Find out the state of the 
# asteroids after all collisions. If two asteroids meet, the smaller one will explode. If both 
# are the same size, both will explode. Two asteroids moving in the same direction will never meet.
class AsteroidCollision(object):
    def asteroidCollision(self, asteroids):
        """
        :type asteroids: List[int]
        :rtype: List[int]
        """
        result = []
        stack = []
        for i in xrange(len(asteroids)):
            if asteroids[i] >= 0:
                stack.append(asteroids[i])
                continue
            # left-bound asteroid
            exploded = False
            while len(stack) > 0:
                pnum = stack.pop()
                # larger right-bound asteroid wins, everything else explodes
                if abs(asteroids[i]) <= pnum:
                    exploded = True
                    if abs(asteroids[i]) < pnum:
                        stack.append(pnum)
                    break
            if (not exploded and len(stack) == 0):
                result.append(asteroids[i])
        result.extend(stack)
        return result

    @staticmethod
    def test():
        print "Q-735 Asteroid Collision"
        ac = AsteroidCollision()
        print ac.asteroidCollision([10,5,-5,-10]) # []
        print ac.asteroidCollision([-2,-1,2,1])

AsteroidCollision.test()

# Q-767 Reorganize String (max heap priority queue)
#
# Given a string S, check if the letters can be rearranged so that two characters that 
# are adjacent to each other are not the same. If possible, output any possible result.  
# If not possible, return the empty string.
class ReorganizeString(object):
    class CharCountItem:
        def __init__(self, key, count):
            self.key = key
            self.count = count

        # max heap
        def __cmp__(self, other):
            return self.count < other.count

    def reorganizeString(self, S):
        """
        :type S: str
        :rtype: str
        """
        import heapq
        result = ""
        chars = [0 for i in xrange(26)]
        for i in xrange(len(S)):
            chars[ord(S[i]) - ord('a')] += 1
        items = []
        for i in xrange(26):
            if chars[i] > 0:
                heapq.heappush(items, self.CharCountItem(chr(ord('a')+i), chars[i]))
        while len(items) >= 2:
            i1 = heapq.heappop(items)
            i2 = heapq.heappop(items)
            result += i1.key + i2.key
            i1.count -= 1
            i2.count -= 1
            if i1.count > 0:
                heapq.heappush(items, i1)
            if i2.count > 0:
                heapq.heappush(items, i2)
        if len(items) > 0:
            it = heapq.heappop(items)
            if it.count > 1:
                return ""
            result += it.key
        return result

    @staticmethod
    def test():
        print "Q-767 Reorganize String"
        rs = ReorganizeString()
        print rs.reorganizeString("aabba")
        print rs.reorganizeString("aaaabb") # empty
        print rs.reorganizeString("aaaabcb")

ReorganizeString.test()

# Q-795 Number of Subarrays With Bounded Maximum
class NumerOfSubarraysWithBoundedMaximum(object):
    def numSubarrayBoundedMax(self, A, L, R):
        """
        :type A: List[int]
        :type L: int
        :type R: int
        :rtype: int
        """
        result = 0
        for i in xrange(len(A)):
            if A[i] > R:
                continue
            inRange = False
            for j in xrange(i, len(A)):
                if A[j] > R:
                    break
                if A[j] >= L:
                    inRange = True
                if inRange:
                    result += 1
        return result

    @staticmethod
    def test():
        print "Q-795 Number of Subarrays With Bounded Maximum"
        nsbm = NumerOfSubarraysWithBoundedMaximum()
        print nsbm.numSubarrayBoundedMax([2,1,4,3], 2, 3) # 3
        print nsbm.numSubarrayBoundedMax([2,1,3,4,3], 2, 3) # 6

NumerOfSubarraysWithBoundedMaximum.test()

# Q-807 Max Increase to Keep City Skyline
#
# In a 2 dimensional array grid, each value grid[i][j] represents the height of a building located 
# there. We are allowed to increase the height of any number of buildings, by any amount (the amounts 
# can be different for different buildings). Height 0 is considered to be a building as well. At the 
# end, the "skyline" when viewed from all four directions of the grid, i.e. top, bottom, left, and 
# right, must be the same as the skyline of the original grid. A city's skyline is the outer contour 
# of the rectangles formed by all the buildings when viewed from a distance. See the following example.
# What is the maximum total sum that the height of the buildings can be increased?
class MaxIncreaseKeepingSkyline(object):
    def maxIncreaseKeepingSkyline(self, grid):
        """
        :type grid: List[List[int]]
        :rtype: int
        """
        rowMax = [0 for i in xrange(len(grid))]
        colMax = [0 for i in xrange(len(grid[0]))]
        for i in xrange(len(grid)):
            for j in xrange(len(grid[0])):
                if rowMax[i] < grid[i][j]:
                    rowMax[i] = grid[i][j]
                if colMax[j] < grid[i][j]:
                    colMax[j] = grid[i][j]
        total = 0
        for i in xrange(len(grid)):
            for j in xrange(len(grid[0])):
                h = min(rowMax[i], colMax[j])
                total += h - grid[i][j]
        return total


    @staticmethod
    def test():
        print "Q-807 Max Increase to Keep City Skyline"
        mikcl = MaxIncreaseKeepingSkyline()
        grid = [[3,0,8,4],[2,4,5,7],[9,2,6,3],[0,3,1,0]] # 35
        print mikcl.maxIncreaseKeepingSkyline(grid) 
        grid2 = [[3,0,8,4]] # 0
        grid3 = [[3],[2],[9],[0]] # 0
        print mikcl.maxIncreaseKeepingSkyline(grid2)        

MaxIncreaseKeepingSkyline.test()

# Q-814 Binary Tree Pruning
#
# We are given the head node root of a binary tree, where additionally every node's value is either 
# a 0 or a 1. Return the same tree where every subtree (of the given tree) not containing a 1 has 
# been removed.
class BinaryTreePruning(object):
    def pruneTree(self, root):
        """
        :type root: TreeNode
        :rtype: TreeNode
        """
        if root is None:
            return None
        if root.val == 0 and root.left is None and root.right is None:
            return None
        if root.left is not None:
            root.left = self.pruneTree(root.left)
        if root.right is not None:
            root.right = self.pruneTree(root.right)
        if root.val == 0 and root.left is None and root.right is None:
            return None
        return root

PrintBinaryTree.test()

# Q-821 Shortest Distance to a Character
#
# Given a string S and a character C, return an array of integers representing the shortest distance 
# from the character C in the string. C is guaranteed to be in S.
class ShortestDistanceToCharacter(object):
    def shortestToChar(self, S, C):
        """
        :type S: str
        :type C: str
        :rtype: List[int]
        """
        dist = [len(S) for i in xrange(len(S))]
        pos = -1
        for i in xrange(len(S)):
            if S[i] == C:
                pos = i
            if pos > -1:
                dist[i] = i - pos
        pos = len(S)
        for i in xrange(len(S)-1, -1, -1):
            if S[i] == C:
                pos = i
            if pos < len(S) and pos - i < dist[i]:
                dist[i] = pos - i
        return dist

    @staticmethod
    def test():
        print "Q-821 Shortest Distance to a Character"
        sdc = ShortestDistanceToCharacter()
        print sdc.shortestToChar("loveleetcode", 'e')

ShortestDistanceToCharacter.test()

# Q-841 Keys and Rooms (BFS)
class KeysAndRooms(object):
    def canVisitAllRooms(self, rooms):
        """
        :type rooms: List[List[int]]
        :rtype: bool
        """
        import Queue
        if len(rooms) == 0:
            return True
        visited = set()
        queue = Queue.Queue()
        queue.put(0)
        while not queue.empty():
            room = queue.get()
            for i in xrange(len(rooms[room])):
                if rooms[room][i] not in visited:
                    queue.put(rooms[room][i])
            visited.add(room)
        return True if len(visited) == len(rooms) else False

    @staticmethod
    def test():
        print "Q-841 Keys and Rooms"
        kr = KeysAndRooms()
        print kr.canVisitAllRooms([[1],[2],[3],[]]) # True
        print kr.canVisitAllRooms([[1,3],[3,0,1],[2],[0]]) # False

KeysAndRooms.test()

