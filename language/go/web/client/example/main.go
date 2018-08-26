package main

import "github.com/agtorre/go-solutions/section6/client"

func main() {
	cli := client.Setup(true, false)

	if err := client.DefaultGetGolang(); err != nil {
		panic(err)
	}
	if err := client.DoOps(cli); err != nil {
		panic(err)
	}
	c := client.Controller{Client: cli}
	if err := c.DoOps(); err != nil {
		panic(err)
	}
	client.Setup(true, true)
	if err := client.DefaultGetGolang(); err != nil {
		panic(err)
	}
}
