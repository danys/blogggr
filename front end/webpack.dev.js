const merge = require('webpack-merge');
const common = require('./webpack.common.js');

const config = merge(common, {
    mode: 'development',
    devtool: 'source-map',
    devServer: {
      port: 9000,
      inline: true,
      hot: true,
      historyApiFallback: true,
      proxy:{
        "/api/**": "http://localhost:8080"
      }
    }
  }
);

module.exports = config;