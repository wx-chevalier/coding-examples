package main

import "github.com/agtorre/go-solutions/section5/pools"

func main() {
	if err := pools.ExecWithTimeout(); err != nil {
		panic(err)
	}
}
