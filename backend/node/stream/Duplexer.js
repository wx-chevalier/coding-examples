const { Duplex } = require('stream');

class Duplexer extends Duplex {
  constructor(props) {
    super(props);
    this.data = [];
  }

  _read(size) {
    const chunk = this.data.shift();
    if (chunk == 'stop') {
      this.push(null);
    } else {
      if (chunk) {
        this.push(chunk);
      }
    }
  }

  _write(chunk, encoding, cb) {
    this.data.push(chunk);
    cb();
  }
}

const d = new Duplexer({ allowHalfOpen: true });
d.on('data', function(chunk) {
  console.log('read: ', chunk.toString());
});
d.on('readable', function() {
  console.log('readable');
});
d.on('end', function() {
  console.log('Message Complete');
});
d.write('I think, ');
d.write('therefore ');
d.write('I am.');
d.write('Rene Descartes');
d.write('stop');
