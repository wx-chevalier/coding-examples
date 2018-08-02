package kafkaflow

import flow "github.com/trustmaster/goflow"

type UpperApp struct {
	flow.Graph
}

func NewUpperApp() *UpperApp {
	u := &UpperApp{}
	u.InitGraphState()

	u.Add(&Upper{}, "upper")
	u.Add(&Printer{}, "printer")

	u.Connect("upper", "Res", "printer", "Line")
	u.MapInPort("In", "upper", "Val")
	return u
}
