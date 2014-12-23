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

  $scope.updateSettings = function () {
    var modalInstance = $modal.open({
      templateUrl: 'partials/member_settings.html',
      controller: 'updateSettingsController'
    })
  }

  $scope.postNewProject = function () {
    var modalInstance = $modal.open({
      templateUrl: 'partials/post_new_project.html',
      controller: 'postNewProjectController'
    })
  }
});

koodohub_app.controller('SettingsController', function($scope, $window, SettingsService, $upload) {

//  $scope.$watch('user.avatar_file', function() {
//    console.log("upload file");
    $scope.upload = $upload.upload ({
      url: 'resource/members/uploadAvatar',
      data: {myObj: $scope.myModelObj},
      file: $scope.user.avatar_file
    }).progress(function(evt) {
      console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :'+ evt.config.file.name);
    }).success(function(data, status, headers, config) {
      $window.location.reload();
    });
//  });
  $scope.updateEmail = function() {
    console.log("Update email");
    SettingsService.updateEmail({email:$scope.user.email});
  }
  $scope.updatePassword = function() {
    console.log("Update password.");
    SettingsService.updatePassword({oldPassword:$scope.user.oldPassword, newPassword:$scope.user.newPassword});
  }
});

koodohub_app.controller('postNewProjectController', function($scope, $modalInstance, ProjectService) {
  $scope.closePostNewProject = function() {
    $modalInstance.dismiss('cancel');
  }
  $scope.project = new ProjectService();
  $scope.save = function() {
    $scope.project.$save(function() {
      $scope.closePostNewProject();
    })
  }
})

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
  ActivateService.get({email:$stateParams.email, token:$stateParams.token});
  $location.path("/");
  $rootScope.$broadcast('switchToSignInEvent');
})

koodohub_app.controller('MemberController', function($scope, $stateParams, MemberService){
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