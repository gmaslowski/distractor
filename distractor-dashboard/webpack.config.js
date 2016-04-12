var webpack = require('webpack');
var pluginHtml = require('html-webpack-plugin');
var pluginExtractText = require("extract-text-webpack-plugin");

module.exports = {
    context: __dirname + '/app',
    entry: {
        vendors: [
            'angular-material/angular-material.css'
        ],
        dashboard: './distractor-dashboard.app.js'
    },
    output: {
        path: __dirname + '/app',
        filename: 'distractor-dashboard.js'
    },
    module: {
        loaders: [{
            test: /\.css$/,
            loader: pluginExtractText.extract("style-loader", "css-loader")
        }]
    },
    plugins: [
        new pluginHtml({
            template: './index.html'
        }),
        new webpack.optimize.CommonsChunkPlugin({
            name: 'vendors',
            filename: 'vendors.[chunkhash].js',
            chunks: ['vendors']
        }),
        new pluginExtractText('[name].[chunkhash].css'),
        new webpack.optimize.DedupePlugin(),
        new webpack.optimize.OccurenceOrderPlugin()
    ]
};