package discovery

import "github.com/hashicorp/consul/api"

type Client interface {
	Register(tags []string) error
	Service(service, tag string) ([]*api.ServiceEntry, *api.QueryMeta, error)
}

type client struct {
	client  *api.Client
	address string
	name    string
	port    int
}

func NewClient(config *api.Config, address, name string, port int) (Client, error) {
	c, err := api.NewClient(config)
	if err != nil {
		return nil, err
	}
	cli := &client{
		client:  c,
		name:    name,
		address: address,
		port:    port,
	}
	return cli, nil
}
