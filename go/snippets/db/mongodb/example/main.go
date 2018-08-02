package main

import "github.com/agtorre/go-solutions/section5/mongodb"

func main() {
	if err := mongodb.Exec(); err != nil {
		panic(err)
	}
}
