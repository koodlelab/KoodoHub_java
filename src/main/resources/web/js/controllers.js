'use strict';

koodohub_app.controller('HomeController', function($scope, $modal, $rootScope,
                                                   $cookieStore, UserProjectService){

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

  $scope.changeCover = function() {
    var modalInstance = $modal.open({
      templateUrl: 'partials/change_cover.html',
      controller: 'SettingsController',
      windowClass: 'changeCover-dialog'
    })
  }

  $scope.changeAvatar = function() {
    var modalInstance = $modal.open({
      templateUrl: 'partials/change_avatar.html',
      controller: 'SettingsController',
      windowClass: 'changeCover-dialog'
    })
  }

  $scope.userProjects = function () {
    $scope.displayProjects = [];
    UserProjectService.getUserProjects({username:$rootScope.user.username}, function(projects) {
      console.log(projects);
      console.log(projects.length);
      for (var i=0; i<projects.length; i++) {
        var project = {};
        console.log(projects[i]);
        project.id = projects[i].id;
        project.title = projects[i].title;
        project.user = projects[i].user;
        project.projectImage = projects[i].medialink.split(';')[0];
        project.description = projects[i].description;
        $scope.displayProjects.push(project);
      }
    });
  }

});

koodohub_app.controller('MembersController', function($scope, MemberService) {
  $scope.members = MemberService.query();
})

koodohub_app.controller('SettingsController', function($scope, $window, $timeout, SettingsService,
                                                       $upload) {
  $scope.upload = function(file) {
    console.log("upload file");
    $upload.upload ({
      url: 'resource/members/uploadAvatar',
      data: {myObj: $scope.myModelObj},
      file: $scope.user.avatar_file
    }).progress(function(evt) {
      $scope.uploadPercentage = parseInt(100.0 * evt.loaded / evt.total);
      console.log('progress: ' + $scope.uploadPercentage + '% file :'+ evt.config.file.name);
    }).success(function(data, status, headers, config) {
      $window.location.reload();
    });
  };

  $scope.uploadCover = function(file) {
    $upload.upload ({
      url: 'resource/members/uploadCover',
      data: {myObj: $scope.myModelObj},
      file: $scope.user.cover_file
    }).progress(function(evt) {
      $scope.uploadPercentage = parseInt(100.0 * evt.loaded / evt.total);
      console.log('progress: ' + $scope.uploadPercentage + '% file :'+ evt.config.file.name);
    }).success(function(data, status, headers, config) {
      $window.location.reload();
    });
  };

  $scope.updateEmail = function() {
    SettingsService.updateEmail({email:$scope.user.email});
  }
  $scope.updatePassword = function() {
    SettingsService.updatePassword({oldPassword:$scope.user.oldPassword,
      newPassword:$scope.user.newPassword});
  }
});

koodohub_app.controller('NewProjectController', function($scope, $rootScope, $timeout,
                                                             $q, ProjectService, $upload, $location) {
  $scope.fileReaderSupported = window.FileReader != null
    && (window.FileAPI == null || FileAPI.html5 != false);
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
        $location.path('/');
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

koodohub_app.controller('ActivateController', function($rootScope, $stateParams, ActivateService,
                                                       $location){
  console.log("activate account");
  ActivateService.get({email:$stateParams.email, token:$stateParams.token});
  $location.path("/");
  $rootScope.$broadcast('switchToSignInEvent');
})

koodohub_app.controller('MemberController', function($scope, $stateParams, MemberService){
  $scope.user = MemberService.get({username:$stateParams.username});
  $scope.memberProjects = function () {
    $scope.displayProjects = [];
    UserProjectService.getUserProjects({username:$rootScope.user.username}, function(projects) {
      for (var i=0; i<projects.length; i++) {
        var project = {};
        project.id = projects[i].id;
        project.title = projects[i].title;
        project.projectImage = projects[i].medialink.split(';')[0];
        project.description = projects[i].description;
        $scope.displayProjects.push(project);
        console.log($scope.displayProjects);
      }
    });
  }
});

koodohub_app.controller('ProjectController', function($scope, $stateParams, ProjectService){
  $scope.project = ProjectService.get({id:$stateParams.id});
  console.log($scope.project);
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