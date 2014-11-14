(function() {
  var app = angular.module('koodohub', ['ui.router']);

  app.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/');

    $stateProvider
      .state('home', {
        url: '/',
        templateUrl: 'member_home.html'
//        controller: 'MemberController'
      })
      .state('sign_up', {
        url: '/sign_up',
        templateUrl: 'sign_up.html',
        controller: 'MemberController'
      })
  });

  app.controller('MemberController', function($scope){
    $scope.signup = function() {
      console.log("sign up for "+$scope.member.name);
//      var new_member = $resource('/member/');
//      new_member.
    }
  });
//      .state('list', {
//        url: '/list',
//        templateUrl: 'templates/list.html',
//        controller: 'ListCtrl'
//      })
//      .state('list.item', {
//        url: '/:item',
//        templateUrl: 'templates/list.item.html',
//        controller: function($scope, $stateParams) {
//          $scope.item = $stateParams.item;
//        }
//      })


//  views: {
//    'content@': {
//      templateUrl: 'partials/home.html',
//        controller: 'HomeController'
//    }
//  }
//  app.controller('StoreController', ['$http', function($http){
//    var store = this;
//    store.products = [];
//    $http.get('/store-products.json').success(function(data){
//      store.products = data;
//    });
//  }]);

})();
