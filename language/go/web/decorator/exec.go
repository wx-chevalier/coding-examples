package decorator

import "fmt"

func Exec() error {
	c := Setup()

	resp, err := c.Get("https://www.google.com")
	if err != nil {
		return err
	}
	fmt.Println("Response code:", resp.StatusCode)
	return nil
}
