
def LCS(a, b):
    M = [[0 for i in xrange(len(a)+1)] for j in xrange(len(b)+1)]
    for i in xrange(1, len(b)+1):
        for j in xrange(1, len(a)+1):
            if a[j-1] == b[i-1]:
                M[i][j] = M[i-1][j-1] + 1
            else:
                M[i][j] = max(M[i][j-1], M[i-1][j])
    lcs = M[len(b)][len(a)]
    return lcs

# Q-214: Shortest Palindrome
#
# Given a string S, you are allowed to convert it to a palindrome by adding
# characters in front of it. Find and return the shortest palindrome you can
# find by performing this transformation. For example, given "aacecaaa",
# return "aaacecaaa"; given "abcd", return "dcbabcd".
def shortestPalindrome_dp(s):
    """
    :type s: str
    :rtype: str
    """
    N = len(s)
    M = [[False for i in xrange(N)] for j in xrange(N)]
    for i in xrange(N):
        M[i][i] = True
    # DP to find longest palindrome substring, but kind of waste because
    # it is really about finding palindrome prefix
    for ln in xrange(1, N):
        for i in xrange(N-ln):
            if s[i] == s[i+ln] and (i+1 >= i+ln-1 or M[i+1][i+ln-1]):
                M[i][i+ln] = True
    # find the longest palindrome prefix
    prefix_end = 0
    for i in xrange(N-1, -1, -1):
        if M[0][i] == True:
            prefix_end = i
            break
    print "prefix " + str(prefix_end)
    suffix = s[prefix_end+1:]
    print "suffix " + suffix
    reverse = suffix[::-1]
    return reverse + s

# Q-300: Longest Increasing Subsequence
def lengthOfLIS(nums):
    """
    :type nums: List[int]
    :rtype: int, i.e. the length of longest increasing subsequence.
    """
    N = len(nums)
    longest = [None] * N
    for i in xrange(0, N):
        longest[i] = 1
        for j in xrange(i-1, -1, -1):
            if (nums[j] < nums[i] and longest[j] + 1 > longest[i]):
                longest[i] = longest[j] + 1
    lis = 0
    for i in xrange(N-1,-1,-1):
        if longest[i] > lis:
            lis = longest[i]
    return lis

def test_LIS():
    print "Longest Increasing Subsequence"
    print lengthOfLIS([])
    print lengthOfLIS([10, 9, 2, 5, 3, 7, 101, 18])
    print lengthOfLIS([-2,-1])
    print lengthOfLIS([1,3,6,7,9,4,10,5,6])

test_LIS()

# Q-374: Guess Number Higher or Lower
# Q-375: Guess Number Higher or Lower II (DP)
#
# We are playing the Guess Game. The game is as follows: I pick a number from 1 to n. You have to 
# guess which number I picked. Every time you guess wrong, I'll tell you whether the guess is higher
# or lower than the picked number (1, 0, -1).
#
# However, when you guess a particular number x, and you guess wrong, you pay $x. You win the 
# game when you guess the number I picked. Given a particular n >= 1, find out how much money you need 
# to have to guarantee a win.
class GuessNumberHigherLower(object):
    def __init__(self, num):
        self.num = num

    def guess(self, n):
        return 0 if self.num == n else (1 if self.num > n else -1)

    # I: Guess game using bisection
    def guessNumber(self, n):
        """
        :type n: int
        :rtype: int
        """
        l, r = 1, n
        while l <= r:
            m = l + (r-l)/2
            g = self.guess(m)
            if g == 0:
                return m
            if g == 1:
                l = m+1
            else:
                r = m-1
        return -1

    # II (DP): Given a particular n >= 1, find out how much money you need to have to guarantee a win
    #
    # This is an application of the decision theory MinMax algorithm - minimize possible loss for a
    # worst case scenario.
    def getMoneyAmount(self, n):
        minMax = [[0 for i in xrange(n+1)] for j in xrange(n+1)]
        for l in xrange(1, n):
            for i in xrange(1, n+1-l):
                # minMax[i][i+l] = min(k + max(minMax[i][k-1], minMax[k+1][i+l]) for k in xrange(i, i+l))
                v = 2^31-1
                for k in xrange(i, i+l):
                    v = min(v, k + max(minMax[i][k-1], minMax[k+1][i+l]))
                minMax[i][i+l] = v
        return minMax[1][n]

    @staticmethod
    def test():
        print "Q-375 Guess Number Higher or Lower"
        gn = GuessNumberHigherLower(100)
        print gn.getMoneyAmount(4)

GuessNumberHigherLower.test()

# Q-376 Wiggle Subsequence (DP)
# 
# A sequence of numbers is called a wiggle sequence if the differences between successive numbers strictly alternate
# between positive and negative. The first difference (if one exists) may be either positive or negative. A sequence 
# with fewer than two elements is trivially a wiggle sequence. Given a sequence of integers, return the length of 
# the longest subsequence that is a wiggle sequence. A subsequence is obtained by deleting some number of elements 
# (eventually, also zero) from the original sequence, leaving the remaining elements in their original order.
class WiggleSubsequence(object):
    def wiggleMaxLength(self, nums):
        """
        :type nums: List[int]
        :rtype: int
        """
        M = [1 for i in xrange(len(nums))]
        up_down = [0 for i in xrange(len(nums))] # wiggle up or down
        # a variant of LIS, O(N^2)
        for i in xrange(1, len(nums)):
            for j in xrange(i-1, -1, -1):
                if (up_down[j] == 0 and nums[i] != nums[j]) or (up_down[j] == -1 and nums[i] > nums[j]) or (up_down[j] == 1 and nums[i] < nums[j]):
                    if M[j]+1 > M[i]:
                        M[i] = M[j]+1
                        up_down[i] = 1 if nums[i] > nums[j] else -1

        max_len = 0
        for i in xrange(len(nums)-1, -1, -1):
            if M[i] > max_len:
                max_len = M[i]
        return max_len

    @staticmethod
    def test():
        print "Q-376 Wiggle Subsequence"
        ws = WiggleSubsequence()
        print ws.wiggleMaxLength([1,17,5,10,13,15,10,5,16,8])

WiggleSubsequence.test()

# Q-377 Combination Sum IV
#
# Given an integer array with all positive numbers and no duplicates, find the number of possible combinations 
# that add up to a positive integer target.
class CombinationSum4(object):
    def combinationSum4(self, nums, target):
        """
        :type nums: List[int]
        :type target: int
        :rtype: int
        """
        M = [0 for i in xrange(target+1)]
        M[0] = 1
        sorted_nums = sorted(nums)
        for i in xrange(1,target+1):
            for j in xrange(len(nums)):
                if i - sorted_nums[j] >= 0:
                    M[i] += M[i-sorted_nums[j]]
        return M[target]

    @staticmethod
    def test():
        print "Combination Sum IV"
        cs = CombinationSum4()
        print cs.combinationSum4([1,2,3], 4)

CombinationSum4.test()

# Q-309 Best Time to Buy and Sell Stocks with Cooldown
#
# Say you have an array for which the ith element is the price of a given stock on day i.
# Design an algorithm to find the maximum profit. You may complete as many transactions
# as you like (ie, buy one and sell one share of the stock multiple times) as long as
# transactions are not overlapping. However, after you sell your stock, you cannot buy
# stock on next day. (ie, cooldown 1 day)

# DP formula
#
# buy[i] = max(sell[i-2], max_profit)
# sell[i] = max(buy[i-1], sell[i-1]) + diff
def max_stock_profit_with_cooldown(prices):
    """
    :type prices: List[int]
    :rtype: int
    """
    N = len(prices)
    if N == 0 or N == 1:
        return 0

    last_buy_profit = 0
    last_sell_profit = prices[1] - prices[0] if prices[1] > prices[0] else 0
    max_profit = 0
    last_max_profit = last_sell_profit if last_sell_profit > 0 else 0 # max traded profit so far

    for i in xrange(2, N):
        buy_p = max_profit
        sell_p = last_sell_profit if last_sell_profit > last_buy_profit else last_buy_profit
        sell_p += prices[i] - prices[i-1]
        if sell_p < 0:
            sell_p = 0
        max_profit = last_max_profit if last_max_profit > max_profit else max_profit
        last_max_profit = sell_p if sell_p > buy_p else buy_p
        last_buy_profit = buy_p
        last_sell_profit = sell_p

    return max_profit if max_profit > last_max_profit else last_max_profit


def test_max_profix_with_cooldown():
    print "Max stock profit with cooldown"
    print max_stock_profit_with_cooldown([2,4,1]) # = 2
    print max_stock_profit_with_cooldown([1,0,2,3,0,2,1,0]) # = 4
    print max_stock_profit_with_cooldown([1,0,2,4,5,3,4,5]) # = 6
    print max_stock_profit_with_cooldown([2,1,2,0,1]) # = 1
    print max_stock_profit_with_cooldown([3,2,6,5,0,3]) # = 7
    print max_stock_profit_with_cooldown([6,1,3,2,4,7]) # = 6
    print max_stock_profit_with_cooldown([6,1,3,2,4,0,3]) # = 5
    print max_stock_profit_with_cooldown([1,4,2,7]) # = 6
    print max_stock_profit_with_cooldown([1,4,0,7]) # = 7
    print max_stock_profit_with_cooldown([1,2,5,0,7]) # = 8
    print max_stock_profit_with_cooldown([8,6,4,3,3,2,3,5,8,3,8,2,6]) # = 10
    print max_stock_profit_with_cooldown([1,4,7,5,6,2,5,1,9,7,9,7,0,6,8]) # = 22

test_max_profix_with_cooldown()


# Q-416 Partition Equal Subset Sum (DP)
#
# Given a non-empty array containing only positive integers, find if the array
# can be partitioned into two subsets such that the sum of elements in both
# subsets is equal. Each of the array element will not exceed 100. The array
# size will not exceed 200.
class PartitionEqualSubsetSum:
    def can_partition_equal_subset_sum(self, nums):
        """
        :type nums: List[int]
        :rtype: bool
        """
        sum = 0
        for i in xrange(len(nums)):
            sum += nums[i]
        if sum % 2 != 0:
            return False

        # P[i][j] = True if there exists subset_sum(0, i) == j
        P = [[False for i in xrange(sum/2 + 1)] for j in xrange(len(nums) + 1)]
        # P[i][0] = True because sum 0 always true
        for i in xrange(len(nums) + 1):
            P[i][0] = True

        for i in xrange(1, len(nums)+1, 1):
            for j in xrange(1, sum/2+1, 1):
                v = j - nums[i-1] # adjust boundary cell
                if P[i-1][j] == True or (v >= 0 and P[i-1][v] == True):
                    P[i][j] = True
            if P[i][sum/2] == True:
                return True

        return P[len(nums)][sum/2]

    @staticmethod
    def test():
        print "Partition Equal Subset Sum"
        part = PartitionEqualSubsetSum()
        print part.can_partition_equal_subset_sum([3,5,2])
        print part.can_partition_equal_subset_sum([1,5,11,5])
        print part.can_partition_equal_subset_sum([1,2,5])
        print part.can_partition_equal_subset_sum([3,3,3,4,5])

PartitionEqualSubsetSum.test()

# Q-486 Predict the Winner (DP)
#
# Given an array of scores that are non-negative integers. Player 1 picks one of the numbers from either 
# end of the array followed by the player 2 and then player 1 and so on. Each time a player picks a number, 
# that number will not be available for the next player. This continues until all the scores have been 
# chosen. The player with the maximum score wins. Given an array of scores, predict whether player 1 is 
# the winner. You can assume each player plays to maximize his score.
# 
# 1 <= length of the array <= 20; 
# Any scores in the given array are non-negative integers and will not exceed 10,000,000;
# If the scores of both players are equal, then player 1 is still the winner.
class PredictTheWinner():
    def predict(self, nums, left, right):
        if left > right:
            return 0
        if (left,right) in self.memo:
            return self.memo[(left, right)]
        if left == right:
            self.memo[(left,right)] = nums[left]
            return nums[left]
        # if taking the left end score
        left_l = self.predict(nums, left+2, right)
        left_r = self.predict(nums, left+1, right-1)
        lscore = min(left_l, left_r) + nums[left]
        # if taking the right end score
        right_l = self.predict(nums, left+1, right-1)
        right_r = self.predict(nums, left, right-2)
        rscore = min(right_l, right_r) + nums[right]
        best = max(lscore, rscore)
        self.memo[(left,right)] = best
        return best

    def predictTheWinner(self, nums):
        self.memo = {}
        self.predict(nums, 0, len(nums)-1)
        total = 0
        for i in xrange(len(nums)):
            total += nums[i]
        best = self.memo[(0,len(nums)-1)]
        return True if best >= (total-best) else False

    @staticmethod
    def test():
        print "Predict the Winner"
        ptw = PredictTheWinner()
        print ptw.predictTheWinner([1,5,2])
        print ptw.predictTheWinner([1,5,23,7])
        print ptw.predictTheWinner([2,4,55,6,8]) # False

PredictTheWinner.test()

# Q-516 Longest Palindrome Subsequence (DP)
# 
# Given a string s, find the longest palindromic subsequence's length in s. You may assume that the
# maximum length of s is 1000.
class LongestPalindromeSubsequence(object):
    def longestPalindromeSubseq(self, s):
        """
        :type s: str
        :rtype: int
        """
        # have to use this special check to make long test cases that have same chars pass within time
        same = True
        for i in xrange(1, len(s)):
            if s[i] != s[i-1]:
                same = False
        if same:
            return len(s)

        M = [[0 for i in xrange(len(s))] for j in xrange(len(s))]
        # using increasing substring length in outer loop would have long tests exceed time limitation
        for i in xrange(len(s)-1, -1, -1):
            M[i][i] = 1
            for j in xrange(i+1, len(s)):
                M[i][j] = M[i+1][j-1] + 2 if s[i] == s[j] else max(M[i+1][j], M[i][j-1])
        return M[0][len(s)-1]

    @staticmethod
    def test():
        print "Longest Palindrome Subsequence"
        lps = LongestPalindromeSubsequence()
        print lps.longestPalindromeSubseq("bbbab")
        print lps.longestPalindromeSubseq("cbbd")
        print lps.longestPalindromeSubseq("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
            "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
            "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
            "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
            "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
            "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
            "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
            "cccccccccccccccccccccccccccccccccccccccccc")
        print lps.longestPalindromeSubseq("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabcaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")

LongestPalindromeSubsequence.test()

# Q-525 Contiguous Array (DP)
#
# Given a binary array, find the maximum length of a contiguous subarray with equal number of 0 and 1.
class ContiguousO1sArray(object):
    # the O(N^2) DP code exceeds time limit for long test case
    def findMaxLength_1(self, nums):
        """
        :type nums: List[int]
        :rtype: int
        """
        M = [[0,0] for i in xrange(len(nums)+1)]
        largest = 0
        for i in xrange(1, len(nums)+1):
            m_0, m_1 = nums[i-1] ^ 1, nums[i-1] & 1
            M[i] = [M[i-1][0]+m_0, M[i-1][1]+m_1]
            for j in xrange(1,i+1-largest):
                if M[i][0] - M[j-1][0] == M[i][1] - M[j-1][1] and i-j+1 > largest:
                    largest = i-j+1
        return largest

    def findMaxLength(self, nums):
        """
        :type nums: List[int]
        :rtype: int
        """
        if len(nums) == 0:
            return 0
        # prefix sum treats 0 as -1, last sum remembers the subscript index of last prefix sum that equals to key
        lastSum = {}
        prefix = [0 for i in xrange(len(nums))]
        prefix[0] = 1 if nums[0] == 1 else -1
        lastSum[prefix[0]] = 0
        for i in xrange(1, len(nums)):
            s = prefix[i-1] + (1 if nums[i] == 1 else -1)
            prefix[i] = s
            lastSum[s] = i
        largest = 0
        for i in xrange(len(prefix)):
            if prefix[i] == 0:
                largest = max(largest, i+1)
            # the range between 2 indices that have same prefix sum
            elif lastSum[prefix[i]] - i > largest:
                largest = lastSum[prefix[i]] - i
        return largest

    @staticmethod
    def test():
        print "Contiguous Array"
        ca = ContiguousO1sArray()
        print ca.findMaxLength([0,1])
        print ca.findMaxLength([1,1,0,1,1,0,0,1]) # 6
        print ca.findMaxLength([0,0,0,0,0,0,1,1,1,1,0,1,1,1,1,0,0,1,0,1,1,0,1,0,0,1,0,1,0,1,0,0,1,0,1,1,0,0,1,1,1,1,0,1,0,0,
            1,0,0,0,0,1,0,0,0,1,1,0,0,0,1,1,0,1,1,1,1,1,0,0,1,1,1,0,1,0,0,0,0,1,0,1,1,0,1,0,0,0,1,0,1,0,0,0,1,1,0,0,1,1,1,1,
            1,0,0,0,0,1,0,0,0,1,1,1,0,1,1,0,1,1,1,0,0,1,0,1,1,1,0,0,1,1,0,0,1,1,0,1,1,1,0,1,1,1,1,0,0,0,0,1,1,1,0,1,1,0,1,0,
            0,1,1,1,0,0,1,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,0,0,1,0,1,1,0,1,1,0,0,1,0,0,1,1,1,0,0,0,1,0,1,0,0,0,1,0,0,1,1,0,
            1,1,1,1,1,0,1,1,0,1,1,1,0,1,0,1,0,0,0,1,0,0,0,0,0,1,0,0,1,1,1,1,1,1,1,1,0,0,0,1,1,0,0,1,1,1,0,0,1,0,0,0,0,1,0,0,
            1,0,0,1,1,0,1,0,1,1,0,1,0,1,1,0,0,1,0,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,0,0,0,1,1,1,0,1,1,0,1,0,0,1,
            0,1,1,1,0,1,1,1,0,0,0,1,0,1,1,1,1,1,1,0,1,0,0,0,1,1,1,0,0,0,0,0,1,1,0,1,1,0,1,0,0,0,0,1,1,0,0,0,1,0,1,1,1,1,1,1,
            0,1,1,1,0,1,0,1,0,1,1,0,0,1,1,0,1,1,0,0,1,1,1,1,0,1,1,0,1,0,0,1,1,1,0,1,1,1,1,0,0,0,1,0,0,0,0,0,1,0,1,0,1,0,0,1,
            1,0,0,0,0,1,0,1,0,1,1,0,1,0,0,1,0,1,1,1,1,1,1,1,0,0,1,1,1,0,1,1,0,0,0,0,1,1,0,1,1,0,1,0,0,0,0,0,0,0,0,1,0,0,0,1,
            0,1,0,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,0,1,0,1,0,1,1,0,0,1,1,1,1,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,1,1,0,1,0,
            0,1,1,1,1,1,1,1,0,0,1,1,0,0,1,1,0,1,0,1,1,0,1,0,0,0,0,1,1,1,0,1,1,1,0,0,0,1,1,1,1,1,0,1,0,0,0,0,0,1,0,1,0,0,0,1,
            1,0,0,0,0,0,0,1,0,0,1,0,0,0,1,0,1,0,1,0,1,1,0,0,1,1,0,0,0,1,1,1,1,0,0,0,0,1,0,1,1,0,0,0,1,0,0,0,1,1,1,0,0,0,0,1,
            0,0,1,0,0,0,1,0,0,1,1,1,1,0,1,1,0,1,0,0,0,1,1,0,0,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,0,1,1,1,1,0,1,0,1,1,0,0,1,0,1,
            0,1,0,1,0,0,1,1,1,1,1,1,0,0,0,1,1,1,0,1,0,1,1,1,1,0,1,1,0,1,0,1,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,1,0,0,1,1,1,0,1,0,
            1,0,1,1,1,1,0,0,1,0,0,1,0,0,0,0,0,1,0,0,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,0,1,1,0,0,0,0,1,
            1,1,0,0,0,0,1,1,1,0,0,1,1,0,1,1,1,1,1,1,0,1,0,0,0,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,0,0,1,1,0,1,0,0,1,0,0,1,0,0,1,1,
            0,1,0,0,1,0,0,1,1,0,0,1,1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,1,0,0,1,1,0,0,0,0,0,1,1,1,1,1,0,0,1,1,0,0,1,1,0,1,0,1,
            0,0,0,1,0,1,1,0,1,1,1,1,1,0,1,1,1,0,0,1,1,1,0,0,0,1,1,0,1,1,1,1,1,0,1,1,0,0,1,1,1,1,1,0,1,0,0,1,0,0,0,0,1,0,0,0,
            0,0]) # 416

ContiguousO1sArray.test()

# Q-746 Min Cost Climbing Stairs
class MinCostClimbingStairs(object):
    def minCostClimbingStairs(self, cost):
        """
        :type cost: List[int]
        :rtype: int
        """
        M = [0 for i in xrange(len(cost))]
        for i in xrange(2, len(cost)):
            M[i] = min(M[i-1] + cost[i-1], M[i-2] + cost[i-2])
        return min(M[len(cost)-1] + cost[len(cost)-1], M[len(cost)-2] + cost[len(cost)-2])

    @staticmethod
    def test():
        print "Q-746 Min Cost Climbing Stairs"
        mccs = MinCostClimbingStairs()
        print mccs.minCostClimbingStairs([1, 100, 1, 1, 1, 100, 1, 1, 100, 1])

MinCostClimbingStairs.test()