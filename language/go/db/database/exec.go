package database

import (
	"database/sql"

	_ "github.com/go-sql-driver/mysql" 
)

func Exec(db *sql.DB) error {
	defer db.Exec("DROP TABLE example")

	if err := Create(db); err != nil {
		return err
	}

	if err := Query(db); err != nil {
		return err
	}
	return nil
}
