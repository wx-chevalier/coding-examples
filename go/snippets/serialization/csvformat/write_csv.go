package csvformat

import (
	"bytes"
	"encoding/csv"
	"io"
	"os"
)

type Book struct {
	Author string
	Title  string
}

type Books []Book

func (books *Books) ToCSV(w io.Writer) error {
	n := csv.NewWriter(w)
	err := n.Write([]string{"Author", "Title"})
	if err != nil {
		return err
	}
	for _, book := range *books {
		err := n.Write([]string{book.Author, book.Title})
		if err != nil {
			return err
		}
	}

	n.Flush()
	return n.Error()
}

func WriteCSVOutput() error {
	b := Books{
		Book{
			Author: "F Scott Fitzgerald",
			Title:  "The Great Gatsby",
		},
		Book{
			Author: "J D Salinger",
			Title:  "The Catcher in the Rye",
		},
	}
	return b.ToCSV(os.Stdout)
}

func WriteCSVBuffer() (*bytes.Buffer, error) {
	b := Books{
		Book{
			Author: "F Scott Fitzgerald",
			Title:  "The Great Gatsby",
		},
		Book{
			Author: "J D Salinger",
			Title:  "The Catcher in the Rye",
		},
	}

	w := &bytes.Buffer{}
	err := b.ToCSV(w)
	return w, err
}
