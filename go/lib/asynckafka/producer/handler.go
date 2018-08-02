package main

import (
	"net/http"

	sarama "gopkg.in/Shopify/sarama.v1"
)

type KafkaController struct {
	producer sarama.AsyncProducer
}

func (c *KafkaController) Handler(w http.ResponseWriter, r *http.Request) {
	if err := r.ParseForm(); err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	msg := r.FormValue("msg")
	if msg == "" {
		w.WriteHeader(http.StatusBadRequest)
		w.Write([]byte("msg must be set"))
		return
	}
	c.producer.Input() <- &sarama.ProducerMessage{Topic: "example", Key: nil, Value: sarama.StringEncoder(r.FormValue("msg"))}
	w.WriteHeader(http.StatusOK)
}
