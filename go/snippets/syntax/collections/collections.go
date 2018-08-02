package collections

type WorkWith struct {
	Data    string
	Version int
}

func Filter(ws []WorkWith, f func(w WorkWith) bool) []WorkWith {
	result := make([]WorkWith, 0)
	for _, w := range ws {
		if f(w) {
			result = append(result, w)
		}
	}
	return result
}

func Map(ws []WorkWith, f func(w WorkWith) WorkWith) []WorkWith {
	result := make([]WorkWith, len(ws))

	for pos, w := range ws {
		newW := f(w)
		result[pos] = newW
	}
	return result
}
