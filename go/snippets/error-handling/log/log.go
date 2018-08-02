package log

import (
	"bytes"
	"fmt"
	"log"
)

func Log() {
	buf := bytes.Buffer{}

	logger := log.New(&buf, "logger: ", log.Lshortfile|log.Ldate)

	logger.Println("test")
	logger.SetPrefix("new logger: ")
	logger.Printf("you can also add args(%v) and use Fataln to log and crash",
	true)
	fmt.Println(buf.String())
}
