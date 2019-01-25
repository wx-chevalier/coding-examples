package storage

import mgo "gopkg.in/mgo.v2"

type MongoStorage struct {
	*mgo.Session
	DB         string
	Collection string
}

func NewMongoStorage(connection, db, collection string)
(*MongoStorage, error) {
	session, err := mgo.Dial("localhost")
	if err != nil {
		return nil, err
	}
	ms := MongoStorage{
		Session:    session,
		DB:         db,
		Collection: collection,
	}
	return &ms, nil
}
