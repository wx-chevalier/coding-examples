package currency

import (
	"errors"
	"strconv"
	"strings"
)

func ConvertStringDollarsToPennies(amount string) (int64, error) {
	_, err := strconv.ParseFloat(amount, 64)
	if err != nil {
		return 0, err
	}

	groups := strings.Split(amount, ".")
	result := groups[0]
	r := ""

	if len(groups) == 2 {
		if len(groups[1]) != 2 {
			return 0, errors.New("invalid cents")
		}
		r = groups[1]
		if len(r) > 2 {
			r = r[:2]
		}
	}

	for len(r) < 2 {
		r += "0"
	}

	result += r
	return strconv.ParseInt(result, 10, 64)
}
