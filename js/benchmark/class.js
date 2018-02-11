'use strict';

const Benchmark = require('benchmark');
const benchmarks = require('beautify-benchmark');
const suite = new Benchmark.Suite();

const util = require('util');
const assert = require('assert');

class A {
  calculate() {
    return 1000000 * 1000000;
  }
}

function B() {}

B.prototype.calculate = function() {
  return 1000000 * 1000000;
};

class C extends A {}

function D() {
  B.call(this);
}
util.inherits(D, B);

class E extends C {}

class F {
  constructor() {
    D.call(this);
  }
}
util.inherits(F, D);

const a = new A();
const b = new B();
const c = new C();
const f = new F();
assert(a.calculate() === b.calculate());
assert(b.calculate() === c.calculate());
assert(b.calculate() === f.calculate());

// add tests
suite
  .add('class', function() {
    const a = new A();
    a.calculate();
  })
  .add('prototype', function() {
    const b = new B();
    b.calculate();
  })
  .add('class extends', function() {
    const c = new C();
    c.calculate();
  })
  .add('util.inherits', function() {
    const d = new D();
    d.calculate();
  })
  .add('class extends nested', function() {
    const e = new E();
    e.calculate();
  })
  .add('class + util.inherits', function() {
    const f = new F();
    f.calculate();
  })
  .on('cycle', function(event) {
    benchmarks.add(event.target);
  })
  .on('start', function() {
    console.log('\n  node version: %s, date: %s\n  Starting...', process.version, Date());
  })
  .on('complete', function done() {
    benchmarks.log();
  })
  .run({ async: false });