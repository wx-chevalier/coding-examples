package client

import (
	"fmt"
	"net/http"
)

type Controller struct {
	*http.Client
}

func (c *Controller) DoOps() error {
	resp, err := c.Client.Get("http://www.google.com")
	if err != nil {
		return err
	}
	fmt.Println("results of client.DoOps", resp.StatusCode)
	return nil
}
