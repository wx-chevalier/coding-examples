package goflow

import flow "github.com/trustmaster/goflow"

type EncodingApp struct {
	flow.Graph
}

func NewEncodingApp() *EncodingApp {
	e := &EncodingApp{}
	e.InitGraphState()

	e.Add(&Encoder{}, "encoder")
	e.Add(&Printer{}, "printer")

	e.Connect("encoder", "Res", "printer", "Line")
	e.MapInPort("In", "encoder", "Val")
	return e
}
