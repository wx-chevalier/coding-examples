package main

import (
	"fmt"
	"net/http"

	"github.com/agtorre/go-solutions/section3/metrics"
)

func main() {
	http.HandleFunc("/counter", metrics.CounterHandler)
	http.HandleFunc("/timer", metrics.TimerHandler)
	http.HandleFunc("/report", metrics.ReportHandler)
	fmt.Println("listening on :8080")
	panic(http.ListenAndServe(":8080", nil))
}
