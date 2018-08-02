package context

import (
	"context"
	"os"

	"github.com/apex/log"
	"github.com/apex/log/handlers/text"
)

func Initialize() {
	log.SetHandler(text.New(os.Stdout))
	ctx := context.Background()
	ctx, e := FromContext(ctx, log.Log)

	ctx = WithField(ctx, "id", "123")
	e.Info("starting")
	gatherName(ctx)
	e.Info("after gatherName")
	gatherLocation(ctx)
	e.Info("after gatherLocation")
}

func gatherName(ctx context.Context) {
	ctx = WithField(ctx, "name", "Go Solutions")
}

func gatherLocation(ctx context.Context) {
	ctx = WithFields(ctx, log.Fields{"city": "Seattle", "state": "WA"})
}
