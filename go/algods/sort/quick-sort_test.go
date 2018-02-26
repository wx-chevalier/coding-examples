package main

import (
	"testing"
)

func Test_swap(t *testing.T) {
	type args struct {
		x *int
		y *int
	}
	tests := []struct {
		name string
		args args
	}{
		// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			swap(tt.args.x, tt.args.y)
		})
	}
}

func Test_partition(t *testing.T) {
	type args struct {
		array         []int
		p             uint
		q             uint
		pivotLocation uint
	}
	tests := []struct {
		name string
		args args
		want uint
	}{
		// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := partition(tt.args.array, tt.args.p, tt.args.q, tt.args.pivotLocation); got != tt.want {
				t.Errorf("partition() = %v, want %v", got, tt.want)
			}
		})
	}
}

func Test_quicksort(t *testing.T) {
	type args struct {
		array []int
		start uint
		end   uint
	}
	tests := []struct {
		name string
		args args
	}{
		// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			quicksort(tt.args.array, tt.args.start, tt.args.end)
		})
	}
}
