package main

import (
	"fmt"

	"github.com/agtorre/go-solutions/section3/collections"
)

func main() {
	ws := []collections.WorkWith{
		collections.WorkWith{"Example", 1},
		collections.WorkWith{"Example 2", 2},
	}

	fmt.Printf("Initial list: %#v\n", ws)

	ws = collections.Map(ws, collections.LowerCaseData)
	fmt.Printf("After LowerCaseData Map: %#v\n", ws)

	ws = collections.Map(ws, collections.IncrementVersion)
	fmt.Printf("After IncrementVersion Map: %#v\n", ws)

	ws = collections.Filter(ws, collections.OldVersion(3))
	fmt.Printf("After OldVersion Filter: %#v\n", ws)
}
