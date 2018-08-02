package global

import (
	"errors"
	"os"
	"sync"

	"github.com/sirupsen/logrus"
)

var (
	log     *logrus.Logger
	initLog sync.Once
)

func Init() error {
	err := errors.New("already initialized")
	initLog.Do(func() {
		err = nil
		log = logrus.New()
		log.Formatter = &logrus.JSONFormatter{}
		log.Out = os.Stdout
		log.Level = logrus.DebugLevel
	})
	return err
}

func SetLog(l *logrus.Logger) {
	log = l
}

func WithField(key string, value interface{}) *logrus.Entry {
	return log.WithField(key, value)
}

func Debug(args ...interface{}) {
	log.Debug(args...)
}
