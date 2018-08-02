package tags

import "reflect"

func SerializeStructStrings(s interface{}) (string, error) {
	result := ""

	r := reflect.TypeOf(s)
	value := reflect.ValueOf(s)

	if r.Kind() == reflect.Ptr {
		r = r.Elem()
		value = value.Elem()
	}

	for i := 0; i < r.NumField(); i++ {
		field := r.Field(i)
		key := field.Name
		if serialize, ok := field.Tag.Lookup("serialize"); ok {
			if serialize == "-" {
				continue
			}
			key = serialize
		}

		switch value.Field(i).Kind() {
		case reflect.String:
			result += key + ":" + value.Field(i).String() + ";"
		default:
			continue
		}
	}
	return result, nil
}
