package greet

import (
	"fmt"

	"github.com/pkg/errors"
)

func Greet(name string) error {
	if name == "" {
		return errors.New("no name provided")
	}
	fmt.Printf("Hello %s! I'm not in the $GOPATH!\n", name)
	return nil
}
