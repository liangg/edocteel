#
# Leetcode questions 1 - 349
#

import math
import Queue

class TreeNode(object):
    def __init__(self, x):
        self.val = x
        self.left = None
        self.right = None

# Q: Contains Duplicates II
def containsNearbyDuplicate(nums, k):
    """
    :type nums: List[int]
    :type k: int
    :rtype: bool
    """
    window = set()
    p = 0
    start = 0
    while p < len(nums):
        if p - start > k:
            window.remove(nums[start])
            start += 1
        if nums[p] in window:
            return True
        window.add(nums[p])
        p += 1
    return False

# Q-152: Maximum Product Subarray
class MaximumProductSubarray(object):
    def maxProduct(self, nums):
        """
        :type nums: List[int]
        :rtype: int
        """
        if len(nums) == 0:
            return 0
        # forward scan 
        max_prod = nums[0]
        prod = 1
        for i in xrange(0, len(nums)):
            prod *= nums[i]
            if (prod > max_prod):
                max_prod = prod
            if (nums[i] == 0):
                prod = 1
        # backward scan
        prod = 1
        for i in xrange(len(nums)-1, -1, -1):
            prod *= nums[i]
            if (prod > max_prod):
                max_prod = prod
            if (nums[i] == 0):
                prod = 1
        return max_prod

    @staticmethod
    def test():
        print "Q-152 Maximum Product Subarray"
        mps = MaximumProductSubarray()
        print mps.maxProduct([3,0,4]) # 4
        print mps.maxProduct([0,-2,0]) # 0
        print mps.maxProduct([2,3,-2,4,0,-3,2,-2]) # 12
        print mps.maxProduct([2,3,-2,4,2,0,-3,2]) # 8
        print mps.maxProduct([2,1,-2,4,0,-3,2]) # 4


# Q-215 Kth Largest Element in an Array
class KthLargestElementInArray(object):
    def swap(self, nums, p1, p2):
        tmp = nums[p1]
        nums[p1] = nums[p2]
        nums[p2] = tmp

    def partition(self, nums, left, right):
        # move elements larger or equal to left side
        pivot, larger = nums[left], left
        self.swap(nums, left, right) # move pivot to the rightmost
        for i in xrange(left, right+1):
            if nums[i] > pivot:
                self.swap(nums, i, larger)
                larger += 1
        self.swap(nums, larger, right)
        return larger # return where pivot is

    def findKthLargest(self, nums, k):
        """
        :type nums: List[int]
        :type k: int
        :rtype: int
        """
        l, r = 0, len(nums)-1
        while (True):
            p = self.partition(nums, l, r)
            if p == k-1:
                return nums[p]
            if p > k-1:
                r = p-1
            else:
                l = p+1

    @staticmethod
    def test():
        print "Q-215 Kth Largest Element in Array"
        k = KthLargestElementInArray()
        print k.findKthLargest([3,2,1,5,6,4], 2) # 5
        print k.findKthLargest([3,2,1,5,4,7,6], 4) # 4

KthLargestElementInArray.test()


# Q-221: Maximal Square
#
# Given a 2D binary matrix filled with 0's and 1's, find the largest square containing
# all 1's and return its area.

class MaximalSquare(object):
    def maximal_square(self, matrix):
        """
        :type matrix: List["str"]
        :rtype: int
        """
        if len(matrix) == 0 or len(matrix[0]) == 0:
            return 0
        NR = len(matrix)
        NC = len(matrix[0])
        max_area = 0
        M = [[0 for x in range(NC)] for y in range(NR)]
        for i in xrange(NR):
            for j in xrange(NC):
                if matrix[i][j] == '1':
                    max_area = 1 # catch single cell '1'
                    if j == 0:
                        M[i][j] = 1
                    else:
                        M[i][j] = M[i][j-1] + 1

        # scan backward and upward for maximal square
        for i in xrange(NR-1, -1, -1):
            for j in xrange(NC-1, -1, -1):
                if M[i][j] > 1 and M[i][j]*M[i][j] > max_area:
                    L = M[i][j]
                    area = 1
                    for k in xrange(i-1, -1, -1):
                        if M[k][j] < L:
                            L = M[k][j]
                        if i-k+1 >= L:
                            a = L*L
                            if a <= max_area:
                                break
                            if a > area:
                                area = a

                    if area > max_area:
                        max_area = area

        return max_area

    @staticmethod
    def test():
        print "Q-221: Maximal Square"
        ms = MaximalSquare()
        print ms.maximal_square(["10"])
        print ms.maximal_square(["101","011","111"])
        print ms.maximal_square(["010101","001110","111110","011111","111011"])


MaximalSquare.test()

# Q-228 Summary Ranges
#
# Given a sorted integer array without duplicates, return the summary of its ranges.
class SummaryRanges(object):
    def summaryRanges(self, nums):
        """
        :type nums: List[int]
        :rtype: List[str]
        """
        result = []
        if len(nums) == 0:
            return result
        start, prev = nums[0], nums[0]
        for i in xrange(1, len(nums)):
            if nums[i] - prev > 1:
                summary = str(start)
                if prev > start:
                    summary += "->" + str(prev)
                result.append(summary)
                start = nums[i]
            prev = nums[i]
        # add the last range
        summary = str(start)
        if prev > start:
            summary += "->" + str(prev)
        result.append(summary)
        return result

    @staticmethod
    def test():
        print "Q-228 Summary Ranges"
        sr = SummaryRanges()
        print sr.summaryRanges([0,1,2,4,5,7])
        print sr.summaryRanges([0,2,3,4,6,8,9])

SummaryRanges.test()

# Q-229: Majority Element II
#
# Given an integer array of size n, find all elements that appear more than
# one third times. The algorithm should run in linear time and in O(1) space.
class MajorityElement2(object):
    def majorityElement(slef, nums):
        """
        :type nums: List[int]
        :rtype: List[int]
        """
        if len(nums) < 2:
            return nums
        candidates = [None] * 2
        counts = [None] * 2
        counts[0] = counts[1] = 0
        for i in xrange(0, len(nums)):
            num = nums[i]
            if counts[0] > 0 and num == candidates[0]:
                counts[0] += 1
            elif counts[1] > 0 and num == candidates[1]:
                counts[1] += 1
            elif counts[0] == 0:
                candidates[0] = num
                counts[0] = 1
            elif counts[1] == 0:
                candidates[1] = num
                counts[1] = 1
            else:
                counts[0] -= 1
                counts[1] -= 1
        # must double check if a candidate indeed appears more than N/3 times
        counts[0] = counts[1] = 0
        for i in xrange(0, len(nums)):
            if candidates[0] == nums[i]:
                counts[0] += 1
            elif candidates[1] == nums[i]:
                counts[1] += 1
        result = []
        if counts[0] > len(nums)/3:
            result.append(candidates[0])
        if counts[1] > len(nums)/3:
            result.append(candidates[1])
        return result

    @staticmethod
    def test():
        print "Majority Elements"
        me = MajorityElement2()
        print me.majorityElement([1])
        print me.majorityElement([2,2])
        print me.majorityElement([1,2,3])
        print me.majorityElement([3,2,3])
        print me.majorityElement([2,1,4,4,2,4,6,2,2,4,2,5,4])

MajorityElement2.test()


# Q-243 Shortest Word Distance $$ 
#
# Given a list of words and two words word1 and word2, return the shortest distance 
# between these two words in the list. 
#
# For example, words = [practice, makes, perfect, coding, makes].
# Given word1 = coding, word2 = practice, return 3.
# Given word1 = makes, word2 = coding, return 1.
class ShortestWordDistance(object):
    def shortestWordDistance(self, words, word1, word2):
        """
        : type words: List[string]
        : type word1: string
        : type word2: string
        : rtype: int
        """
        result, p1, p2 = len(words), -1, -1
        for i in xrange(len(words)):
            if words[i] == word1:
                p1 = i
                if p2 != -1:
                    if p1-p2 < result:
                        result = p1-p2
            elif words[i] == word2:
                p2 = i
                if p1 != -1:
                    if p2-p1 < result:
                        result = p2-p1
        return result

    @staticmethod
    def test():
        print "Q-243 Shortest Word Distance $$"
        swd = ShortestWordDistance()
        print swd.shortestWordDistance(["practice", "makes", "perfect", "coding", "makes"], "coding", "practice") # 3
        print swd.shortestWordDistance(["practice", "makes", "perfect", "coding", "makes"], "coding", "makes") # 1

# Q-268: Missing Number
#
# Given an array containing n distinct numbers taken from 0, 1, 2, ..., n, find the
# one that is missing from the array. For example, Given nums = [0, 1, 3] return 2.
def missingNumber(nums):
    """
    :type nums: List[int]
    :rtype: int
    """
    N = len(nums)
    for i in xrange(0, N):
        j = i
        while nums[j] != j:
            if nums[j] >= 0 and nums[j] < N and nums[nums[j]] != nums[j]:
                tmp = nums[nums[j]]
                nums[nums[j]] = nums[j]
                nums[j] = tmp
            else:
                nums[j] = -1
                break
        i += 1

    for i in xrange(0, N):
        if nums[i] == -1:
            return i
    return N

def test_missing_number():
    print "Missing number"
    print missingNumber([2,0,3])
    print missingNumber([9,2,1,6,8,6,12,7,0,4])
    print missingNumber([9,2,1,6,8,6,5,7,0,4])

test_missing_number()

# Q-274: H-Index
#
# Given an array of citations (each citation is a non-negative integer) of a
# researcher, write a function to compute the researcher's h-index. H_index is
# defined like this - a scientist has index h if h of his/her N papers have at
# least h citations each, and the other (N-h) papers have no more than h citations
# each. For example, given citations = [3, 0, 6, 1, 5], which means the researcher
# has 5 papers in total and each of them had received 3, 0, 6, 1, 5 citations
# respectively. Since the researcher has 3 papers with at least 3 citations each
# and the remaining two with no more than 3 citations each, his h-index is 3.
# Note: If there're several possible values for h, the max one is taken as the h-index.

def compute_h_index(citations):
    num = len(citations)
    if num == 0:
        return 0

    # in-direct sort citations, (citation, original index)
    cit2 = []
    for i in xrange(num):
        cit2.append((citations[i], i))
    sorted_cits = sorted(cit2, key=lambda pc : pc[0])

    # for each paper, compute the number of citations >= its citations
    larger = [None] * num
    for i in xrange(num):
        idx = sorted_cits[num-1-i][1]
        larger[idx] = i

    # H of N papers have at least H citations
    h_index = 0
    for i in xrange(num):
        nge = larger[i] + 1
        if (citations[i] >= nge and nge > h_index):
            h_index = nge

    return h_index

def test_h_index():
    print "Paper Citations H_index"
    c0 = [3,0,6,1,5,2]
    print compute_h_index(c0) # 3
    c1 = [3,1,6,2,5,4,4,2]
    print compute_h_index(c1) # 4
    c2 = [100,99]
    print compute_h_index(c2) # 2

test_h_index()

# Q-216: Combination Sum III
#
# Find all possible combinations of k numbers that add up to a number n, given
# that only numbers from 1 to 9 can be used and each combination should be a
# unique set of numbers.

class CombinationSum3:
    def combo_sum_recursive(self, begin, p, k, n, combo, result):
        #print begin,p,k,n,combo
        if p == k:
            if n == 0:
                result.append(list(combo)) # python pass by reference, must copy
            return
        elif n == 0:
            return

        # can only use number 1 <= x <= 9
        for i in xrange(begin, min(n, 9) + 1):
            combo[p] = i
            if n == i:
                self.combo_sum_recursive(10, p+1, k, n-i, combo, result)
                break
            self.combo_sum_recursive(i+1, p+1, k, n-i, combo, result)

    def combinationSum3(self, k, n):
        result = []
        combo = [None] * k
        self.combo_sum_recursive(1, 0, k, n, combo, result)
        return result

    @staticmethod
    def test():
        print "Combination Sum III"
        cs = CombinationSum3()
        print cs.combinationSum3(2,6)
        print cs.combinationSum3(3,9)
        print cs.combinationSum3(3,11)
        print cs.combinationSum3(4,15)
        print cs.combinationSum3(2,18) # []

CombinationSum3.test()



def palindromePrefix(s, left, right):
    """
    :type left: int, probe range begin
    :type right: int, probe range end
    :return prefix_end: int
    """
    # recursion stop condition
    if left > right or right < left:
        return -1

    # check whether the prefix ending at 'mid' is palindrome
    mid = left + (right - left)/2
    l = 0
    r = mid
    while l < r:
        if s[l] == s[r]:
            l += 1
            r -= 1
        else:
            break

    prefix_end = -1
    prefix_palindrome = l >= r
    # prefix is palindrome, check if a longer palindrome exists
    if prefix_palindrome:
        prefix_end = mid
        rv = palindromePrefix(s, mid+1, right)
        if (rv != -1):
            prefix_end = rv
    else:
        # check both sides for palindrome prefix
        rv = palindromePrefix(s, mid+1, right)
        if rv != -1:
            prefix_end = rv
        else:
            lv = palindromePrefix(s, left, mid-1)
            if lv != -1:
                prefix_end = lv
    return prefix_end

def shortestPalindrome(s):
    """
    :type s: str
    :rtype: str
    """
    prefix_end = palindromePrefix(s, 0, len(s)-1)
    suffix = s[prefix_end+1:]
    reverse = suffix[::-1]
    return reverse + s

def test_shortest_palindrome():
    print "Shortest Palindrome"
    print shortestPalindrome("aba")
    print shortestPalindrome("aacecaaa")
    print shortestPalindrome("abcd")
    print shortestPalindrome("cbcabac")

test_shortest_palindrome()

# Q-287 Find Duplicate Number
#
# Given an array nums containing n + 1 integers where each integer is between
# 1 and n (inclusive), prove that at least one duplicate number must exist.
# Assume that there is only one duplicate number, find the duplicate one.

def find_majority_duplicate_number(nums, separator, leftmost):
    # a number appearing once is not the duplicate number that we want
    if len(nums) < 2:
        return 0

    mask = leftmost - 1
    candidate = 0
    count = 0
    for num in nums:
        if (num & mask) ^ separator == mask:
            if count > 0 and num == candidate:
                count += 1
            elif count == 0:
                candidate = num
                count = 1
            else:
                count -= 1

    if count > 0:
        # must double check if a candidate indeed appears more than N/3 times
        count = 0
        for num in nums:
            if (num & mask) ^ separator == mask and candidate == num:
                count += 1

    return candidate if count > 1 else 0

def divide_and_find_duplicate(nums, separator, leftmost, depth, nelems):
    # runtime is O(32*lgN) because it checks 32 bits at most
    if leftmost == 0 or depth >= 32:
        return 0

    # try to find a majority element
    val = find_majority_duplicate_number(nums, separator, leftmost)
    if val != 0:
        return val

    # split numbers into 2 halves using the current leftmost bit
    subsets = []
    mask = leftmost - 1
    count0 = count1 = 0
    for num in nums:
        # use separator mask bits to select numbers that are supposed to be checked
        if (num & mask) ^ separator == mask:
            if nelems < 4000:
                subsets.append(num)
            if num & leftmost == 0:
                count0 += 1
            else:
                count1 += 1

    # no need to check duplicate, but the assumption is that duplicate exists
    if (count0 == 1 and count1 == 1) or (count0 + count1 == 1):
        return 0

    # the half with more numbers has the duplicate
    if count0 > count1:
        return divide_and_find_duplicate(nums, separator|leftmost, leftmost << 1, depth+1, count0)
    elif count1 > count0:
        return divide_and_find_duplicate(nums, separator, leftmost << 1, depth+1, count1)

    val = divide_and_find_duplicate(nums, separator, leftmost << 1, depth+1, count1)
    if val == 0:
        val = divide_and_find_duplicate(nums, separator|leftmost, leftmost << 1, depth+1, count0)
    return val

def find_duplicate_number(nums):
    """
    :type nums: List[int]
    :rtype: int
    """
    print nums
    return divide_and_find_duplicate(nums, 0, 1, 0, len(nums))

def test_find_duplicate_number():
    print "Find Duplicate Number"
    print find_duplicate_number([1,1,2,3,4,5])
    print find_duplicate_number([1,1,2,3,1,5])
    print find_duplicate_number([1,2,2,3,4,5])
    print find_duplicate_number([1,2,2,3,4,5,6])
    print find_duplicate_number([1,2,2,3,2,5,6,7,8])
    print find_duplicate_number([1,2,3,4,5,6,7,8,9,10,11,12,13,4,15,16])
    print find_duplicate_number([1,2,3,4,5,6,7,8,9,10,1,12,13,14,1,16,17])

test_find_duplicate_number()



class DetectCapital:
    def detectCapitalUse(self, word):
        first = True
        l, r = (0, len(word) - 1)
        upper = word[l].isupper() and word[r].isupper()
        while l <= r:
            leftU, rightU = (word[l].isupper(), word[r].isupper())
            if not ((leftU == upper or first) and (rightU == leftU or (leftU and not rightU and first))):
                return False
            l += 1
            r -= 1
            first = False
        return True

    @staticmethod
    def test():
        print "Detect Capital"
        dc = DetectCapital()
        print dc.detectCapitalUse("AIR")
        print dc.detectCapitalUse("airbnb")
        print dc.detectCapitalUse("Airbnb")
        print dc.detectCapitalUse("AIRBNb")
        print dc.detectCapitalUse("airbnB")
        print dc.detectCapitalUse("AirbnB")
        print dc.detectCapitalUse("AIRbNB")

DetectCapital.test()


# Q-318 Maximum Product of Word Lengths
class MaxProductWordLengths:
    def maxProduct(self, words):
        cbits = {}
        cbits['a'] = bit = 1
        for c in "bcdefghijklmnopqrstuvwxyz":
            bit <<= 1
            cbits[c] = bit

        word_bits = {}
        for w in words:
            wbits = 0
            for c in w:
                wbits |= cbits[c]
            word_bits[w] = wbits

        N = len(words)
        max_prod = 0
        pair = ("","")
        for i in xrange(N):
            len1 = len(words[i])
            for j in xrange(i+1, N):
                if word_bits[words[i]] & word_bits[words[j]] == 0:
                    prod = len1 * len(words[j])
                    if prod > max_prod:
                        max_prod = prod
                        pair = (words[i], words[j])

        print max_prod, pair
        return max_prod

    @staticmethod
    def test():
        print "Max Product of Word Lengths"
        mp = MaxProductWordLengths()
        print mp.maxProduct(["abcw", "baz", "foo", "bar", "xtfn", "abcdef"])
        print mp.maxProduct(["a", "ab", "abc", "d", "cd", "bcd", "abcd"])
        print mp.maxProduct(["a", "aa", "aaa", "aaaa"])

MaxProductWordLengths.test()

# Q-303 Range Sum Query
#
# Given an integer array nums, find the sum of the elements between indices
# i and j (i <= j), inclusive.
class RangeSumQuery:
    def __init__(self, nums):
        self.prefix_sums = None
        if len(nums) == 0:
            return

        self.prefix_sums = [0 for i in xrange(len(nums))]
        self.prefix_sums[0] = nums[0]
        for i in xrange(1, len(nums)):
            self.prefix_sums[i] = self.prefix_sums[i-1] + nums[i]

    def sumRange(self, i, j):
        if self.prefix_sums is None:
            return 0
        return self.prefix_sums[j] - self.prefix_sums[i-1] if i > 0 else self.prefix_sums[j]

    @staticmethod
    def test():
        print "Range Sum Query"
        r = RangeSumQuery([])
        print r.sumRange(0,0)
        rsq = RangeSumQuery([-2,0,3,-5,2,-1,4])
        print rsq.sumRange(0,2)
        print rsq.sumRange(0,5)
        print rsq.sumRange(2,5)
        print rsq.sumRange(1,4)

RangeSumQuery.test()

# Q-304 Range Sum Query (2D)
#
# Given a 2D matrix matrix, find the sum of the elements inside the rectangle defined by its upper
# left corner (row1, col1) and lower right corner (row2, col2). The matrix is given as a 2D array.
class RangeSumQuery2D:
    def __init__(self, matrix):
        self.m_sum = None
        if len(matrix) == 0:
            return

        self.m_sum = [[0 for j in xrange(len(matrix[0]))] for i in xrange(len(matrix))]
        for i in xrange(len(matrix)):
            prefix = 0
            for j in xrange(len(matrix[0])):
                s = 0 if i == 0 else self.m_sum[i-1][j]
                self.m_sum[i][j] = s + prefix + matrix[i][j]
                prefix += matrix[i][j]
        print self.m_sum

    def sumRegion(self, row1, col1, row2, col2):
        if self.m_sum is None:
            return 0
        top_sum = 0 if row1 == 0 else self.m_sum[row1-1][col2]
        left_sum = 0 if col1 == 0 else self.sumRegion(row1, 0, row2, col1-1)
        return self.m_sum[row2][col2] - top_sum - left_sum

    @staticmethod
    def test():
        print "Range Sum Query 2D"
        m = [
            [3, 0, 1, 4, 2],
            [5, 6, 3, 2, 1],
            [1, 2, 0, 1, 5],
            [4, 1, 0, 1, 7],
            [1, 0, 3, 0, 5]
        ]
        rsq = RangeSumQuery2D(m)
        print rsq.sumRegion(2, 1, 4, 3) # 8
        print rsq.sumRegion(1, 1, 2, 2) # 11
        print rsq.sumRegion(1, 2, 2, 4) # 12

RangeSumQuery2D.test()

# Q-307 Range Sum Query - Mutable
#
# Given an integer array nums, find the sum of the elements between indices i and j, inclusive.
# The update(i, val) function modifies nums by updating the element at index i to val.
class RangeSumQueryMutable(object):
    def __init__(self, nums):
        if (len(nums) == 0):
            return
        self.N = len(nums)
        height = int(math.ceil(math.log(self.N, 2))) + 1
        self.seg_tree = [0 for i in xrange(2**(height))]
        for i in xrange(self.N):
            j = self.search(i, 0, self.N-1, 0)
            self.updateSeg(i, 0, self.N-1, 0, nums[i])

    # search for position of the k-th element in the "nums" array, within the range of
    # left "l" and right "r", starting at segment tree index "seg_idx"
    def search(self, kth, l, r, seg_idx):
        if l == r:
            return seg_idx
        m = l + (r - l) / 2
        return self.search(kth, l, m, seg_idx*2+1) if kth <= m else self.search(kth, m+1, r, seg_idx*2+2)

    def prefixSum(self, kth, l, r, seg_idx):
        if l == r:
            return self.seg_tree[seg_idx]
        m = l + (r - l) / 2
        if kth > m:
            return self.seg_tree[seg_idx*2+1] + self.prefixSum(kth, m+1, r, seg_idx*2+2)
        return self.prefixSum(kth, l, m, seg_idx*2+1)

    def updateSeg(self, kth, l, r, seg_idx, val):
        if l == r:
            self.seg_tree[seg_idx] = val
            return
        m = l + (r - l) / 2
        if kth <= m:
            self.updateSeg(kth, l, m, seg_idx*2+1, val)
        else:
            self.updateSeg(kth, m+1, r, seg_idx*2+2, val)
        self.seg_tree[seg_idx] = self.seg_tree[seg_idx*2+1] + self.seg_tree[seg_idx*2+2]

    # modifies nums by updating the element at index i to val
    def update(self, i, val):
        self.updateSeg(i, 0, self.N-1, 0, val)

    # sum of the elements between indices i and j (i less than equal to j), inclusive
    def sumRange(self, i, j):
        return self.prefixSum(j, 0, self.N-1, 0) - self.prefixSum(i-1, 0, self.N-1, 0) if i > 0 \
            else self.prefixSum(j, 0, self.N-1, 0)

    @staticmethod
    def test():
        print "Range Sum Query - Mutable"
        rsq = RangeSumQueryMutable([-2,0,3,-5,2,-1,4])
        print rsq.sumRange(0,3)
        print rsq.sumRange(2,5)
        rsq.update(4,3)
        print rsq.sumRange(2,5)
        rsq.update(2,2)
        print rsq.sumRange(0,5)

        rsq2 = RangeSumQueryMutable([1,1,1,1,1,1,1,1,1,1])
        print rsq2.sumRange(3,8)
        rsq2.update(4,3)
        print rsq2.sumRange(3,8)

RangeSumQueryMutable.test()

# Q-310 : Minimum Height Trees
#
# For a undirected graph with tree characteristics, we can choose any
# node as the root. The result graph is then a rooted tree. Among all
# possible rooted trees, those with minimum height are called minimum
# height trees (MHTs). Given such a graph, write a function to find all
# the MHTs and return a list of their root labels.
class MinimumHeightTrees:
    def find_minimum_height_trees(self, n, edges):
        """
        :type n: int
        :type edges: List[List[int]]
        :rtype: List[int]
        """
        if n == 1:
            return [0]

        my_edges = {}
        for i in xrange(n):
            my_edges[i] = set()
        for edge in edges:
            my_edges[edge[0]].add(edge[1])
            my_edges[edge[1]].add(edge[0])

        roots = []
        for i in xrange(n):
            if len(my_edges[i]) == 1:
                roots.append(i)

        removed = []
        while len(roots) > 0:
            del removed[:]
            next_roots = []
            for v in roots:
                for neighbor in my_edges[v]:
                    my_edges[neighbor].remove(v)
                    if len(my_edges[neighbor]) == 1:
                        next_roots.append(neighbor)
                removed.append(v)
            roots = next_roots

        return removed

    @staticmethod
    def test():
        print "Q-310: Minimum Height Trees"
        mht = MinimumHeightTrees()
        print mht.find_minimum_height_trees(1, [])
        print mht.find_minimum_height_trees(2, [[0,1]])
        print mht.find_minimum_height_trees(6, [[0, 3], [1, 3], [2, 3], [4, 3], [5, 4]])
        print mht.find_minimum_height_trees(7, [[0, 3], [1, 3], [2, 3], [4, 3], [5, 4], [6,0]])

MinimumHeightTrees.test()


# Q-322 Coin Change
#
# You are given coins of different denominations and a total amount of money
# amount. Write a function to compute the fewest number of coins that you need
# to make up that amount. If that amount of money cannot be made up by any
# combination of the coins, return -1. You may assume that you have an infinite
# number of each kind of coin. For example, coins = [1, 2, 5], amount = 11
# return 3 (11 = 5 + 5 + 1); coins = [2], amount = 3, return -1.
#
# Q-518 Coin Change 2
#
# You are given coins of different denominations and a total amount of money. 
# Write a function to compute the number of combinations that make up that amount. 
# You may assume that you have infinite number of each kind of coin.
class CoinChange:
    def coinChange(self, coins, amount):
        """
        :type coins: List[int]
        :type amount: int
        :rtype: int
        """
        if amount == 0 or len(coins) == 0:
            return 0

        n_coins = len(coins)
        M = [amount+1 for i in xrange(amount+1)]
        M[0] = 0

        for i in xrange(amount+1):
            for j in xrange(n_coins):
                if i >= coins[j]:
                    num = M[i - coins[j]] + 1
                    if num < M[i]:
                        M[i] = num

        return -1 if M[amount] == amount+1 else M[amount]


    def coinChange2(self, amount, coins):
        """
        :type amount: int
        :type coins: List[int]
        :rtype: int
        """
        if amount == 0:
            return 1
        combos = [0 for i in xrange(amount+1)]
        combos[0] = 1
        for i in xrange(len(coins)):
            for j in xrange(1, amount+1):
                if j >= coins[i]:
                    combos[j] += combos[j - coins[i]]
        return combos[amount]

    @staticmethod
    def test():
        print "Coin Changes"
        cc = CoinChange()
        print cc.coinChange([3,1,5], 13)
        print cc.coinChange([3,1,5], 12)
        print cc.coinChange([3,1,10], 13)
        print cc.coinChange([186,419,83,408], 6249) # = 20
        print "Coin Changes 2"
        print cc.coinChange2(0,[7])
        print cc.coinChange2(12, [2,3,7])
        print cc.coinChange2(5, [1,2,5])

CoinChange.test()

# Q-326: Power of three
#
# Any number that is power of 3 should divide the largest power of 3 (3^19).
class PowerOfThree(object):
    def isPowerOfThree(n):
        """
        :type n: int
        :rtype: bool
        """
        return n > 0 and 3**19 % n == 0

# Q-334 Increasing Triplet Subsequence
#
# Given an unsorted array return whether an increasing subsequence of length 3
# exists or not in the array. That is, return true if there exists i, j, k
# such that arr[i] < arr[j] < arr[k] given 0 <= i < j < k <= n-1 else return false.
# Your algorithm should run in O(n) time complexity and O(1) space complexity.

# If O(N) space is allowed, this is easy. With O(1) space requirement, it's
# similar idea to know the min to the left and the max to the right of each
# element. However, the tricky part is to scan twice, left-to-right and right-
# to-left, and maintain the left min and right max differently.
def search_triplet_seq(nums, direction):
    """
    : type direction: True for right-to-left, False for left-to-right
    """
    N = len(nums)
    if N < 3:
        return False
    left_min = nums[0]
    right_max = nums[N-1]
    l = 0
    r = N - 1
    while l <= r:
        if nums[l] < left_min:
            left_min = nums[l]
        if nums[r] > right_max:
            right_max = nums[r]
        if nums[l] > left_min and nums[l] < right_max:
            return True
        if nums[r] > left_min and nums[r] < right_max:
            return True
        if nums[l] >= nums[r]:
            if direction:
                r -= 1
            else:
                l += 1
        else:
            if direction:
                l += 1
            else:
                r -= 1
    return False

def increasing_triplet_seq(nums):
    """
    :type nums: List[int]
    :rtype: bool
    """
    return search_triplet_seq(nums, True) or search_triplet_seq(nums, False)

def test_increasing_triplet_seq():
    print "Increasing Triplet Sequence"
    print increasing_triplet_seq([5, 4, 3, 2, 1])
    print increasing_triplet_seq([5,3,0,-1,8,8,2,1,9,1,-2])
    print increasing_triplet_seq([5,3,0,-1,2,2,2,1,1,-2])
    print increasing_triplet_seq([5,3,0,-1,6,7,8,1,6,-2])
    print increasing_triplet_seq([5,3,0,-1,6,0,1,5,6,-2])
    print increasing_triplet_seq([1,2,3,1,2,1])
    print increasing_triplet_seq([1,2,-10,-8,-7])

test_increasing_triplet_seq()

# Q-338 Counting Bits
# 
# Given a non negative integer number num. For every numbers i in the range 0 <= i <= num calculate 
# the number of 1's in their binary representation and return them as an array.
class CountingBits(object):
    def countBits(self, num):
        """
        :type num: int
        :rtype: List[int]
        """
        if num == 0:
            return [0]
        result, right, left = [0,1], 2, 0
        for i in xrange(2, num+1):
            result.append(result[left]+1)
            left += 1
            if left == right:
                left, right = 0, len(result)
        return result

    @staticmethod
    def test():
        print "Q-338 Counting Bits"
        cb = CountingBits()
        print cb.countBits(5) # [0, 1, 1, 2, 1, 2]
        print cb.countBits(20)

CountingBits.test()


# Q-971 Reverse Only Letters
class ReverseOnlyLetters(object):
    def is_letter(self, c):
        cv = ord(c)
        return (cv >= ord('a') and cv <= ord('z')) or (cv >= ord('A') and cv <= ord('Z'))

    def reverseOnlyLetters(self, S):
        """
        :type S: str
        :rtype: str
        """
        l, r = 0, len(S)-1
        chars = list(S)
        while l < r:
            while l < len(S)-1 and not self.is_letter(chars[l]):
                l += 1
            while r >= 0 and not self.is_letter(chars[r]):
                r -= 1
            if l < r:
                t = chars[l]
                chars[l] = chars[r]
                chars[r] = t
                l += 1
                r -= 1
        return ''.join(chars)

    @staticmethod
    def test():
        print "Q-971 Reverse Only Letters"
        rol = ReverseOnlyLetters()
        print rol.reverseOnlyLetters("a-bC-dEf-ghIj")

ReverseOnlyLetters.test()