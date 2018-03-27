package main

import "fmt"
import "github.com/wxyyxc1992/coding-snippets/go/snippets/package/math"

func main() {
	xs := []float64{1, 2, 3, 4}
	avg := math.Average(xs)
	fmt.Println(avg)
}
