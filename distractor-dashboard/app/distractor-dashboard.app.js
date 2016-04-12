require('angular');
require('angular-animate');
require('angular-aria');
require('angular-ui-router');
require('angular-messages');
require('angular-material');

require('./commons/commons.module.js');
require('./dashboard/dashboard.module.js');


angular.module('distractor-dashboard', [
    // commons
    'distractor-dashboard.commons',

    // distractor-dashboard features
    'distractor-dashboard.dashboard'
]);

angular.module('distractor-dashboard')
    .config(distractorRouting);

distractorRouting.$inject = [
    '$urlRouterProvider',
    '$stateProvider',
    '$mdThemingProvider'
];

function distractorRouting($urlRouterProvider, $stateProvider, $mdThemingProvider) {
    $urlRouterProvider.otherwise('/dashboard');

    $stateProvider.state('main', {
        abstract: true,
        templateUrl: './commons/common-template.html'
    });

    $mdThemingProvider.theme('default')
        .primaryPalette('pink')
        .accentPalette('orange');
}

