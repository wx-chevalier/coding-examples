package dbinterface

import (
	"fmt"

	"github.com/agtorre/go-solutions/section5/database"
)

func Query(db DB) error {
	name := "Aaron"
	rows, err := db.Query("SELECT name, created FROM example where name=?", name)
	if err != nil {
		return err
	}
	defer rows.Close()
	for rows.Next() {
		var e database.Example
		if err := rows.Scan(&e.Name, &e.Created); err != nil {
			return err
		}
		fmt.Printf("Results:\n\tName: %s\n\tCreated: %v\n", e.Name, e.Created)
	}
	return rows.Err()
}
