package rest

import "net/http"

type APIClient struct {
	*http.Client
}

func NewAPIClient(username, password string) *APIClient {
	t := http.Transport{}
	return &APIClient{
		Client: &http.Client{
			Transport: &APITransport{
				Transport: &t,
				username:  username,
				password:  password,
			},
		},
	}
}

func (c *APIClient) GetGoogle() (int, error) {
	resp, err := c.Get("http://www.google.com")
	if err != nil {
		return 0, err
	}
	return resp.StatusCode, nil
}
