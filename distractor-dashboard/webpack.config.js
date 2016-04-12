module.exports = {
    context: __dirname + '/app',
    entry: {
        dashboard: './distractor-dashboard.app.js'
    },
    output: {
        path: __dirname + '/app',
        filename: 'distractor-dashboard.js'
    }
};