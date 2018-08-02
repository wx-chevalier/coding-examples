package main

import (
	"fmt"
	"log"
	"net/http"
	"os"

	"github.com/agtorre/go-solutions/section7/middleware"
)

func main() {
	h := middleware.ApplyMiddleware(
		middleware.Handler,
		middleware.Logger(log.New(os.Stdout, "", 0)),
		middleware.SetID(100),
	)
	http.HandleFunc("/", h)
	fmt.Println("Listening on port :3333")
	err := http.ListenAndServe(":3333", nil)
	panic(err)
}
