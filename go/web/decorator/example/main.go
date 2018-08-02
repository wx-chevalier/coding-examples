package main

import "github.com/agtorre/go-solutions/section6/decorator"

func main() {
	if err := decorator.Exec(); err != nil {
		panic(err)
	}
}
