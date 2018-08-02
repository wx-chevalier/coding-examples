package math

import "math/big"

var memoize map[int]*big.Int

func init() {
	memoize = make(map[int]*big.Int)
}

func Fib(n int) *big.Int {
	if n < 0 {
		return nil
	}

	if n < 2 {
		memoize[n] = big.NewInt(1)
	}

	if val, ok := memoize[n]; ok {
		return val
	}

	memoize[n] = big.NewInt(0)
	memoize[n].Add(memoize[n], Fib(n-1))
	memoize[n].Add(memoize[n], Fib(n-2))

	return memoize[n]
}
