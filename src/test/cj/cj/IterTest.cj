package cj

class IterTest {
    def testFilter() {
        {
            val list = [1, 2, 3, 4, 5, 6].iter().filter(x -> x % 2 == 0).toList()
            Assert.equal(list, [2, 4, 6])
        }
    }

    def testForWithTupleAssignment() {
        {
            var ret = @[Tuple[Int, Int]]
            for (a, b) in [(1, 2), (3, 4)] {
                ret.add((b, a))
            }
            Assert.equal(ret, @[(2, 1), (4, 3)])
        }
    }

    def testIterNext() {
        {
            val iter = [7, 8, 9].iter()
            val out = @[Int]
            for opt = iter.next(); opt.isPresent(); opt = iter.next() {
                out.add(opt.get())
            }
            Assert.equal(out, @[7, 8, 9])
        }
        {
            # NOTE: this portion of the test is dependent on how the
            # current hash/Set/MutableMap works
            val iter = Set.of([1, 2, 3]).iter()
            val out = @[Int]
            for opt = iter.next(); opt.isPresent(); opt = iter.next() {
                out.add(opt.get())
            }
            Assert.equal(out, @[1, 2, 3])
        }
    }

    def testToSetAndMap() {
        {
            val set = [8, 10, 9].iter().toSet()
            Assert.equal(set.size(), 3)
            Assert.equal(List.sorted(set), [8, 9, 10])
        }
        {
            val map = [(3, 4), (1, 2)].iter().toMap()
            Assert.equal(map.size(), 2)
            Assert.equal(List.sorted(map.pairs()), [(1, 2), (3, 4)])
        }
    }
}
