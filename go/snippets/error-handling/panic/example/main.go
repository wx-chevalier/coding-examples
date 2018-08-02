package main

import (
	"fmt"

	"github.com/agtorre/go-solutions/section4/panic"
)

func main() {
	fmt.Println("before panic")
	panic.Catcher()
	fmt.Println("after panic")
}
