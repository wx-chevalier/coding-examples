package math

import (
	"fmt"
	"math"
)

func Examples() {
	i := 25

	result := math.Sqrt(float64(i))

	fmt.Println(result)

	result = math.Ceil(9.5)
	fmt.Println(result)

	result = math.Floor(9.5)
	fmt.Println(result)
	fmt.Println("Pi:", math.Pi, "E:", math.E)
}
