'use strict';

var services = angular.module('koodohub.service', ['ngResource']);

services.factory('MemberService', function($resource) {
  return $resource('resource/members/:username');
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