package main

import (
	"fmt"

	"github.com/agtorre/go-solutions/section3/currency"
)

func main() {
	userInput := "15.93"

	pennies, err := currency.ConvertStringDollarsToPennies(userInput)
	if err != nil {
		panic(err)
	}

	fmt.Printf("User input converted to %d pennies\n", pennies)

	pennies += 15

	dollars := currency.ConvertPenniesToDollarString(pennies)

	fmt.Printf("Added 15 cents, new values is %s dollars\n", dollars)
}
