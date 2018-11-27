package main

import (
	"fmt"
	"os/exec"
	"strings"
	"sync"
)

func exeCmd(cmd string, wg *sync.WaitGroup) {
	fmt.Println("command is ", cmd)
	// splitting head => g++ parts => rest of the command
	parts := strings.Fields(cmd)
	head := parts[0]
	parts = parts[1:len(parts)]

	out, err := exec.Command(head, parts...).Output()
	if err != nil {
		fmt.Printf("%s", err)
	}
	fmt.Printf("%s", out)
	wg.Done() // Need to signal to waitgroup that this goroutine is done
}

func main() {
	wg := new(sync.WaitGroup)
	commands := []string{"echo newline >> foo.o", "echo newline >> f1.o", "echo newline >> f2.o"}
	for _, str := range commands {
		wg.Add(1)
		go exeCmd(str, wg)
	}
	wg.Wait()
}
