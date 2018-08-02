package dataconv

import "fmt"

func CheckType(s interface{}) {
	switch s.(type) {
	case string:
		fmt.Println("It's a string!")
	case int:
		fmt.Println("It's an int!")
	default:
		fmt.Println("not sure what it is...")
	}
}

func Interfaces() {
	CheckType("test")
	CheckType(1)
	CheckType(false)

	var i interface{}
	i = "test"

	if val, ok := i.(string); ok {
		fmt.Println("val is", val)
	}

	if _, ok := i.(int); !ok {
		fmt.Println("uh oh! glad we handled this")
	}
}
