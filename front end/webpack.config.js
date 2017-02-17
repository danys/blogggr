var path = require('path');

const config = {
    entry: './src/index.jsx',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'webpack.bundle.js'
    },
    module: {
        rules: [
            {test: /\.(js|jsx)$/, use: 'babel-loader'}
        ]
    },
    devtool: 'source-map',
    devServer: {
        inline: true,
        hot: true
    }
};

module.exports = config;