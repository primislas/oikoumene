const PROXY_CONFIG = {
  // "**": {
  //   "target": "http://localhost:9000",
  //   "secure": false,
  //   "bypass": function (req) {
  //     console.log(`Proxying request to: ${req.url}`);
  //     if (req && req.headers && req.headers.accept && req.headers.accept.indexOf("html") !== -1) {
  //       console.log(`Skipping proxy for browser request: ${req.url}.`);
  //       return "/index.html";
  //     }
  //   }
  // }
  "/clausewitz/eu4/map/data": {
    "target": "ws://localhost:9000",
    "secure": false,
    "changeOrigin": true,
    "ws": true,
    "pathRewrite": {
      // "^/turmchen": ""
    }
  },
  "/clausewitz/*": {
    "target": "http://localhost:9000",
    "secure": false,
    "changeOrigin": true,
    "pathRewrite": {
      // "^/turmchen": ""
    }
  }

};

module.exports = PROXY_CONFIG;
