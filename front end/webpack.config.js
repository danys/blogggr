var path = require('path');

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
        hot: true
    }
};

module.exports = config;