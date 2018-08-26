package controllers

type Storage interface {
	Get() string
	Put(string)
}

type MemStorage struct {
	value string
}

func (m *MemStorage) Get() string {
	return m.value
}

func (m *MemStorage) Put(s string) {
	m.value = s
}
