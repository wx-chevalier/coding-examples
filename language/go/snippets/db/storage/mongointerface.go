package storage

import (
	"context"

	"gopkg.in/mgo.v2/bson"
)

func (m *MongoStorage) GetByName(ctx context.Context, name string)
 (*Item, error) {
	c := m.Session.DB(m.DB).C(m.Collection)
	var i Item
	if err := c.Find(bson.M{"name": name}).One(&i); err != nil {
		return nil, err
	}
	return &i, nil
}

func (m *MongoStorage) Put(ctx context.Context, i *Item) error {
	c := m.Session.DB(m.DB).C(m.Collection)
	return c.Insert(i)
}
