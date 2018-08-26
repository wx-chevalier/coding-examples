package main

import (
	"sync"
)

var items []string
var mu *sync.RWMutex

func init() {
	mu = &sync.RWMutex{}
}

func AddItem(item string) {
	mu.Lock()
	items = append(items, item)
	mu.Unlock()
}

func ReadItems() []string {
	mu.RLock()
	defer mu.RUnlock()
	return items
}
