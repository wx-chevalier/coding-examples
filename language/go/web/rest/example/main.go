package main

import "github.com/agtorre/go-solutions/section6/rest"

func main() {
	if err := rest.Exec(); err != nil {
		panic(err)
	}
}
