import unittest

class Directory(object):
    """
    Simulate "cd" command with the following requirements
    1, home directory ~
    2, softlinked directory resolution
    3, matching the longest softlink pattern first
    4, cycle detection in softlink dictionary
    """
    def __init__(self, home: str):
        self.home = home 
        self.root = "/"
        self.softlinks = {}
        self.resolve_longest_match: bool = False

    def add_softlinks(self, softlinks: dict[str, str]):    
        if not self._check_cycle(softlinks):
            self.softlinks = softlinks
        else:
            print("Softlink dictionary invalid with cycle, not added")
    
    def set_prefer_longest(self, prefer: bool):
        self.resolve_longest_match = prefer

    def _collapse_dir(self, dir_path: str) -> list[str]:
        paths = dir_path.split("/")
        collapsed = []
        for pdir in paths:
            if pdir == "..":
                if (len(collapsed) > 0):
                    collapsed.pop() 
            elif pdir == "" or pdir == "." or pdir == "/":
                continue
            elif pdir == "~":
                # not expect "~" in the middle of dir path
                raise Exception("bad path")
            else:
                collapsed.append(pdir)
        return collapsed

    def cd_bad(self, curr_dir: str, new_dir: str) -> str:    
        # not working with many ".." in new_dir
        curr_dir2 = self._collapse_dir(curr_dir)
        new_dir2 = self._collapse_dir(new_dir)
        print(f"curr_dir2 {curr_dir2}, new_dir2 {new_dir2}")
        return self.root + "/".join(curr_dir2 + new_dir2)
    
    def cd(self, curr_dir: str, new_dir: str) -> str:
        if new_dir.startswith("~"):
            # directly change to home
            return self.cd_home(new_dir)
        
        if curr_dir.startswith("~"):
            curr_dir = self.expand_home(curr_dir)

        concat_dir = curr_dir + "/" + new_dir
        paths = self._collapse_dir(concat_dir)
        print(f"concat_dir {paths}");
        abs_dir = self.root + "/".join(paths)
        if (len(self.softlinks) > 0):
            return self._resolve_softlink(abs_dir, self.resolve_longest_match)
        return abs_dir

    def cd_home(self, new_dir: str) -> str:
        if new_dir.startswith("~"):
            new_dir = self.home + new_dir[1:]
            print(f"new_home_dir {new_dir}")
        paths = self._collapse_dir(new_dir)
        return self.root + "/".join(paths)
    
    def expand_home(self, dir: str) -> str:
        if dir.startswith("~"):
            dir = self.home + dir[1:]
        return dir
    
    def _resolve_link(self, dir: str) -> str:
        for k,v in self.softlinks.items():
            if dir.startswith(k):
                new_p = v + dir[len(k):]
                print(f"resolve {dir} to {new_p}")
                return new_p
        return dir

    def _resolve_link2(self, dir: str) -> str:
        # sort keys by length descending
        keys = [k for k in self.softlinks.keys()]
        sorted_keys = sorted(keys, key=lambda x: -len(x))
        for k in sorted_keys:
            if dir.startswith(k):
                v = self.softlinks[k]
                new_p = v + dir[len(k):]
                print(f"resolve {dir} to {new_p}")
                return new_p
        return dir

    def _resolve_softlink(self, dir: str, perfer_longest: bool = False) -> str:
        done = False
        while not done:
            new_dir = self._resolve_link2(dir) if perfer_longest else self._resolve_link(dir)
            if new_dir == dir:
                done = True
            else:
                dir = new_dir
        return dir

    # graph cycle detection with DFS
    def _check_cycle(self, softlinks: dict[str, str]) -> bool:        
        # graph adjacency list
        adj: dict[str, list[str]] = {k: [] for k in softlinks.keys()}
        for k,v in softlinks.items():
            adj[k].append(v)

        visited = set()
        onstack = set()

        def dfs(node: str) -> bool:
            visited.add(node)
            onstack.add(node)
            # default [] for leaf node
            for c in adj.get(node, []):
                if c not in visited:
                    if dfs(c) == True:
                        return True
                elif c in onstack:
                    # backedge found
                    return True
            onstack.remove(node)
            return False

        for n in adj.keys():
            if n not in visited:
                if dfs(n):
                    print(f"Softlink dictionary invalid with cycle")
                    return True
        return False
    
class DictionaryTest(unittest.TestCase):
    def testBaisc(self):
        dir = Directory("/Users/liang")
        self.assertEqual(dir.cd("/foo/bar", "baz"), "/foo/bar/baz")
        self.assertEqual(dir.cd("/foo/..", "/baz"), "/baz")
        self.assertEqual(dir.cd("/", "/foo/bar/./../baz"), "/foo/baz")
        self.assertEqual(dir.cd("/", "/foo/bar/../../baz"), "/baz")
        self.assertEqual(dir.cd("/../..", "/foo/bar/./../baz"), "/foo/baz")
        self.assertEqual(dir.cd("/foo/bar", "./"), "/foo/bar")

        print("-- Test overflow \"..\"")
        self.assertEqual(dir.cd("/foo/bar", ".."), "/foo")    
        self.assertEqual(dir.cd("/foo/bar/../../..", "foo"), "/foo")
        self.assertEqual(dir.cd("/foo", "../../../.."), "/")

        print("-- Test home directory handling")
        self.assertEqual(dir.cd("/foo", "~/baz"), "/Users/liang/baz")
        self.assertEqual(dir.cd("~", "baz"), "/Users/liang/baz")

    def testSoftLink(self):
        print("-- Test softlink")
        dir = Directory("/Users/liang")        
        dir.add_softlinks({"/foo/bar": "/abc"})
        self.assertEqual(dir.cd("/foo/bar", "baz"), "/abc/baz")

        dir.add_softlinks({"/foo/bar": "/abc", "/abc": "/bcd"})
        self.assertEqual(dir.cd("/foo/bar", "baz"), "/bcd/baz")

        dir.add_softlinks({"/foo/bar": "/abc", "/abc": "/bcd", "/bcd/baz": "/xyz"})
        self.assertEqual(dir.cd("/foo/bar", "baz"), "/xyz")

        print(f"-- Test cycle in softlinks")
        dir.add_softlinks({"/foo/bar": "/abc", "/abc": "/bcd", "/bcd": "/foo/bar"})

        print(f"-- Test resolve to longest matching softlink")
        dir.add_softlinks({"/foo/bar": "/abc", "/foo/bar/baz": "/xyz"})
        dir.set_prefer_longest(True)
        self.assertEqual(dir.cd("/foo/bar", "baz"), "/xyz")

if __name__ == '__main__':
    unittest.main()