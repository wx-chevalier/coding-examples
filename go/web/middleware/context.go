package middleware

import (
	"golang.org/x/net/context"
	"net/http"
	"strconv"
)

type ContextID int

const ID ContextID = 0

func SetID(start int64) Middleware {
	return func(next http.HandlerFunc) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			ctx := context.WithValue(r.Context(), ID, strconv.FormatInt(start, 10))
			start++
			r = r.WithContext(ctx)
			next(w, r)
		}
	}
}

func GetID(ctx context.Context) string {
	if val, ok := ctx.Value(ID).(string); ok {
		return val
	}
	return ""
}
