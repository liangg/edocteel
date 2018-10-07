
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

# Q-347 Top K Frequent Elements (priority queue)
#
import heapq

class TopKFrequent:
    class NumCountItem:
        def __init__(self, key, count):
            self.key = key
            self.count = count

        # min heap
        def __cmp__(self, other):
            return self.count > other.count

    def top_k_frequent_items(self, nums, k):
        """
        :type nums: List[int]
        :type k: int
        :rtype: List[int]
        """
        num_counts = {}
        for n in nums:
            if num_counts.has_key(n):
                num_counts[n] = num_counts[n] + 1
            else:
                num_counts[n] = 1

        # push K items to the priority queue
        i = 0
        pq = []
        for key, val in num_counts.items():
            if i < k:
                heapq.heappush(pq, self.NumCountItem(key, val))
                del num_counts[key]
                i += 1
        # iterate the rest of items and maintain the top K items
        for key, val in num_counts.iteritems():
            if pq[0].count < val:
                heapq.heappop(pq)
                heapq.heappush(pq, self.NumCountItem(key, val))

        return [item.key for item in pq] # list comprehension

    @staticmethod
    def test_top_k_frequent():
        print "Top K Frequent Items"
        topk = TopKFrequent()
        print topk.top_k_frequent_items([1,2,3,4,5,2,3,4,3,4,], 3)
        print topk.top_k_frequent_items([1,1,1,2,2,3], 2)

TopKFrequent.test_top_k_frequent()


# Q-357 Count Numbers with Unique Digits
#
# Given a non-negative integer n, count all numbers with unique digits, x, where 0 <= x < 10^n.
# Given n = 2, return 91. (The answer should be the total numbers in the range of 0 <= x < 100,
# excluding [11,22,33,44,55,66,77,88,99])
#
class CountNumbersWithUniqueDigits(object):
    def countNumbersWithUniqueDigits(self, n):
        """
        :type n: int
        :rtype: int
        """
        return


# Q-373 Find K Pairs with Smallest Sums
#
# You are given two integer arrays nums1 and nums2 sorted in ascending order and an integer k.
# Define a pair (u,v) which consists of one element from the first array and one element from
# the second array. Find the k pairs (u1,v1),(u2,v2) ...(uk,vk) with the smallest sums.
class KPairsWithSmallesSums:
    def k_smallest_pairs(self, nums1, nums2, k):
        """
        :type nums1: List[int]
        :type nums2: List[int]
        :type k: int
        :rtype: List[List[int]]
        """
        res = []
        size1, size2 = (len(nums1), len(nums2))
        pq = []
        idx2 = 0
        while len(res) < min(k, size1*size2):
            if idx2 < size2:
                for i in xrange(size1):
                    # Python heapq use first element in tuple as cmp key
                    heapq.heappush(pq, (nums1[i] + nums2[idx2], i, idx2))
                idx2 += 1
            _, i, j = heapq.heappop(pq)
            res.append([nums1[i], nums2[j]])

        return res

    @staticmethod
    def test():
        print "K Pairs with Smallest Sums"
        kpss = KPairsWithSmallesSums()
        print kpss.k_smallest_pairs([1,7,11], [2,4,6], 3)
        print kpss.k_smallest_pairs([1,7,11], [2,4,6], 5)
        print kpss.k_smallest_pairs([1,7,11], [2,4,6], 6)

KPairsWithSmallesSums.test()

# Q-389 Find The Difference
#
# Given two strings s and t which consist of only lowercase letters. String t is generated by random 
# shuffling string s and then add one more letter at a random position. Find the letter that was 
# added in t.
class FindDifference(object):
    def findTheDifference(self, s, t):
        """
        :type s: str
        :type t: str
        :rtype: str
        """
        counts = {}
        for i in xrange(len(s)):
            c = 1
            if counts.has_key(s[i]):
                c = counts[s[i]] + 1
            counts[s[i]] = c
        for i in xrange(len(t)):
            if not counts.has_key(t[i]) or counts[t[i]] == 0:
                return t[i]
            c = counts[t[i]]
            counts[t[i]] = c - 1

    @staticmethod
    def test():
        print "Q-389 Find Difference"
        fd = FindDifference()
        print fd.findTheDifference("abbcd", "abcbde")

# Q-394 Decode String
class DecodeString(object):
    def decodeString(self, s):
        """
        :type s: str
        :rtype: str
        """
        decoded, n = self.decode(s)
        return decoded

    def decode(self, s):
        cstr, nstr = "", ""
        for i in xrange(0, len(s)):
            if s[i] == '[':
                num = int(nstr)
                decoded, suffix = self.decode(s[i+1:])
                decoded_suffix, suffix_suffix = self.decode(suffix)
                return cstr + decoded*num + decoded_suffix, suffix_suffix
            elif s[i] == ']':
                return cstr, s[i+1:]
            if s[i] >= '0' and s[i] <= '9':
                nstr += s[i]
            else:
                cstr += s[i]
        return s, ""

    @staticmethod
    def test():
        print "Q-394 Decode String"
        ds = DecodeString()
        print ds.decodeString("3[a]2[bc]")
        print ds.decodeString("3[a2[c]]")
        print ds.decodeString("2[abc]3[cd]ef")
        print ds.decodeString("10[leet]")

DecodeString.test()


# Q-396 Max Rotate Function
class MaxRotateFunction:
    def maxRotateFunction(self, A):
        N = len(A)
        allSum = 0
        rfv = 0
        for i in xrange(N):
            allSum += A[i]
            rfv += i * A[i]

        maxRfv = rfv
        for i in xrange(1, N):
            rfv = rfv - (N-1)*A[N-i] + (allSum - A[N-i])
            if rfv > maxRfv:
                maxRfv = rfv

        return maxRfv

    @staticmethod
    def test():
        print "Max Rotate Function"
        r = MaxRotateFunction()
        print r.maxRotateFunction([4,3,2,6])

MaxRotateFunction.test()


# Q-417 Pacific Atlantic Water Flow
#
# Given an m x n matrix of non-negative integers representing the height of each unit cell
# in a continent, the "Pacific ocean" touches the left and top edges of the matrix and the
# "Atlantic ocean" touches the right and bottom edges. Water can only flow in four directions
# (up, down, left, or right) from a cell to another one with height equal or lower. Find the
# list of grid coordinates where water can flow to both the Pacific and Atlantic ocean.

class PacificAtlanticWaterFlow:
    def converge_water_flow_path(self, matrix, M, N, pacific, atlantic):
        n_changes = 0
        # whether can flow to pacific
        for i in xrange(M):
            for j in xrange(N):
                if pacific[i][j] == False:
                    if (i == 0 or j == 0 or (matrix[i][j] >= matrix[i-1][j] and pacific[i-1][j]) or \
                        (matrix[i][j] >= matrix[i][j-1] and pacific[i][j-1])):
                        pacific[i][j] = True
                        n_changes += 1
        # whether can flow to atlantic
        for i in xrange(M-1, -1, -1):
            for j in xrange(N-1, -1, -1):
                if atlantic[i][j] == False:
                    if (i == M-1 or j == N-1 or (matrix[i][j] >= matrix[i+1][j] and atlantic[i+1][j]) or \
                        (matrix[i][j] >= matrix[i][j+1] and atlantic[i][j+1])):
                        atlantic[i][j] = True
                        n_changes += 1

        # re-check whether can flow to pacific from right or down direction
        for i in xrange(M-1, -1, -1):
            for j in xrange(N-1, -1, -1):
                if pacific[i][j] == False:
                    if (j < N-1 and matrix[i][j] >= matrix[i][j+1] and pacific[i][j+1]) or \
                        (i < M-1 and matrix[i][j] >= matrix[i+1][j] and pacific[i+1][j]):
                        pacific[i][j] = True
                        n_changes += 1
        # re-check whether can flow to atlantic from left or top direction
        for i in xrange(0, M):
            for j in xrange(0, N):
                if atlantic[i][j] == False:
                    if (j > 0 and matrix[i][j] >= matrix[i][j-1] and atlantic[i][j-1]) or \
                        (i > 0 and matrix[i][j] >= matrix[i-1][j] and atlantic[i-1][j]):
                        atlantic[i][j] = True
                        n_changes += 1
        return n_changes

    def pacific_atlantic_water_flow(self, matrix):
        """
        :type matrix: List[List[int]]
        :rtype: List[List[int]]
        """
        M = len(matrix)
        if M == 0:
            return []
        N = len(matrix[0])
        if N == 0:
            return []

        pacific = [[False for i in xrange(N)] for i in xrange(M)] # list comprehension
        atlantic = [[False for i in xrange(N)] for i in xrange(M)]
        # keep converging water flow path through all 4 directions
        while True:
            if self.converge_water_flow_path(matrix, M, N, pacific, atlantic) == 0:
                break

        result = []
        for i in xrange(M):
            for j in xrange(N):
                if pacific[i][j] and atlantic[i][j]:
                    result.append([i,j])

        return result

    @staticmethod
    def test_pacific_atlantic_water_flow():
        print "Pacific Atlantic Water Flow"
        sol = PacificAtlanticWaterFlow()
        print sol.pacific_atlantic_water_flow([[2],[3],[2],[1]])
        print sol.pacific_atlantic_water_flow([[2,3,2,1]])
        # [[0, 4], [1, 3], [1, 4], [2, 2], [3, 0], [3, 1], [4, 0]]
        print sol.pacific_atlantic_water_flow([[1,2,2,3,5], [3,2,3,4,4], [2,4,5,3,1], [6,7,1,4,5],[5,1,1,2,4]])
        # [[0,2],[1,0],[1,1],[1,2],[2,0],[2,1],[2,2]]
        print sol.pacific_atlantic_water_flow([[1,2,3],[8,9,4],[7,6,5]])
        # [[0, 1], [0, 2], [1, 0], [1, 1], [1, 2], [2, 0], [2, 1], [2, 2]]
        print sol.pacific_atlantic_water_flow([[1,11,6],[10,9,7],[5,5,6]])
        # [[0, 3], [1, 0], [1, 1], [1, 2], [1, 3], [2, 0], [2, 1], [2, 2], [2, 3]]
        print sol.pacific_atlantic_water_flow([[1,2,3,4],[8,9,9,4],[8,7,6,5]])
        # [[0,13],[0,14],[1,13],[11,3],[12,0],[12,2],[12,3],[13,0],[13,1],[13,2],[14,0],[15,0]]
        print sol.pacific_atlantic_water_flow([[8,13,11,18,14,16,16,4,4,8,10,11,1,19,7],[2,9,15,16,14,5,8,15,9,5,14,6,10,15,5],
            [15,16,17,10,3,6,3,4,2,17,0,12,4,1,3],[13,6,13,15,15,16,4,10,7,4,19,19,4,9,13],
            [7,1,9,14,9,11,5,4,15,19,6,0,0,13,5],[9,9,15,12,15,5,1,1,18,1,2,16,15,18,9],
            [13,0,4,18,12,0,11,0,1,15,1,15,4,2,0],[11,13,12,16,9,18,6,8,18,1,5,12,17,13,5],
            [7,17,2,5,0,17,9,18,4,13,6,13,7,2,1],[2,3,9,0,19,6,6,15,14,4,8,1,19,5,9],
            [3,10,5,11,7,14,1,5,3,19,12,5,2,13,16],[0,8,10,18,17,5,5,8,2,11,5,16,4,9,14],
            [15,9,16,18,9,5,2,5,13,3,10,19,9,14,3],[12,11,16,1,10,12,6,18,6,6,18,10,9,5,2],
            [17,9,6,6,14,9,2,2,13,13,15,17,15,3,14],[18,14,12,6,18,16,4,10,19,5,6,8,9,1,6]])
        # [[0,29],[1,28],[2,27],[2,28],[3,27],[34,2],[35,2],[35,3],[36,1],[36,2],[37,0],[37,2],[37,3],[38,0],[38,2]]
        print sol.pacific_atlantic_water_flow([[11,2,11,0,15,12,4,15,0,14,11,3,19,11,5,11,18,19,4,3,11,1,9,17,5,2,15,18,11,15],[12,10,8,15,4,7,4,5,7,8,5,12,3,3,10,12,16,15,17,13,13,16,0,0,17,17,11,3,14,0],[8,18,1,6,15,16,14,11,9,11,3,4,17,7,2,16,18,2,0,0,16,18,10,15,14,18,10,19,17,6],[14,17,4,13,13,6,16,1,3,18,18,18,4,1,15,4,0,9,19,3,6,7,19,13,11,11,10,19,3,15],[16,6,19,17,19,17,5,12,6,3,1,0,3,10,13,18,4,3,9,0,1,18,9,15,18,3,4,6,1,15],[1,2,12,9,9,7,17,0,1,14,18,1,5,3,0,7,2,19,7,19,1,11,1,3,2,4,0,3,16,18],[18,10,10,3,12,11,7,8,3,16,7,11,11,12,15,1,13,9,8,17,1,9,7,19,1,14,8,10,18,14],[5,19,9,4,10,14,1,5,11,16,11,3,5,4,19,8,11,16,19,12,6,3,18,16,17,8,11,19,7,14],[0,15,17,11,10,13,19,0,10,3,15,19,3,3,3,4,3,12,17,10,5,16,12,5,5,17,5,17,6,6],[8,19,9,3,13,8,13,17,4,12,13,8,13,12,10,10,16,7,2,8,17,3,7,1,7,16,11,19,13,19],[6,19,6,13,10,5,14,7,3,1,10,6,4,8,15,0,0,2,12,13,14,14,7,5,1,16,15,15,4,7],[7,7,11,14,2,4,14,2,2,0,6,11,15,14,11,13,2,3,14,9,16,3,8,15,2,18,15,15,2,2],[7,5,12,10,14,3,6,9,2,1,2,15,0,4,7,9,7,12,15,9,2,13,7,8,7,9,4,3,5,19],[11,9,1,8,0,15,1,6,5,11,14,19,6,11,0,12,1,6,8,7,0,1,2,9,14,4,5,8,3,16],[8,0,11,5,14,4,19,0,6,8,1,10,13,8,18,6,6,4,5,9,10,14,14,13,12,16,4,3,3,11],[0,9,6,19,16,4,5,10,13,19,8,15,14,7,13,11,17,18,14,18,19,11,0,4,12,11,2,8,17,14],[16,19,16,9,9,14,5,13,7,10,18,6,15,12,12,1,11,16,1,8,1,7,16,7,19,6,12,0,15,0],[2,4,18,15,13,9,4,18,19,5,16,7,10,1,7,7,4,4,10,8,13,15,9,4,16,13,6,3,13,7],[3,11,10,13,6,4,0,13,11,4,5,6,19,13,8,10,8,9,2,4,4,11,12,8,12,15,6,1,10,12],[7,6,19,3,2,14,15,6,9,1,6,14,4,15,13,9,14,7,10,12,17,18,6,4,12,4,1,6,6,12],[15,17,9,15,9,15,9,10,10,11,12,17,2,18,11,0,6,11,14,17,2,13,9,13,3,4,3,1,8,11],[17,13,12,17,4,19,19,7,7,13,19,10,4,16,1,18,14,2,9,18,2,8,3,1,10,9,12,6,2,11],[17,12,6,8,3,16,5,2,16,3,13,3,13,9,11,11,5,12,14,16,3,19,16,16,1,14,5,3,17,19],[1,4,0,3,1,17,5,15,2,19,12,7,18,13,1,0,7,2,9,18,10,18,8,9,13,13,8,10,14,14],[9,14,4,18,10,18,3,9,9,17,16,4,19,7,3,18,7,0,10,13,9,10,11,16,3,5,1,2,16,19],[8,10,13,8,7,2,9,4,16,15,5,4,15,7,9,7,15,2,6,17,14,3,13,3,4,15,13,10,8,16],[17,7,19,19,13,12,6,0,11,4,10,4,1,9,15,9,7,7,14,6,7,18,9,13,6,16,5,2,17,1],[2,7,0,4,8,18,4,11,13,4,11,12,3,18,11,2,4,18,3,3,17,9,18,11,9,15,14,19,7,17],[13,1,15,18,4,12,18,18,15,16,7,17,9,15,11,3,9,7,18,13,3,11,7,19,10,10,7,13,7,19],[17,17,14,3,19,7,1,13,9,3,6,16,10,8,14,8,17,18,12,11,4,11,10,15,9,0,4,12,7,15],[4,4,8,1,7,11,13,4,11,5,18,2,16,11,16,13,0,13,13,12,11,15,8,4,0,3,2,9,8,15],[17,4,13,5,3,17,14,4,7,6,6,11,16,18,2,0,3,12,1,5,12,16,3,14,4,16,5,8,15,9],[5,3,17,17,6,4,19,5,4,6,11,4,14,18,4,19,16,15,1,17,3,8,13,14,16,13,18,19,6,4],[15,0,8,15,6,6,11,8,18,2,4,10,18,16,15,8,1,5,9,13,7,19,12,2,9,18,1,15,12,8],[5,0,18,14,1,8,18,15,5,13,15,7,8,8,9,0,14,12,4,17,2,10,9,7,19,7,19,9,7,1],[7,4,16,16,13,4,3,6,15,11,14,7,3,0,5,15,10,13,18,18,11,6,7,9,19,13,4,2,7,9],[9,14,15,11,14,5,15,1,19,15,3,4,0,10,4,1,2,15,18,15,15,2,9,0,3,10,9,16,4,1],[14,13,17,19,0,13,15,9,16,18,5,6,16,16,6,10,14,15,17,5,9,2,5,11,19,19,11,6,15,14],[17,7,19,6,5,19,10,2,11,17,17,13,16,13,19,4,12,3,4,13,7,9,19,9,12,3,16,8,18,13]])

PacificAtlanticWaterFlow.test_pacific_atlantic_water_flow()

# Q-435 Non-overlapping Intervals
#
# Given a collection of intervals, find the minimum number of intervals you need to remove to make the rest of the
# intervals non-overlapping. You may assume the interval's end point is always bigger than its start point.
# Intervals like [1,2] and [2,3] have borders "touching" but they don't overlap each other.
class NonOverlappingIntervals:
    def eraseOverlapIntervals(self, intervals):
        # sort intervals by right closing value
        sorted_intervals = sorted(intervals, key = lambda interval: interval[1])

        N = len(intervals)
        result = 0
        idx = 0
        while idx < N:
            result += 1
            next = idx + 1
            while next < N and sorted_intervals[next][0] < sorted_intervals[idx][1]:
                next += 1
            idx = next
        print "maximum non-overlapping intervals " + str(result)
        return N - result

    @staticmethod
    def test():
        print "Non-overlapping Intervals"
        noli = NonOverlappingIntervals()
        print noli.eraseOverlapIntervals([[1,2],[2,3],[3,4],[1,3]])
        print noli.eraseOverlapIntervals([[1,2],[1,2],[1,2]])
        print noli.eraseOverlapIntervals([[1,2],[3,4]])

NonOverlappingIntervals.test()

# Q-438 Find All Anagrams in a String
class FindAllAnagramsInString:
    def findAnagrams(self, s, p):
        if not s or len(s) < len(p):
            return []

        N = len(p)
        start = ord('a')
        pcount = [0 for i in xrange(26)]
        scount = [0 for i in xrange(26)]
        pcode, code = (0, 0)
        for i in xrange(N):
            pcode += ord(p[i])
            code += ord(s[i])
            pcount[ord(p[i]) - start] += 1
            scount[ord(s[i]) - start] += 1
        code -= ord(s[N-1])
        scount[ord(s[N-1]) - start] -= 1

        result = []
        for i in xrange(N-1, len(s)):
            code += ord(s[i])
            scount[ord(s[i]) - start] += 1
            if code == pcode:
                matched = True
                for j in xrange(26):
                    if pcount[j] != scount[j]:
                        matched = False
                        break
                if matched:
                    result.append(i+1-N)
            code -= ord(s[i+1-N])
            scount[ord(s[i+1-N]) - start] -= 1
        return result

    @staticmethod
    def test():
        print "Q-438 Find All Anagrams In String"
        aas = FindAllAnagramsInString()
        print aas.findAnagrams("af","be")
        print aas.findAnagrams("cbaebabacd", "abc")
        print aas.findAnagrams("abab", "ab")
        #print aas.findAnagrams("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz",
#"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz")

FindAllAnagramsInString.test()


# Q-442 Find All Duplicates in Array
# 
# Given an array of integers, 1 < = a[i] < = n (n = size of array), some elements appear twice and others appear 
# once. Find all the elements that appear twice in this array. Could you do it without extra space and in O(n) runtime?
#
# Q-448 Find All NUmbers Disappeared in array
#
# Given an array of integers where 1 < = a[i] < = n (n = size of array), some elements appear twice and others 
# appear once. Find all the elements of [1, n] inclusive that do not appear in this array.
class FindAllDuplicatesInArray():
    def findDuplicates(self, nums):
        res = []
        # it is important that 1 < = nums[i] < = len(nums), so we negate the value in radix cell
        for i in xrange(len(nums)):
            if nums[abs(nums[i]) - 1] < 0:
                res.append(abs(nums[i]))
                continue
            nums[abs(nums[i])-1] *= -1
        # restore the array
        for i in xrange(len(nums)):
            if nums[i] < 0:
                nums[i] *= abs(nums[i])
        return res

    def findDisappearedNumbers(self, nums):
        # it is important that 1 < = nums[i] < = len(nums), so we negate the value in radix cell
        for i in xrange(len(nums)):
            if nums[abs(nums[i]) - 1] < 0:
                continue
            nums[abs(nums[i])-1] *= -1

        res = []
        # restore the array
        for i in xrange(len(nums)):
            if nums[i] < 0:
                nums[i] *= abs(nums[i])
            else:
                res.append(i+1)
        return res

    @staticmethod
    def test():
        print "Find All Duplicates in Array"
        fad = FindAllDuplicatesInArray()
        print fad.findDuplicates([4,3,2,7,8,2,3,1])
        print fad.findDisappearedNumbers([4,3,2,7,8,2,3,1])

FindAllDuplicatesInArray.test()

# Q-454 4-Sum II
#
# Given four lists A, B, C, D of integer values, compute how many tuples (i, j, k, l) there are such that 
# A[i] + B[j] + C[k] + D[l] is zero. To make problem a bit easier, all A, B, C, D have same length of N 
# where 0 < = N < = 500.
class FourSum(object):
    def fourSumCount(self, A, B, C, D):
        """
        :type A: List[int]
        :type B: List[int]
        :type C: List[int]
        :type D: List[int]
        :rtype: int
        """

    @staticmethod
    def test():
        print "Q-454 4-Sum II"
        fs = FourSum()


# Q-459 Repeated Substring Pattern
#
# Given a non-empty string check if it can be constructed by taking a substring of it and appending multiple 
# copies of the substring together. You may assume the given string consists of lowercase English letters 
# only and its length will not exceed 10000.
class RepeatedSubstringPattern(object):
    def samePattern(self, s, p, length):
        for i in xrange(length):
            if p+i >= len(s) or s[i] != s[p+i]:
                return False
        return True

    def repeatedSubstringPattern(self, s):
        if len(s) == 0 or len(s) == 1:
            return False
        begin = 0
        for i in xrange(len(s)):
            if s[i] != s[0]:
                break
            begin += 1
        for i in xrange(begin, len(s)):
            if s[i] == s[0] and i > 0 and i+i-1 < len(s) and s[i-1] == s[i+i-1]:
                pi = i
                matched = True
                while pi < len(s):
                    if not self.samePattern(s, pi, i):
                        matched = False
                        break
                    pi += i
                if matched:
                    return True
        return False if begin < len(s) else True

    @staticmethod
    def test():
        print "Repeated Substring Pattern"
        rsp = RepeatedSubstringPattern()
        print rsp.repeatedSubstringPattern("aba")
        print rsp.repeatedSubstringPattern("aaab")
        print rsp.repeatedSubstringPattern("aaaa")
        print rsp.repeatedSubstringPattern("abaababaab")

RepeatedSubstringPattern.test()

# Q-462 Minimum Moves to Equal Array Elements II
# 
# Given a non-empty integer array, find the minimum number of moves required to make all array elements 
# equal, where a move is incrementing a selected element by 1 or decrementing a selected element by 1.
# You may assume the array's length is at most 10,000.
class MinimumMovesEqualArrayElements(object):
    def minMoves2(self, nums):
        """
        :type nums: List[int]
        :rtype: int
        """
        N = len(nums)
        if N == 0:
            return 0
        nums.sort()
        median = nums[N/2]
        result, l, r = 0, 0, N-1
        while l < r:
            result += median - nums[l]
            result += nums[r] - median
            l += 1
            r -= 1
        return result



# Q-477 Total Hamming Distance
#
# The Hamming distance between two integers is the number of positions at which the corresponding bits are 
# different. Now your job is to find the total Hamming distance between all pairs of the given numbers.
# Elements of the given array are in the range of 0 to 10 ^ 9; length of the array will not exceed 10 ^ 4.
class TotalHammingDistance:
    def hammingDistance(x, y):
        xy = x ^ y
        cnt = 0
        while xy > 0:
            if xy & 1 != 0:
                cnt += 1
            xy = xy >> 1
        return cnt

    def totalHammingDistance(self, nums):
        zeros = [0 for i in xrange(32)]
        ones = [0 for i in xrange(32)]
        ns = [nums[i] for i in xrange(len(nums))]
        for i in xrange(32):
            for j in xrange(len(nums)):
                if ns[j] & 1 != 0:
                    ones[i] += 1
                else:
                    zeros[i] += 1
                ns[j] >>= 1
        dist = 0
        for i in xrange(32):
            dist += ones[i] * zeros[i]
        return dist

    @staticmethod
    def test():
        print "Total Hamming Distances"
        thd = TotalHammingDistance()
        print thd.totalHammingDistance([4,14,2,1])

TotalHammingDistance.test()


# Q-491 Increasing Subsequences
# 
# Given an integer array, your task is to find all the different possible increasing subsequences of the 
# given array, and the length of an increasing subsequence should be at least 2.
class IncreasingSubsequences():

    def findSubsequences(self, nums):
        """
        :type nums: List[int]
        :rtype: List[List[int]]
        """
        if len(nums) == 0:
            return []
        # use set to remove dups, and set element is tuple instead of list (unhashable)
        memo = set()
        memo.add((nums[0],))
        for i in xrange(1, len(nums)):
            # list clone set elements (tuple) so append does not mutate the memoization
            l = list(memo)
            for ss in l:
                if nums[i] >= ss[-1]:
                    memo.add(ss + (nums[i],))
            memo.add((nums[i],))
        return [list(e) for e in memo if len(e) > 1]

    @staticmethod
    def test():
        print "Increasing Subsequences"
        incseq = IncreasingSubsequences()
        print incseq.findSubsequences([4,6,7,7])

IncreasingSubsequences.test()


# Q-496 Next Greater Element
# 
# You are given two arrays (without duplicates) nums1 and nums2 where nums1 elements are subset of nums2. 
# Find all the next greater numbers for nums1's elements in the corresponding places of nums2. The Next 
# Greater Number of a number x in nums1 is the first greater number to its right in nums2. If it does not 
# exist, output -1 for this number.
#
# Q-503 Next Greater Element 2
#
# Given a circular array (the next element of the last element is the first element of the array), print 
# he Next Greater Number for every element. The Next Greater Number of a number x is the first greater 
# number to its traversing-order next in the array, which means you could search circularly to find its 
# next greater number. If it doesn't exist, output -1 for this number.
#
# Q-556 Next Greater Element 3
#
# Given a positive 32-bit integer n, you need to find the smallest 32-bit integer which has exactly the 
# same digits existing in the integer n and is greater in value than n. If no such positive 32-bit integer 
# exists, you need to return -1.
class NextGreaterElement(object):
    def nextGreaterElement(self, findNums, nums):
        findNumsMap = {}
        for i in xrange(len(findNums)):
            findNumsMap[findNums[i]] = i
        # it may be better to use None as initial value
        res = [-1 for i in xrange(len(findNums))]
        stk = []
        for i in xrange(len(nums)):
            while len(stk) > 0:
                if nums[i] <= stk[-1]:
                    break
                e = stk.pop()
                res[findNumsMap[e]] = nums[i]
            if nums[i] in findNumsMap:
                stk.append(nums[i])
        return res

    # Q-503
    def nextGreaterElementsCircularArray(self, nums):
        res = [-1 for i in xrange(len(nums))]
        stk = []
        for i in xrange(2*len(nums)):
            idx = i % len(nums)
            while len(stk) > 0:
                if nums[idx] <= stk[-1][0]:
                    break
                e = stk.pop()
                res[e[1]] = nums[idx]
            if res[idx] == -1:
                stk.append([nums[idx], idx])
        return res

    # Q-556 
    def nextGreaterElement3(self, n):
        import sys
        print sys.maxint
        counts = [0 for i in xrange(10)]
        ng, replace = n, -1
        # digit low to high
        while ng > 0:
            digit = ng%10
            ng /= 10
            counts[digit] += 1
            # if there is bigger digit in a lower decimal position
            replace = self.hasReplaceDigit(digit, counts)
            if replace != -1:
                break
        if replace == -1:
            return -1
        # reconstruct the next greater number
        ng = ng*10 + replace
        counts[replace] -= 1
        for i in xrange(10):
            while counts[i] > 0:
                ng = ng*10 + i
                counts[i] -= 1
        return ng if ng < 0x7FFFFFFF else -1

    def hasReplaceDigit(self, digit, counts):
        for i in xrange(digit+1, 10):
            if counts[i] > 0:
                return i
        return -1

    @staticmethod
    def test():
        print "Next Greater Element"
        nge = NextGreaterElement()
        print nge.nextGreaterElement([4,1,2], [1,3,4,2])
        print nge.nextGreaterElement([2,4], [1,2,3,4])
        print nge.nextGreaterElementsCircularArray([1,2,1]) # [2,-1,2]
        print nge.nextGreaterElementsCircularArray([2,1,5,4,3]) # [5,5,-1,5,5]
        print nge.nextGreaterElement3(32) # -1
        print nge.nextGreaterElement3(123) # 132
        print nge.nextGreaterElement3(83264) # 83426
        print nge.nextGreaterElement3(176432) # 213467
        print nge.nextGreaterElement3(1999999999) # -1

NextGreaterElement.test()

FindDifference.test()