package tags

import (
	"errors"
	"reflect"
	"strings"
)

func DeSerializeStructStrings(s string, res interface{}) error {
	r := reflect.TypeOf(res)

	if r.Kind() != reflect.Ptr {
		return errors.New("res must be a pointer")
	}

	r = r.Elem()
	value := reflect.ValueOf(res).Elem()

	vals := strings.Split(s, ";")
	valMap := make(map[string]string)
	for _, v := range vals {
		keyval := strings.Split(v, ":")
		if len(keyval) != 2 {
			continue
		}
		valMap[keyval[0]] = keyval[1]
	}

	for i := 0; i < r.NumField(); i++ {
		field := r.Field(i)

		if serialize, ok := field.Tag.Lookup("serialize"); ok {
			if serialize == "-" {
				continue
			}
			if val, ok := valMap[serialize]; ok {
				value.Field(i).SetString(val)
			}
		} else if val, ok := valMap[field.Name]; ok {
			value.Field(i).SetString(val)
		}
	}
	return nil
}
