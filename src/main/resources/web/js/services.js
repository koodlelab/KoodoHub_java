'use strict';

var services = angular.module('koodohub.service', ['ngResource']);

services.factory('MemberService', function($resource) {
  return $resource('resource/members/:username');
});

services.factory('ActivateService', function ($resource) {
  return $resource('resource/members/activateAccount/:email/:token', {}, {
    'get': { method: 'GET', params: {}, isArray: false}
  });
});

services.factory('SettingsService', function ($resource) {
  return $resource('resource/members/:path', {path: '@path'
  }, {
    'updateEmail': {
      method: 'GET',
      params: {path:'updateEmail'},
      isArray: false
    },

    'updatePassword': {
      method: 'GET',
      params: {path:'updatePassword'},
      isArray: false
    }
  });
});

services.factory('UserService', function($resource) {
  return $resource('resource/members/:path', {path: '@path'}, {
    'getUserProjects': {
      method: 'GET',
      params: {path:'getProjects'},
      isArray: true
    },
    'getAllInterestedProjects': {
      method: 'GET',
      params: {path: 'getAllInterestedProjects'},
      isArray: true
    },
    'followUser': {
      method: 'GET',
      params: {path: 'follow'},
      isArray: false
    },
    'unfollowUser': {
      method: 'GET',
      params: {path: 'unfollow'},
      isArray: false
    },
    'getFollowings': {
      method: 'GET',
      params: {path: 'getFollowings'},
      isArray: true
    },
    'getFollowers': {
      method: 'GET',
      params: {path: 'getFollowers'},
      isArray: true
    }
  })
});

services.factory('ImageViewService', function() {
  this.defaultPreviewTypes = ['image', 'video'];
  this.defaultFileTypeSettings = {
    image: function(vType, vName) {
      return typeof vType !== "undefined" && vType.match('image.*')
      ||  vName.match(/\.(gif|png|jpe?g)$/i);
    },
    video: function (vType, vName) {
      return typeof vType !== "undefined" && vType.match(/\.video\/(ogg|mp4|webm)$/i)
        || vName.match(/\.(og?|mp4|webm)$/i);
    }
  };
  this.defaultPreviewTemplates = {
    image: '<div class="file-preview-frame">\n' +
      '   <img src="{data}" width="180" height="140">\n' +
      '   {footer}\n' +
      '</div>\n',
    video: '<div class="file-preview-frame">\n' +
      '   <video width="180" height="135" controls>\n' +
      '       <source src="{data}" type="{type}"> </video>\n' +
      '   {footer}\n' +
      '</div>\n',
    addLink: '<div class="add-project-file-frame">\n' +
      '   <div class="file-button" ng-file-select ng-model="new_files" ng-required="true"\n'+
      '    ng-multiple="true" allow-dir="true" accept="image/*,video/*" ' +
      'ng-file-change="addFiles(new_files)">\n'+
      '    <span class="glyphicon glyphicon-camera"></span>\n'+
      '    <span class="glyphicon glyphicon-facetime-video"></span>\n'+
      '    </div></div>\n'
  };
  this.defaultViewTemplates = {
    image: '<img src="{data}" width="{width}" height="auto">',
    video: '<video width="{width}" height="auto" {controls}><source src="{data}" type="{type}"/></video>'
  };
  this.parseFileTypeByName = function(fileName) {
    var isValid, vType;
    for (var i = 0; i < this.defaultPreviewTypes.length; i++) {
      var cat = this.defaultPreviewTypes[i];
      vType = this.defaultFileTypeSettings[cat]("undefined", fileName) ? cat : '';
      if (vType != '') {
        return vType;
      }
    }
    return 'other';
  }

  this.parseFileType = function(file) {
    var isValid, vType;
    for (var i = 0; i < this.defaultPreviewTypes.length; i++) {
      var cat = this.defaultPreviewTypes[i];
      vType = this.defaultFileTypeSettings[cat](file.type, file.name) ? cat : '';
      if (vType != '') {
        return vType;
      }
    }
    return 'other';
  };
  return this;
});

services.factory('ProjectService', function($resource) {
  return $resource('resource/projects/:id');
});

services.factory('SessionService', function($resource) {
  return $resource('resource/session/:action', {}, {
      authenticate: {
        method: 'POST',
        params: {'action' : 'authenticate'},
        headers : {'Content-Type': 'application/x-www-form-urlencoded'}
      }
    }
  );
});