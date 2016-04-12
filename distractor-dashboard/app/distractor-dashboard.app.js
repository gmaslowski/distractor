require('angular');
require('angular-ui-router');

require('./dashboard/dashboard.module.js');

angular.module('distractor-dashboard', [
    // dependencies
    'ui.router',

    // distractor-dashboard features
    'distractor-dashboard.dashboard'
]);

angular.module('distractor-dashboard')
    .config(distractorRouting);

distractorRouting.$inject = [
    '$stateProvider',
    '$urlRouterProvider'
];

function distractorRouting($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/dashboard');

    $stateProvider.state('main', {
        abstract: true
    });

    $stateProvider.state('main.index', {
        url: '/'
    });
}