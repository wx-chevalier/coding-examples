package internal

import (
	"golang.org/x/net/context"

	"github.com/agtorre/go-solutions/section7/grpcjson/keyvalue"
	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
)

type KeyValue struct {
	m map[string]string
}

func NewKeyValue() *KeyValue {
	return &KeyValue{
		m: make(map[string]string),
	}
}

func (k *KeyValue) Set(ctx context.Context, r *keyvalue.SetKeyValueRequest) (*keyvalue.KeyValueResponse, error) {
	k.m[r.GetKey()] = r.GetValue()
	return &keyvalue.KeyValueResponse{Value: r.GetValue()}, nil
}

// Get gets a value given a key, or say not found if
// it doesn't exist
func (k *KeyValue) Get(ctx context.Context, r *keyvalue.GetKeyValueRequest) (*keyvalue.KeyValueResponse, error) {
	val, ok := k.m[r.GetKey()]
	if !ok {
		return nil, grpc.Errorf(codes.NotFound, "key not set")
	}
	return &keyvalue.KeyValueResponse{Value: val}, nil
}
