koodohub_app.controller('HomeController', function($scope, $modal, $rootScope){

  $rootScope.$on('switchToSignInEvent', function(event) {
    delete $rootScope.errors;
    $scope.openSignIn();
  });

  $rootScope.$on('switchToSignUpEvent', function(event) {
    delete $rootScope.errors;
    $scope.openSignUp();
  });

  $scope.openSignUp = function () {
    delete $rootScope.errors;  //prevent errors to show up in modal
    var modalInstance = $modal.open({
      templateUrl: 'partials/sign_up.html',
      controller: 'SignUpModalController'
    });
  };

  $scope.openSignIn = function () {
    delete $rootScope.errors;
    var modalInstance = $modal.open({
      templateUrl: 'partials/sign_in.html',
      controller: 'SignInModalController'
    });
  }

  $scope.changeAvatar = function () {
    var modalInstance = $modal.open({
      templateUrl: 'partials/change_avatar.html',
      controller: 'ChangeAvatarController'
    });
  }
});

koodohub_app.controller('ChangeAvatarController', function($scope, $modalInstance, MemberService, $upload) {
  $scope.closeChangeAvatar = function() {
    $modalInstance.dismiss('cancel');
  }
  //TODO watch is not good option.  look for different angular js file upload plugin
  $scope.$watch('avatar_file', function() {
    console.log("upload file");
    $scope.upload = $upload.upload ({
      url: 'resource/members/uploadAvatar',
      data: {myObj: $scope.myModelObj},
      file: $scope.avatar_file
    }).progress(function(evt) {
      console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :'+ evt.config.file.name);
    }).success(function(data, status, headers, config) {
      $scope.closeChangeAvatar();
    });
  });
});

koodohub_app.controller('SignUpModalController', function($scope, $modalInstance, $window, MemberService, $rootScope) {
  $scope.user = new MemberService();
  $scope.signup = function() {
    var that = this;
    $scope.user.$save(function() {
      that.closeSignUp();
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

koodohub_app.controller('ActivateController', function($rootScope, $stateParams, ActivateService, $location){
  console.log("activate user "+$stateParams.email);
  ActivateService.get({email:$stateParams.email, token:$stateParams.token});
  $location.path("/");
  $rootScope.$broadcast('switchToSignInEvent');
})

koodohub_app.controller('MemberController', function($scope, $stateParams, MemberService){
  $scope.user = MemberService.get({username: $stateParams.username});
});

koodohub_app.controller('SettingsController', function($scope, $stateParams, MemberService){
  $scope.user = MemberService.get({username: $stateParams.username});
});

koodohub_app.controller('SignInModalController', function($scope, SessionService, $modalInstance,
                                                 $rootScope, $window, AUTH_TOKEN) {

  $scope.signin = function() {
    var that = this;
    SessionService.authenticate($.param({loginName: $scope.session.loginName,
      password: $scope.session.password}), function(authenticationResult) {
      that.closeSignIn();
      var authToken = authenticationResult.token;
      $window.sessionStorage.token = authToken;
      if ($scope.session.rememberMe) {
        $.cookie(AUTH_TOKEN, authToken, { expires: 365 });
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