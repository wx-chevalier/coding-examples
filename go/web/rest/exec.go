package rest

import "fmt"

func Exec() error {
	c := NewAPIClient("username", "password")

	StatusCode, err := c.GetGoogle()
	if err != nil {
		return err
	}
	fmt.Println("Result of GetGoogle:", StatusCode)
	return nil
}
