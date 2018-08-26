#!/usr/bin/env bash
go-fuzz-build github.com/agtorre/go-solutions/section1/fuzz
go-fuzz -bin=./fuzz-fuzz.zip -workdir=output
