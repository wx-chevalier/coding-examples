package proxy

import (
	"log"
	"net/http"
)

type Proxy struct {
	Client  *http.Client
	BaseURL string
}

func (p *Proxy) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	if err := p.ProcessRequest(r); err != nil {
		log.Printf("error occurred during process request: %s", err.Error())
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	resp, err := p.Client.Do(r)
	if err != nil {
		log.Printf("error occurred during client operation: %s", err.Error())
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	defer resp.Body.Close()
	CopyResponse(w, resp)
}
