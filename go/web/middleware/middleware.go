package middleware

import (
	"log"
	"net/http"
	"time"
)

type Middleware func(http.HandlerFunc) http.HandlerFunc

func ApplyMiddleware(h http.HandlerFunc, middleware ...Middleware) http.HandlerFunc {
	applied := h
	for _, m := range middleware {
		applied = m(applied)
	}
	return applied
}

func Logger(l *log.Logger) Middleware {
	return func(next http.HandlerFunc) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			start := time.Now()
			l.Printf("started request to %s with id %s", r.URL, GetID(r.Context()))
			next(w, r)
			l.Printf("completed request to %s with id %s in %s", r.URL, GetID(r.Context()), time.Since(start))
		}
	}
}
