'use strict';

koodohub_app.controller('HomeController', function($scope, $modal, $rootScope, $cookieStore){

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

  $scope.setTab = function (tabId) {
    $cookieStore.put('tab', tabId);
  };

  $scope.isSet = function (tabId) {
    if ($cookieStore.get('tab') == null) {
      $cookieStore.put('tab', 1);
    }
    return $cookieStore.get('tab')==tabId;
  };
});

koodohub_app.controller('MembersController', function($scope, MemberService) {
  $scope.members = MemberService.query();
})

koodohub_app.controller('SettingsController', function($scope, $window, $timeout, SettingsService, $upload) {

  $scope.upload = function(file) {
    console.log("upload file");
    var fileLinks = '';
    $scope.generateThumb(file);
    $upload.upload ({
      url: 'resource/members/uploadAvatar',
      data: {myObj: $scope.myModelObj},
      file: $scope.user.avatar_file
    }).progress(function(evt) {
      $scope.uploadPercentage = parseInt(100.0 * evt.loaded / evt.total);
      console.log('progress: ' + $scope.uploadPercentage + '% file :'+ evt.config.file.name);
    }).success(function(data, status, headers, config) {
      console.log('link'+data);
      $window.location.reload();
    });
  };
  $scope.updateEmail = function() {
    SettingsService.updateEmail({email:$scope.user.email});
  }
  $scope.updatePassword = function() {
    SettingsService.updatePassword({oldPassword:$scope.user.oldPassword, newPassword:$scope.user.newPassword});
  }
});

koodohub_app.controller('postNewProjectController', function($scope, $rootScope, $timeout,
                                                             $q, ProjectService, $upload) {
  $scope.fileReaderSupported = window.FileReader != null && (window.FileAPI == null || FileAPI.html5 != false);
  $scope.project = new ProjectService();
  $scope.previewTemplate='';
  $scope.project_files = [];
  $scope.project.medialink = '';
  $scope.postNewProject = function() {
    var uploadFiles = function () {
      var promises = [];
      for (var i = 0; i < $scope.project_files.length; i++) {
        var response = $upload.upload({
          url: 'resource/projects/uploadFile',
          file: $scope.project_files[i]
        }).progress(function (evt) {
          console.log('progress: ' + $scope.uploadPercentage + '% file :' + evt.config.file.name);
        }).success(function (ddata, status, headers, config) {
          console.log(config.file.name + ' uploaded.' + ddata.data);
          $scope.project.medialink += (ddata.data + ';');
        })
        promises.push(response);
      }
      return $q.all(promises);
    }
    uploadFiles().then(function() {
      $scope.project.title = $scope.title;
      $scope.project.description = $scope.description;
      console.log("media link:"+$scope.project.medialink);
      $scope.project.$save(function(response) {
        $location.path('/projects/'+response.data);
        console.log("project saved.");
      });
    });
  }

  $scope.addFiles = function(files) {
    if (typeof files != "undefined") {
      $scope.project_files = $scope.project_files.concat(files);
      console.log($scope.project_files);
    }
  }

  $scope.removeFile = function(filename) {
    var fileIndex = -1;
    for (var i=0; i<$scope.project_files.length; i++) {
      if ($scope.project_files[i].name == filename) {
        fileIndex = i;
        break;
      }
    }
    $scope.project_files.splice(fileIndex, 1);
  }
})

koodohub_app.controller('SignUpModalController', function($scope, $modalInstance, $window,
                                                          MemberService, $rootScope) {
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
  $scope.member = MemberService.get({username:$stateParams.username});
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