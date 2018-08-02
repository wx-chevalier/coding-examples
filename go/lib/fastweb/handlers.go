package main

import (
	"encoding/json"

	"github.com/valyala/fasthttp"
)

func GetItems(ctx *fasthttp.RequestCtx) {
	enc := json.NewEncoder(ctx)
	items := ReadItems()
	enc.Encode(&items)
	ctx.SetStatusCode(fasthttp.StatusOK)
}

func AddItems(ctx *fasthttp.RequestCtx) {
	item, ok := ctx.UserValue("item").(string)
	if !ok {
		ctx.SetStatusCode(fasthttp.StatusBadRequest)
	}
	AddItem(item)
	ctx.SetStatusCode(fasthttp.StatusOK)
}
