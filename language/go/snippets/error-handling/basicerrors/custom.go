package basicerrors

import (
	"fmt"
)

type CustomError struct {
	Result string
}

func (c CustomError) Error() string {
	return fmt.Sprintf("there was an error; %s was the result", c.Result)
}

func SomeFunc() error {
	c := CustomError{Result: "this"}
	return c
}
