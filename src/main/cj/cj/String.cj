package cj

native class String: Eq, Ord, Hash {
    def __eq(a: Self, b: Self) : Bool
    def __lt(a: Self, b: Self) : Bool

    def __add(a: Self, b: Self) : Self

    def hash(x: Self) : Int
}