package validation

type Controller struct {
	ValidatePayload func(p *Payload) error
}

func New() *Controller {
	c := Controller{
		ValidatePayload: ValidatePayload,
	}
	return &c
}
