package main

import (
	"github.com/agtorre/go-solutions/section5/database"
	"github.com/agtorre/go-solutions/section5/dbinterface"
	_ "github.com/go-sql-driver/mysql"
)

func main() {
	db, err := database.Setup()
	if err != nil {
		panic(err)
	}

	tx, err := db.Begin()
	if err != nil {
		panic(err)
	}
	defer tx.Rollback()

	if err := dbinterface.Exec(db); err != nil {
		panic(err)
	}
	if err := tx.Commit(); err != nil {
		panic(err)
	}
}
