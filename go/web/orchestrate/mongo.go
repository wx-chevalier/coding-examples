package orchestrate

import (
	"fmt"

	mgo "gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

type State struct {
	Name       string `bson:"name"`
	Population int    `bson:"pop"`
}

func ConnectAndQuery(session *mgo.Session) error {
	conn := session.DB("gosolutions").C("example")

	if err := conn.Insert(&State{"Washington", 7062000}, &State{"Oregon", 3970000}); err != nil {
		return err
	}

	var s State
	if err := conn.Find(bson.M{"name": "Washington"}).One(&s); err != nil {
		return err
	}
	fmt.Printf("State: %#v\n", s)
	return nil
}
