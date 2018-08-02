FROM golang:alpine

ENV GOPATH /code/
ADD . /code/src/github.com/agtorre/go-solutions/section3/monitoring
WORKDIR /code/src/github.com/agtorre/go-solutions/section3/monitoring
RUN go build

ENTRYPOINT /code/src/github.com/agtorre/go-solutions/section3/monitoring/monitoring
