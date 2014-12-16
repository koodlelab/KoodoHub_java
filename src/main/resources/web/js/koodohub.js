'use strict';

var koodohub_app = angular.module('koodohub', ['ui.router', 'ui.bootstrap', 'ngCookies', 'koodohub.service']);

koodohub_app.config(function($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider) {

  $urlRouterProvider.otherwise('/');

  $stateProvider
    .state('home', {
      url: '/',
      templateUrl: 'partials/home.html'
    })
    .state('activate',{
      url: '/member/activate/:email/:token',
      controller: 'ActivateController'
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
        'response': function(response) {
          $rootScope.message = response.data.message;
          return response;
        },

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
  $httpProvider.interceptors.push(function ($q, $window, $location, AUTH_HEADER) {
    return {
      'request': function(config) {
//          console.log("request with token:"+$window.sessionStorage.token);
        var isRestCall = config.url.indexOf('resource') == 0;
        if (isRestCall && angular.isDefined($window.sessionStorage.token)) {
          var authToken = $window.sessionStorage.token;
          config.headers[AUTH_HEADER] = authToken;
        }
        return config || $q.when(config);
      }
    };
  });
});

koodohub_app.run(function($rootScope, $http, $location, $cookieStore, $window,
                          MemberService, $state, AUTH_TOKEN) {

  $rootScope.authenticated = false;

  /* Reset error when a new view is loaded */
  $rootScope.$on('$viewContentLoaded', function() {
    delete $rootScope.errors;
  });

  $rootScope.logout = function() {
    delete $rootScope.user;
    delete $window.sessionStorage.token;
    $.removeCookie(AUTH_TOKEN);
    $window.location.reload();

  };

  /* Try getting valid user from cookie or go to login page */
  var originalPath = $location.path();
  var authToken = $window.sessionStorage.token;
  if (authToken === undefined) {
    authToken = $.cookie(AUTH_TOKEN);
  }
  if (authToken !== undefined) {
    MemberService.get({username: authToken.split(':')[0]}, function(user) {
      $rootScope.user = user;
      $rootScope.authenticated = true;
      $window.sessionStorage.token = authToken;
      $location.path(originalPath);
    });
  }
  $rootScope.initialized = true;

});



