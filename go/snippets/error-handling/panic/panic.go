package panic

import (
	"fmt"
	"strconv"
)

func Panic() {
	zero, err := strconv.ParseInt("0", 10, 64)
	if err != nil {
		panic(err)
	}

	a := 1 / zero
	fmt.Println("we'll never get here", a)
}

func Catcher() {
	defer func() {
		if r := recover(); r != nil {
			fmt.Println("panic occurred:", r)
		}
	}()
	Panic()
}
