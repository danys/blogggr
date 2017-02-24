var path = require('path');
var HtmlWebpackPlugin = require('html-webpack-plugin');

const config = {
    entry: './src/index.js',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'webpack.bundle.js'
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
            }
        ]
    },
    devtool: 'source-map',
    devServer: {
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
        })
    ]
};

module.exports = config;