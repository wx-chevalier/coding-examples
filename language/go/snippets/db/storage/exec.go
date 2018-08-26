package storage

import (
	"context"
	"fmt"
)

func Exec() error {
	m, err := NewMongoStorage("localhost", "gosolutions", "items")
	if err != nil {
		return err
	}
	if err := PerformOperations(m); err != nil {
		return err
	}

	if err := m.Session.DB(m.DB).C(m.Collection).DropCollection(); err != nil {
		return err
	}

	return nil
}

func PerformOperations(s Storage) error {
	ctx := context.Background()
	i := Item{Name: "candles", Price: 100}
	if err := s.Put(ctx, &i); err != nil {
		return err
	}

	candles, err := s.GetByName(ctx, "candles")
	if err != nil {
		return err
	}
	fmt.Printf("Result: %#v\n", candles)
	return nil
}
