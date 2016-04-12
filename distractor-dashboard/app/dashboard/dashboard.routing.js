angular.module('distractor-dashboard.dashboard')
    .config(dashboardRouting);

dashboardRouting.$inject = ['$stateProvider'];

function dashboardRouting($stateProvider) {
    $stateProvider.state('main.dashboard', {
        url: '/dashboard',
        templateUrl: 'dashboard/dashboard.html',
        controller: 'distractor-dashboard.dashboard.controller',
        controllerAs: 'dashboard'
    });
}