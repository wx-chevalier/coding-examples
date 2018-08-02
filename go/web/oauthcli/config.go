package oauthcli

import (
	"golang.org/x/net/context"
	"fmt"
	"os"

	"golang.org/x/oauth2"
	"golang.org/x/oauth2/github"
)

func Setup() *oauth2.Config {
	return &oauth2.Config{
		ClientID:     os.Getenv("GITHUB_CLIENT"),
		ClientSecret: os.Getenv("GITHUB_SECRET"),
		Scopes:       []string{"repo", "user"},
		Endpoint:     github.Endpoint,
	}
}

func GetToken(ctx context.Context, conf *oauth2.Config) (*oauth2.Token, error) {
	url := conf.AuthCodeURL("state")
	fmt.Printf("Type the following url into your browser and follow the directions on screen: %v\n", url)
	fmt.Println("Paste the code returned in the redirect URL and hit Enter:")

	var code string
	if _, err := fmt.Scan(&code); err != nil {
		return nil, err
	}
	return conf.Exchange(ctx, code)
}
