const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

const config = {
  entry: {
    app: './src/index.js',
    vendor: './src/vendor.js'
  },
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: '[name].bundle.js',
    publicPath: '/'
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude:[path.resolve(__dirname, "node_modules")],
        use: [
          {
            loader: 'babel-loader',
            options: {
              presets: ['env', 'react'],
              "plugins": [
                ["transform-object-rest-spread", { "useBuiltIns": true }]
              ]
            }
          }
        ],
      },
      {
        test: /\.css$/,
        use: [
          MiniCssExtractPlugin.loader,
          "css-loader"
        ]
      },
      {
        test: /\.(svg|woff|woff2|otf|eot|ttf)$/,
        use:[
          {
            loader: "url-loader",
            options: {
              limit: 65000,
              name: '[name].[ext]'
            }
          }
        ]
      },
      {
        test: /\.png$/,
        use:[
          {
            loader: "file-loader",
            options: {
              name: '[name].[ext]'
            }
          }
        ]
      }
    ]
  },
  plugins: [
    new HtmlWebpackPlugin({
      title:"Blogggr",
      template: 'src/index.ejs',
      publicPath: '/'
    }),
    new MiniCssExtractPlugin({
      filename: "[name].css",
      chunkFilename: "[id].css"
    }),
    new webpack.ProvidePlugin({
      jQuery: 'jquery',
      $: 'jquery',
      jquery: 'jquery',
      'window.jQuery': 'jquery',
      'window.$': 'jquery',
      moment: 'moment'
    })
  ]
};

module.exports = config;