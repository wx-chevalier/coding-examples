package main

import "github.com/agtorre/go-solutions/section4/global"

func main() {
	if err := global.UseLog(); err != nil {
		panic(err)
	}
}
