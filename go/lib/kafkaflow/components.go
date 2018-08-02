package kafkaflow

import (
	"fmt"
	"strings"

	flow "github.com/trustmaster/goflow"
)

type Upper struct {
	flow.Component
	Val <-chan string
	Res chan<- string
}

func (e *Upper) OnVal(val string) {
	e.Res <- strings.ToUpper(val)
}

type Printer struct {
	flow.Component
	Line <-chan string
}

func (p *Printer) OnLine(line string) {
	fmt.Println(line)
}
