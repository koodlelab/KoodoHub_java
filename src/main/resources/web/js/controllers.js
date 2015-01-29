'use strict';

koodohub_app.controller('HomeController', ['$scope', '$modal','$rootScope',
  '$cookieStore', 'UserService', function($scope, $modal, $rootScope,
                                                   $cookieStore, UserService){
  $scope.$watch('user.username', function() {
    if ($scope.user) {

      UserService.getFollowings({username: $scope.user.username}, function (followings) {
        $scope.user.followings = followings;
      });

      UserService.getFollowers({username: $scope.user.username}, function (followers) {
        $scope.user.followers = followers;
      });

      $rootScope.userDisplayProjects = [];

      $rootScope.portfolioProjects = [];

      UserService.getUserProjects({username:$rootScope.user.username, includeFollowing:true}, function(projects) {
        for (var i=0; i<projects.length; i++) {
          var project = {};
          project.id = projects[i].id;
          project.title = projects[i].title;
          project.user = projects[i].user;
          project.projectImage = projects[i].medialink.split(';')[0];
          project.description = projects[i].description;
          project.createdOn = projects[i].createdOn;
          if (project.user.username === $rootScope.user.username) {
            $rootScope.portfolioProjects.push(project);
          }
          $rootScope.userDisplayProjects.push(project);
        }
      });
    }
  });

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
  };

  $scope.changeCover = function() {
    var modalInstance = $modal.open({
      templateUrl: 'partials/change_cover.html',
      controller: 'SettingsController',
      windowClass: 'changeCover-dialog'
    })
  };

  $scope.changeAvatar = function() {
    var modalInstance = $modal.open({
      templateUrl: 'partials/change_avatar.html',
      controller: 'SettingsController',
      windowClass: 'changeCover-dialog'
    })
  };

  $scope.isCurrentUser = function(user) {
    var isCurrentUser = ($rootScope.user.username === user);
    return isCurrentUser;
  };

}]);

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
                                                             $q, ProjectService, UserService,
                                                             $upload, $location) {
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
      $scope.project.$save(function(response) {
        UserService.getUserProjects({username:$rootScope.user.username, includeFollowing:false}, function(projects) {
          for (var i=0; i<projects.length; i++) {
            var project = {};
            project.id = projects[i].id;
            project.title = projects[i].title;
            project.user = projects[i].user;
            project.projectImage = projects[i].medialink.split(';')[0];
            project.description = projects[i].description;
            project.createdOn = projects[i].createdOn;
            $scope.displayProjects.push(project);
          }
        });
        $location.path('/');
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
  ActivateService.get({email:$stateParams.email, token:$stateParams.token});
  $location.path("/");
  $rootScope.$broadcast('switchToSignInEvent');
});

koodohub_app.controller('MemberController', function($rootScope,$scope, $stateParams,
                                                     MemberService, UserService){

  $scope.member = MemberService.get({username:$stateParams.username});
  $scope.$watch('member.username', function() {
    if ($scope.member.username) {
      console.log("name:"+$scope.member.username);
      $scope.displayProjects = [];
      UserService.getUserProjects({username: $scope.member.username},
        function (projects) {
          for (var i = 0; i < projects.length; i++) {
            var project = {};
            project.id = projects[i].id;
            project.title = projects[i].title;
            project.projectImage = projects[i].medialink.split(';')[0];
            project.description = projects[i].description;
            project.createdOn = projects[i].createdOn;
            $scope.displayProjects.push(project);
            console.log($scope.displayProjects);
          }
        });
      refreshFollowers($stateParams.username);
      UserService.getFollowings({username:$stateParams.username}, function(followings) {
        $scope.member.followings = followings;
      });
    }
  });

  var refreshStatus = function(followers) {
    $scope.following_user = false;
    $scope.member.followers = followers;
    for (var i = 0; i< followers.length; i++) {
      if (followers[i].username === $rootScope.user.username) {
        $scope.following_user = true;
        break;
      }
    }
    UserService.getFollowings({username: $scope.user.username}, function (followings) {
      $scope.user.followings = followings;
    });

    //TODO
    $rootScope.portfolioProjects = [];
    $rootScope.userDisplayProjects = [];
    UserService.getUserProjects({username:$rootScope.user.username, includeFollowing:true}, function(projects) {
      for (var i=0; i<projects.length; i++) {
        var project = {};
        project.id = projects[i].id;
        project.title = projects[i].title;
        project.user = projects[i].user;
        project.projectImage = projects[i].medialink.split(';')[0];
        project.description = projects[i].description;
        project.createdOn = projects[i].createdOn;
        if (project.user.username === $rootScope.user.username) {
          $rootScope.portfolioProjects.push(project);
        }
        $rootScope.userDisplayProjects.push(project);
      }
      console.log("user displayproject:"+$rootScope.userDisplayProjects.length);
    });
  }

  var refreshFollowers = function(user) {
    UserService.getFollowers({username:user}, function(followers) {
      refreshStatus(followers);
    });
  }

  $scope.followUser = function(user) {
    console.log("follow user "+user);
    UserService.followUser({username:user}, function(response) {
      refreshStatus(response.data);
    });
  };

  $scope.unfollowUser = function(user) {
    console.log("unfollow user "+user);
    UserService.unfollowUser({username:user}, function(response) {
      refreshStatus(response.data);
    });
  };
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