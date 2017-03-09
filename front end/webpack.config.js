var path = require('path');
var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");

const config = {
    entry: {
        app: './src/index.js',
        vendor: './src/vendor.js'
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: '[name].bundle.js'
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
                            presets: ['es2015', 'react']
                        }
                    }
                ],
            },
            {
                test: /\.css$/,
                use: ExtractTextPlugin.extract({
                    fallback: "style-loader",
                    use: "css-loader"
                })
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
    devtool: 'source-map',
    devServer: {
        port: 9000,
        inline: true,
        hot: true,
        historyApiFallback: true,
        proxy:{
            "/api/**": "http://localhost:8080"
        }
    },
    plugins: [
        new HtmlWebpackPlugin({
            title:"Blogggr",
            template: 'src/index.ejs'
        }),
        new webpack.optimize.CommonsChunkPlugin({
            name: ['app', 'vendor'],
            minChunk: Infinity
        }),
        new ExtractTextPlugin("styles.css"),
        new webpack.ProvidePlugin({
            jQuery: 'jquery',
            $: 'jquery',
            jquery: 'jquery'
        })
    ]
};

module.exports = config;