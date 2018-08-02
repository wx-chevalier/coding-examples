package structured

import "github.com/sirupsen/logrus"

type Hook struct {
	id string
}

func (hook *Hook) Fire(entry *logrus.Entry) error {
	entry.Data["id"] = hook.id
	return nil
}

func (hook *Hook) Levels() []logrus.Level {
	return logrus.AllLevels
}

func Logrus() {
	logrus.SetFormatter(&logrus.TextFormatter{})
	logrus.SetLevel(logrus.InfoLevel)
	logrus.AddHook(&Hook{"123"})

	fields := logrus.Fields{}
	fields["success"] = true
	fields["complex_struct"] = struct {
		Event string
		When  string
	}{"Something happened", "Just now"}

	x := logrus.WithFields(fields)
	x.Warn("warning!")
	x.Error("error!")
}
