package main

import (
	"fmt"
	"github.com/astaxie/beego"
)

func main() {

	ch := make(chan string)

	go func() {
		fmt.Println("Beego...")
		beego.Run()

	}()

	fmt.Println(<-ch)
}
