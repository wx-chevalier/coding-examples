package dataconv

import (
	"fmt"
	"strconv"
)

func Strconv() error {
	s := "1234"
	res, err := strconv.ParseInt(s, 10, 64)
	if err != nil {
		return err
	}

	fmt.Println(res)

	res, err = strconv.ParseInt("FF", 16, 64)
	if err != nil {
		return err
	}

	fmt.Println(res)

	val, err := strconv.ParseBool("true")
	if err != nil {
		return err
	}

	fmt.Println(val)
	return nil
}
