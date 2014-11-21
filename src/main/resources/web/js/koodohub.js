(function() {
  var app = angular.module('koodohub', ['ui.router', 'ui.bootstrap', 'koodohub.services']);

  app.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/');

    $stateProvider
      .state('home', {
        url: '/',
        templateUrl: 'member_home.html'
      })
      .state('sign_in', {
        url: '/sign_in',
        templateUrl: 'sign_in.html',
        controller: 'SessionController'
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

  app.controller('MemberController', function($scope, MemberService){
    $scope.signup = function() {
      console.log("sign up for "+$scope.user.fullName);
      var newMember = new MemberService();
      newMember.$save($.param({user: $scope.user}), function(data) {
        $scope.messages.push(
          {type: 'success', msgs: ['Welcome to Koodo Hub. '+$scope.user.userName]}
        );
      }), function(failureResponse) {
        $scope.messages.push(
          {type: 'danger', msgs: failureResponse.data.errors}
        );
      }
    }
  });

  app.controller('SessionController', function($scope, SessionService){
    this.user = {};

//    this.storeToSession = function() {
//      $cookieStore.put('userHeader', this.user.header);
//    };
//
//    this.loadFromSession = function() {
//      var userHeader = $cookieStore.get('userHeader');
//      if ( userHeader ) {
//        this.loadCurrentUser(userHeader);
//      }
//    };
//
//    this.loadCurrentUser = function(loadCurrentUser) {
//      /* $http get to load user info with given header */
//
//    };

    $scope.signin = function() {
      console.log("sign in for "+$scope.session.loginName);
        SessionService.authenticate($.param({loginName: $scope.session.loginName,
          password: $scope.session.password}), function(user) {
          console.log("yes");
//          $rootScope.user = user;
//          $http.defaults.headers.common['X-Auth-Token'] = user.token;
//          $cookieStore.put('user', user);
//          $location.path("/");
        });
    };
  });
})();
