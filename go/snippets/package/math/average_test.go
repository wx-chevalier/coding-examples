package math

import "testing"

func TestAverage(t *testing.T) {
	type args struct {
		xs []float64
	}

	tests := []struct {
		name string
		args args
		want float64
	}{
		// TODO: Add test cases.
		{"basic", args{[]float64{1, 2}}, 1.5},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := Average(tt.args.xs); got != tt.want {
				t.Errorf("Average() = %v, want %v", got, tt.want)
			}
		})
	}
}
