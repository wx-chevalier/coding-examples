package templates

import (
	"fmt"
	"html/template"
	"os"
)

func HTMLDifferences() error {
	t := template.New("html")
	t, err := t.Parse("<h1>Hello! {{.Name}}</h1>\n")
	if err != nil {
		return err
	}

	err = t.Execute(os.Stdout, map[string]string{"Name": "<script>alert('Can you see me?')</script>"})
	if err != nil {
		return err
	}

	fmt.Println(template.JSEscaper(`example <example@example.com>`))
	fmt.Println(template.HTMLEscaper(`example <example@example.com>`))
	fmt.Println(template.URLQueryEscaper(`example <example@example.com>`))

	return nil
}
