FROM golang:alpine

ENV GOPATH /code/
ADD . /code/src/github.com/agtorre/go-solutions/section3/docker
WORKDIR /code/src/github.com/agtorre/go-solutions/section3/docker/example
RUN go build

ENTRYPOINT /code/src/github.com/agtorre/go-solutions/section3/docker/example/example
