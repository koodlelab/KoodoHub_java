(function() {
  var app = angular.module('koodohub', ['ui.router', 'ui.bootstrap', 'ngCookies', 'koodohub.services']);

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
//          "member_info@member": {
//            templateUrl: 'partials/member_info.html'
//          },
          "member_stats@member": {
            templateUrl: 'partials/member_stats.html'
          },
          "member_micropost_form@member": {
            templateUrl: 'partials/member_project_form.html'
          },
          "member_micropost_feed@member": {
            templateUrl: 'partials/member_project_feed.html'
          }
        }
      })
    /* Register error provider that shows message on failed requests or redirects to login page on
     * unauthenticated requests */
    $httpProvider.interceptors.push(function ($q, $rootScope, $location) {
        return {
          'responseError': function(rejection) {
            var status = rejection.status;
            var config = rejection.config;
            var method = config.method;
            var url = config.url;
            $rootScope.errors = [];
            if (status == 401) {
              $rootScope.errors.push("Please login with valid username and password.");
              $location.path( "/" );
            } else {
              $rootScope.errors.push(method + " on " + url + " failed with status " + status);
              $rootScope.errors = $rootScope.errors.concat(rejection.data.errors);
            }
            return $q.reject(rejection);
          }
        };
      }
    );

    /* Registers auth token interceptor, auth token is either passed by header or by query parameter
     * as soon as there is an authenticated user */
    $httpProvider.interceptors.push(function ($q, $window, $location) {
      return {
        'request': function(config) {
          console.log("request with token:"+$window.sessionStorage.token);
          var isRestCall = config.url.indexOf('services') == 0;
          if (isRestCall && angular.isDefined($window.sessionStorage.token)) {
            var authToken = $window.sessionStorage.token;
            config.headers['X-Auth-Token'] = authToken;
          }
          return config || $q.when(config);
        }
      };
    });
  });

  app.run(function($rootScope, $http, $location, $cookieStore, $window, MemberService, $state) {

    $rootScope.authenticated = false;

    /* Reset error when a new view is loaded */
    $rootScope.$on('$viewContentLoaded', function() { console.log("view reloaded");
      delete $rootScope.errors;
    });

    $rootScope.logout = function() {
      delete $rootScope.user;
      delete $window.sessionStorage.token;
      $.removeCookie('authToken');
      $window.location.reload();

    };

    /* Try getting valid user from cookie or go to login page */
    var originalPath = $location.path();
    var authToken = $window.sessionStorage.token;
    if (authToken === undefined) {
      authToken = $.cookie('authToken');
    }
    if (authToken !== undefined) {
      MemberService.get({username: authToken.split(':')[0]}, function(user) {
        $rootScope.user = user;
        $rootScope.authenticated = true;
        $window.sessionStorage.token = authToken;
        $location.path(originalPath);
      });
    }
    console.log("authenticated..."+$rootScope.authenticated);

    $rootScope.initialized = true;

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

  app.controller('SignUpModalController', function($scope, $modalInstance, $window, MemberService, $rootScope) {
    $scope.user = new MemberService();
    $scope.signup = function() {
      var that = this;
      $scope.user.$save(function() {
        that.closeSignUp();
        $window.location.reload();
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

  app.controller('SignInModalController', function($scope, SessionService, $modalInstance,
                                                   $rootScope, $window) {

    $scope.signin = function() {
      var that = this;
      SessionService.authenticate($.param({loginName: $scope.session.loginName,
        password: $scope.session.password}), function(authenticationResult) {
        that.closeSignIn();
        var authToken = authenticationResult.token;
        $window.sessionStorage.token = authToken;
        if ($scope.session.rememberMe) {
          $.cookie('authToken', authToken, { expires: 365 });
        }
        $rootScope.user = $scope.session.loginName;
        console.log("signed in");
        $window.location.reload();
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
