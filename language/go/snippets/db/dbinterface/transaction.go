package dbinterface

import "database/sql"

type DB interface {
	Exec(query string, args ...interface{}) (sql.Result, error)
	Prepare(query string) (*sql.Stmt, error)
	Query(query string, args ...interface{}) (*sql.Rows, error)
	QueryRow(query string, args ...interface{}) *sql.Row
}

type Transaction interface {
	DB
	Commit() error
	Rollback() error
	Stmt(stmt *sql.Stmt) *sql.Stmt
}
