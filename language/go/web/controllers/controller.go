package controllers

type Controller struct {
	storage Storage
}

func New(storage Storage) *Controller {
	c := Controller{
		storage: storage,
	}
	return &c
}

type Payload struct {
	Value string `json:"value"`
}
