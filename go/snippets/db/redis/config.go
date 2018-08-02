package redis

import (
	"os"

	redis "gopkg.in/redis.v5"
)

func Setup() (*redis.Client, error) {
	client := redis.NewClient(&redis.Options{
		Addr:     "localhost:6379",
		Password: os.Getenv("REDISPASSWORD"),
		DB:       0, 
	})

	_, err := client.Ping().Result()
	return client, err
}
