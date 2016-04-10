module.exports = {
    context: __dirname + '/app',
    entry: {
        dashboard: ['./distractor-dashboard.app.js', './dashboard/dashboard.module.js', './dashboard/dashboard.routing.js']
    },
    output: {
        path: __dirname + '/app',
        filename: 'distractor-dashboard.js'
    }
};