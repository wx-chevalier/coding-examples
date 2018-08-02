package oauthstore

import (
	"golang.org/x/net/context"
	"net/http"

	"golang.org/x/oauth2"
)

type Config struct {
	*oauth2.Config
	Storage
}

func (c *Config) Exchange(ctx context.Context, code string) (*oauth2.Token, error) {
	token, err := c.Config.Exchange(ctx, code)
	if err != nil {
		return nil, err
	}
	if err := c.Storage.SetToken(token); err != nil {
		return nil, err
	}
	return token, nil
}

func (c *Config) TokenSource(ctx context.Context, t *oauth2.Token) oauth2.TokenSource {
	return StorageTokenSource(ctx, c, t)
}

func (c *Config) Client(ctx context.Context, t *oauth2.Token) *http.Client {
	return oauth2.NewClient(ctx, c.TokenSource(ctx, t))
}
