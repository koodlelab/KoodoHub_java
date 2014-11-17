(function() {
  var app = angular.module('koodohub', ['ui.router', 'ngResource', 'ui.bootstrap']);

  app.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/');

    $stateProvider
      .state('home', {
        url: '/',
        templateUrl: 'member_home.html'
      })
      .state('sign_up', {
        url: '/sign_up',
        templateUrl: 'sign_up.html',
        controller: 'MemberController'
      })
  });

  app.controller('MainController', function($scope){
    $scope.messages = [];

    $scope.closeAlert = function (index) {
      $scope.messages.splice(index, 1);
    }

  });

  app.controller('MemberController', function($scope, $resource){
    $scope.signup = function() {
      console.log("sign up for "+$scope.member.fullName);
      var new_member = $resource('/services/members');
      new_member.save($scope.member, function(data) {
        $scope.messages.push(
          {type: 'success', msgs: ['Welcome to Koodo Hub. '+$scope.member.userName]}
        );

      }, function(failureResponse) {
        $scope.messages.push(
          {type: 'danger', msgs: failureResponse.data.errors}
        );
      });
    }
  });
})();
