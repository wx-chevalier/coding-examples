package main

import (
	"fmt"
	"net/http"

	"github.com/agtorre/go-solutions/section7/handlers"
)

func main() {
	http.HandleFunc("/name", handlers.HelloHandler)
	http.HandleFunc("/greeting", handlers.GreetingHandler)
	fmt.Println("Listening on port :3333")
	err := http.ListenAndServe(":3333", nil)
	panic(err)
}
