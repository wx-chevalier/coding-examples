package main

import "errors"

func Coverage(condition bool) error {
	if condition {
		return errors.New("condition was set")
	}
	return nil
}
