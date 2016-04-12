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

distractorRouting.$inject = ['$stateProvider'];

function distractorRouting($stateProvider) {
    $stateProvider.state('main', {
        abstract: true
    });

    $stateProvider.state('main.index', {
        url: '/',
        controller: indexController
    });
}

indexController.$inject = ['$state'];

function indexController($state) {
    console.log("going 2 main.dashboard");
    $state.go('main.dashboard');
}