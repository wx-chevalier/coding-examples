
  server.host('https://pre-api-xspace.taobao.com', () => {
    server
      .post('/h5/:method/1.0')
      .on('request', (req) => {})
      .intercept((req, res, interceptor) => {
        interceptor.passthrough();
      })
      .on('beforeResponse', (req, res) => {
        console.log(res);
      });
  });
