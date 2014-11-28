(function() {
  var app = angular.module('koodohub', ['ui.router', 'ui.bootstrap', 'koodohub.services']);

  app.config(function($stateProvider, $urlRouterProvider, $httpProvider) {
    $urlRouterProvider.otherwise('/');

    $stateProvider
      .state('home', {
        url: '/',
        templateUrl: 'partials/home.html',
        controller: 'HomeController'
      })
      .state('member', {
        url: '/member/:username',
        views: {
          '': {
            templateUrl: 'partials/member.html',
            controller: 'MemberController'
          },
          "member_info@member": {
            templateUrl: 'partials/member_info.html'
          },
          "member_stats@member": {
            templateUrl: 'partials/member_stats.html'
          },
          "member_micropost_form@member": {
            templateUrl: 'partials/member_micropost_form.html'
          },
          "member_micropost_feed@member": {
            templateUrl: 'partials/member_micropost_feed.html'
          }
        }
      });
//      .state('')
    /* Intercept http errors */
    var interceptor = function ($rootScope, $q, $location) {

      function success(response) {
        return response;
      }

      function error(response) {
        $rootScope.errors = [];
        var status = response.status;
        var config = response.config;
        var method = config.method;
        var url = config.url;

        if (status == 401) {
          $rootScope.errors.push("Please login.");
          $location.path("/sign_in");
        } else {
          $rootScope.errors.push(method + " on " + url + " failed with status " + status);
          $rootScope.errors = $rootScope.errors.concat(response.data.errors);
        }
        return $q.reject(response);
      }

      return function (promise) {
        return promise.then(success, error);
      };
    }
    $httpProvider.responseInterceptors.push(interceptor);
  });

  app.run(function($rootScope, $http, $location, SessionService) {

    /* Reset error when a new view is loaded */
    $rootScope.$on('$viewContentLoaded', function() {
      console.log("view reloaded");
      delete $rootScope.errors;
    });

//    $rootScope.hasRole = function(role) {
//
//      if ($rootScope.user === undefined) {
//        return false;
//      }
//
//      if ($rootScope.user.roles[role] === undefined) {
//        return false;
//      }
//
//      return $rootScope.user.roles[role];
//    };
//
//    $rootScope.logout = function() {
//      delete $rootScope.user;
//      delete $http.defaults.headers.common['X-Auth-Token'];
//      $cookieStore.remove('user');
//      $location.path("/login");
//    };
//
//    /* Try getting valid user from cookie or go to login page */
//    var originalPath = $location.path();
//    $location.path("/login");
//    var user = $cookieStore.get('user');
//    if (user !== undefined) {
//      $rootScope.user = user;
//      $http.defaults.headers.common['X-Auth-Token'] = user.token;
//
//      $location.path(originalPath);
//    }

  });

  app.controller('HomeController', function($scope, $modal, $rootScope){

    $rootScope.$on('switchToSignInEvent', function(event) {
      delete $rootScope.errors;
      $scope.openSignIn();
    });

    $rootScope.$on('switchToSignUpEvent', function(event) {
      delete $rootScope.errors;
      $scope.openSignUp();
    });

    $scope.openSignUp = function () {
      var modalInstance = $modal.open({
        templateUrl: 'partials/sign_up.html',
        controller: 'SignUpModalController'
      });
    };

    $scope.openSignIn = function () {
      var modalInstance = $modal.open({
        templateUrl: 'partials/sign_in.html',
        controller: 'SignInModalController'
      });
    }
  });

  app.controller('SignUpModalController', function($scope, $modalInstance, $location, MemberService, $rootScope) {
    $scope.user = new MemberService();
    $scope.signup = function() {
      var that = this;
      $scope.user.$save(function() {
        that.closeSignUp();
        $location.path('/member/'+$scope.user.username);
      });
    };
    $scope.switchToSignIn = function() {
      this.closeSignUp();
      $rootScope.$broadcast('switchToSignInEvent');
    }
    $scope.closeSignUp = function () {
      $modalInstance.dismiss('cancel');
    }
  });

  app.controller('MemberController', function($scope, $stateParams, MemberService){
    $scope.user = MemberService.get({username: $stateParams.username});
  });

  app.controller('SignInModalController', function($scope, SessionService, $modalInstance, $rootScope){

    $scope.signin = function() {
      console.log("sign in for "+$scope.session.loginName);
      SessionService.authenticate($.param({loginName: $scope.session.loginName,
        password: $scope.session.password}), function(user) {
        //todo
        that.closeSignIn();
        $location.path('/member/'+$scope.user.username);
      });
    };

    $scope.switchToSignUp = function() {
      this.closeSignIn();
      $rootScope.$broadcast('switchToSignUpEvent');
    }

    $scope.closeSignIn = function () {
      $modalInstance.dismiss('cancel');
    }
  });
})();
