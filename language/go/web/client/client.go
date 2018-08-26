package client

import (
	"crypto/tls"
	"net/http"
)

func Setup(isSecure, nop bool) *http.Client {
	c := http.DefaultClient

	if !isSecure {
		c.Transport = &http.Transport{
			TLSClientConfig: &tls.Config{
				InsecureSkipVerify: false,
			},
		}
	}
	if nop {
		c.Transport = &NopTransport{}
	}
	http.DefaultClient = c
	return c
}

type NopTransport struct {
}

func (n *NopTransport) RoundTrip(*http.Request) (*http.Response, error) {
	return &http.Response{StatusCode: http.StatusTeapot}, nil
}
