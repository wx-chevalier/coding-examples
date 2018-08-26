package dataconv

import "fmt"

func ShowConv() {
	var a = 24
	var b = 2.0

	c := float64(a) * b
	fmt.Println(c)

	precision := fmt.Sprintf("%.2f", b)
	fmt.Printf("%s - %T\n", precision, precision)
}
