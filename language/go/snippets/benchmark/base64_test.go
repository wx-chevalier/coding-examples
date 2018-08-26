// $ go test -bench=BenchmarkBase64*  -benchmem
package benchmark

import (
	"encoding/base64"
	"math/rand"
	"testing"
)

func BenchmarkBase64Encode(b *testing.B) {
	data := make([]byte, 1024)
	rand.Read(data)

	b.ResetTimer()
	for n := 0; n < b.N; n++ {
		base64.StdEncoding.EncodeToString([]byte(data))
	}
}

func BenchmarkBase64Decode(b *testing.B) {
	data := make([]byte, 1024)
	rand.Read(data)
	encoded := base64.StdEncoding.EncodeToString([]byte(data))

	b.ResetTimer()
	for n := 0; n < b.N; n++ {
		_, err := base64.StdEncoding.DecodeString(encoded)
		if err != nil {
			panic(err)
		}
	}
}
