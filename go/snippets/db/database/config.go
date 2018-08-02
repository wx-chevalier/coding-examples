package database

import (
	"database/sql"
	"fmt"
	"os"
	"time"

	_ "github.com/go-sql-driver/mysql"
)

type Example struct {
	Name    string
	Created *time.Time
}

func Setup() (*sql.DB, error) {
	db, err := sql.Open("mysql",
		fmt.Sprintf("%s:%s@/gosolutions?parseTime=true",
		 os.Getenv("MYSQLUSERNAME"), os.Getenv("MYSQLPASSWORD")))
	if err != nil {
		return nil, err
	}
	return db, nil
}
