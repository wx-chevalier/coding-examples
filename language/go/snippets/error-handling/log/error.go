package log

import "github.com/pkg/errors"
import "log"

func OriginalError() error {
	return errors.New("error occurred")
}

func PassThroughError() error {
	err := OriginalError()
	return errors.Wrap(err, "in passthrougherror")
}

func FinalDestination() {
	err := PassThroughError()
	if err != nil {
		log.Printf("an error occurred: %s\n", err.Error())
		return
	}
}
