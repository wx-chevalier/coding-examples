package main

import "github.com/agtorre/go-solutions/section5/storage"

func main() {
	if err := storage.Exec(); err != nil {
		panic(err)
	}
}
