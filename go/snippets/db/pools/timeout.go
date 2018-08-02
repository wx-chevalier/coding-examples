package pools

import (
	"context"
	"time"
)

func ExecWithTimeout() error {
	db, err := Setup()
	if err != nil {
		return err
	}

	ctx := context.Background()
	ctx, can := context.WithDeadline(ctx, time.Now())

	defer can()

	_, err = db.BeginTx(ctx, nil)
	return err
}
