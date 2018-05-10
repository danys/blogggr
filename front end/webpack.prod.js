const webpack = require('webpack');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");

const config = merge(common, {
    mode: 'production',
    optimization: {
    minimizer: [
      new UglifyJSPlugin({
        cache: true,
        parallel: true,
        sourceMap: true
      }),
      new OptimizeCSSAssetsPlugin({})
      ]
    },
    plugins: [
        new UglifyJSPlugin({
          sourceMap: true
        }),
        new webpack.DefinePlugin({
          'process.env.NODE_ENV': JSON.stringify('production')
        })
    ],
  }
);

module.exports = config;