package currency

import (
	"strconv"
)

func ConvertPenniesToDollarString(amount int64) string {
	result := strconv.FormatInt(amount, 10)

	negative := false
	if result[0] == '-' {
		result = result[1:]
		negative = true
	}

	for len(result) < 3 {
		result = "0" + result
	}
	length := len(result)

	result = result[0:length-2] + "." + result[length-2:]

	if negative {
		result = "-" + result
	}

	return result
}
