package proxy

import (
	"bytes"
	"net/http"
	"net/url"
)

func (p *Proxy) ProcessRequest(r *http.Request) error {
	proxyURLRaw := p.BaseURL + r.URL.String()

	proxyURL, err := url.Parse(proxyURLRaw)
	if err != nil {
		return err
	}
	r.URL = proxyURL
	r.Host = proxyURL.Host
	r.RequestURI = ""
	return nil
}

func CopyResponse(w http.ResponseWriter, resp *http.Response) {
	var out bytes.Buffer
	out.ReadFrom(resp.Body)

	for key, values := range resp.Header {
		for _, value := range values {
			w.Header().Add(key, value)
		}
	}

	w.WriteHeader(resp.StatusCode)
	w.Write(out.Bytes())
}
