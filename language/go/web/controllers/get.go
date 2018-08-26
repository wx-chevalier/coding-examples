package controllers

import (
	"encoding/json"
	"net/http"
)

func (c *Controller) GetValue(UseDefault bool) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		if r.Method != "GET" {
			w.WriteHeader(http.StatusMethodNotAllowed)
			return
		}
		value := "default"
		if !UseDefault {
			value = c.storage.Get()
		}
		p := Payload{Value: value}
		w.WriteHeader(http.StatusOK)
		if payload, err := json.Marshal(p); err == nil {
			w.Write(payload)
		}
	}
}
