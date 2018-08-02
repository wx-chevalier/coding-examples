package errwrap

import (
	"fmt"

	"github.com/pkg/errors"
)

func WrappedError(e error) error {
	return errors.Wrap(e, "An error occurred in WrappedError")
}

type ErrorTyped struct {
	error
}

func Wrap() {
	e := errors.New("standard error")

	fmt.Println("Regular Error - ", WrappedError(e))
	fmt.Println("Typed Error - ", WrappedError(ErrorTyped{errors.New("typed error")}))
	fmt.Println("Nil -", WrappedError(nil))
}
