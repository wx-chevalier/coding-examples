package filedirs

import (
	"errors"
	"io"
	"os"
)

func Operate() error {
	if err := os.Mkdir("example_dir", os.FileMode(0755)); err != nil {
		return err
	}

	if err := os.Chdir("example_dir"); err != nil {
		return err
	}

	f, err := os.Create("test.txt")
	if err != nil {
		return err
	}

	value := []byte("hello\n")
	count, err := f.Write(value)
	if err != nil {
		return err
	}
	if count != len(value) {
		return errors.New("incorrect length returned from write")
	}

	if err := f.Close(); err != nil {
		return err
	}

	f, err = os.Open("test.txt")
	if err != nil {
		return err
	}

	io.Copy(os.Stdout, f)

	if err := f.Close(); err != nil {
		return err
	}

	if err := os.Chdir(".."); err != nil {
		return err
	}

	if err := os.RemoveAll("example_dir"); err != nil {
		return err
	}
	return nil
}
