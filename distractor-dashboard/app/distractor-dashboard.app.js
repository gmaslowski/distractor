require('angular');
require('angular-ui-router');

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
    '$stateProvider'
];

function distractorRouting($urlRouterProvider, $stateProvider) {
    $urlRouterProvider.otherwise('/dashboard');

    $stateProvider.state('main', {
        abstract: true,
        templateUrl: './commons/common-template.html'
    });

}

