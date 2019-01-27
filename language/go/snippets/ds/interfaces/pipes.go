package interfaces

import (
	"io"
	"os"
)

func PipeExample() error {
	r, w := io.Pipe()

	go func() {
		w.Write([]byte("test\n"))
		w.Close()
	}()

	if _, err := io.Copy(os.Stdout, r); err != nil {
		return err
	}
	return nil
}
