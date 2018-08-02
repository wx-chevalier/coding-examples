package ansicolor

import "fmt"

type Color int

const (
	ColorNone = iota
	Red
	Green
	Yellow
	Blue
	Magenta
	Cyan
	White
	Black Color = -1
)

type ColorText struct {
	TextColor Color
	Text      string
}

func (r *ColorText) String() string {
	if r.TextColor == ColorNone {
		return r.Text
	}

	value := 30
	if r.TextColor != Black {
		value += int(r.TextColor)
	}
	return fmt.Sprintf("\033[0;%dm%s\033[0m", value, r.Text)
}
