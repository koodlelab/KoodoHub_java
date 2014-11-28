var services = angular.module('koodohub.services', ['ngResource']);

services.factory('MemberService', function($resource) {
  return $resource('services/members/:username');
});


services.factory('SessionService', function($resource) {

  return $resource('services/session/:action', {},
    {
      authenticate: {
        method: 'POST',
        params: {'action' : 'authenticate'},
        headers : {'Content-Type': 'application/x-www-form-urlencoded'}
      }
    }
  );
});