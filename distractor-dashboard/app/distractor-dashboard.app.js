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
    '$urlRouterProvider'
];

function distractorRouting($urlRouterProvider) {
    $urlRouterProvider.otherwise('/dashboard');
}

