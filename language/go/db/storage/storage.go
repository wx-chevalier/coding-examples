package storage

import "context"

type Item struct {
	Name  string
	Price int64
}

type Storage interface {
	GetByName(context.Context, string) (*Item, error)
	Put(context.Context, *Item) error
}
