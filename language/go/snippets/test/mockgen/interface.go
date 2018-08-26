package mockgen

type GetSetter interface {
	Set(key, val string) error
	Get(key string) (string, error)
}
