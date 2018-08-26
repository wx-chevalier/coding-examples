package rest

import "net/http"

type APITransport struct {
	*http.Transport
	username, password string
}

func (t *APITransport) RoundTrip(req *http.Request) (*http.Response, error) {
	req.SetBasicAuth(t.username, t.password)
	return t.Transport.RoundTrip(req)
}
