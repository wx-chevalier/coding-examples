package collections

import "strings"

func LowerCaseData(w WorkWith) WorkWith {
	w.Data = strings.ToLower(w.Data)
	return w
}

func IncrementVersion(w WorkWith) WorkWith {
	w.Version++
	return w
}

func OldVersion(v int) func(w WorkWith) bool {
	return func(w WorkWith) bool {
		return w.Version >= v
	}
}
