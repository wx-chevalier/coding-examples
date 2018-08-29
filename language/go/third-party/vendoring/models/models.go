package models

import "sync/atomic"

type DB interface {
	GetScore() (int64, error)
	SetScore(int64) error
}

func NewDB() DB {
	return &db{0}
}

type db struct {
	score int64
}

func (d *db) GetScore() (int64, error) {
	return atomic.LoadInt64(&d.score), nil
}

func (d *db) SetScore(score int64) error {
	atomic.StoreInt64(&d.score, score)
	return nil
}
