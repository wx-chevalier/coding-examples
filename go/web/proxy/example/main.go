package main

import (
	"fmt"
	"net/http"

	"github.com/agtorre/go-solutions/section7/proxy"
)

func main() {
	p := &proxy.Proxy{
		Client:  http.DefaultClient,
		BaseURL: "https://www.golang.org",
	}
	http.Handle("/", p)
	fmt.Println("Listening on port :3333")
	err := http.ListenAndServe(":3333", nil)
	panic(err)
}
