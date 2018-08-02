package goflow

import (
	"encoding/base64"
	"fmt"

	flow "github.com/trustmaster/goflow"
)

type Encoder struct {
	flow.Component
	Val <-chan string
	Res chan<- string
}

func (e *Encoder) OnVal(val string) {
	encoded := base64.StdEncoding.EncodeToString([]byte(val))
	e.Res <- fmt.Sprintf("%s => %s", val, encoded)
}

type Printer struct {
	flow.Component
	Line <-chan string
}

func (p *Printer) OnLine(line string) {
	fmt.Println(line)
}
