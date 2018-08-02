package decorator

import "net/http"

type TransportFunc func(*http.Request) (*http.Response, error)

func (tf TransportFunc) RoundTrip(r *http.Request) (*http.Response, error) {
	return tf(r)
}

type Decorator func(http.RoundTripper) http.RoundTripper

func Decorate(t http.RoundTripper, rts ...Decorator) http.RoundTripper {
	decorated := t
	for _, rt := range rts {
		decorated = rt(decorated)
	}
	return decorated
}
