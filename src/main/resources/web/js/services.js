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
      isArray: false}
  }, {
    'updatePassword': {
      method: 'GET',
      params: {path:'updatePassword'},
      isArray: false}
  })
});

services.factory('ProjectService', function($resource) {
  return $resource('resource/projects/:id');
});

services.factory('SessionService', function($resource) {
  return $resource('resource/session/:action', {},
    {
      authenticate: {
        method: 'POST',
        params: {'action' : 'authenticate'},
        headers : {'Content-Type': 'application/x-www-form-urlencoded'}
      }
    }
  );
});