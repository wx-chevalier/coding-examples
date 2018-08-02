/*
	本文件描述了 Otto 的使用实例
*/
package main

import (
	"github.com/robertkrimen/otto"
)

func main() {
	vm := otto.New()
	vm.Run(`
		abc = 2 + 2;
		console.log("The value of abc is " + abc); // 4
	`)
}
