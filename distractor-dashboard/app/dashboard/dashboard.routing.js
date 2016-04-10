angular.module('distractor-dashboard.dashboard')
    .config(dashboardRouting);

dashboardRouting.$inject = ['$stateProvider'];

function dashboardRouting($stateProvider) {
    $stateProvider.state('main.dashboard', {
        url: '/dashboard',
        controller: 'distractor-dashboard.dashboard',
        controllerAs: 'dashboard',
        templateUrl: 'dashboard/dashboard.html'
    });
}