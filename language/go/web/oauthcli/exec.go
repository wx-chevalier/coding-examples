package oauthcli

import (
	"fmt"
	"io"
	"net/http"
	"os"
)

func GetUsers(client *http.Client) error {
	url := fmt.Sprintf("https://api.github.com/user")

	resp, err := client.Get(url)
	if err != nil {
		return err
	}
	defer resp.Body.Close()
	fmt.Println("Status Code from", url, ":", resp.StatusCode)
	io.Copy(os.Stdout, resp.Body)
	return nil
}
