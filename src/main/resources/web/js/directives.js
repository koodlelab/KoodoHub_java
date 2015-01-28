'use strict';

koodohub_app.directive('hoverProjectImage', function() {
  return {
    restrict: 'E',
    link: function(scope, element, attrs) {
      element.on('mouseenter', function(event) {
        element.find('.project-text').css("height", "200px");
      });
      element.on('mouseleave', function(event) {
        element.find('.project-text').css("height", "70px");
      });
    }
  }
});

koodohub_app.directive('followingButton', ['$compile', function($compile) {
  return {
    restrict: 'E',
    replace: true,
    link: function(scope, element, attrs) {
      scope.$watch('following_user', function(html) {
        console.log("watching following_user"+scope.following_user);
        if (scope.following_user != null) {
          console.log("changing following Button:"+scope.following_user);
          var html = '';
          if (scope.following_user) {
            html = '<button class="btn btn-lg follow-member" ng-click="unfollowUser(member.username)">\n' +
              '       <i class="glyphicon glyphicon-minus"></i>Unfollow</button>\n'
          } else {
            html = '<button class="btn btn-lg follow-member" ng-click="followUser(member.username)">\n' +
              '       <i class="glyphicon glyphicon-plus"></i>Follow</button>\n'
          };
          console.log(html);
          element.html(html);
          $compile(element.contents())(scope);
        };
      }, true);
    }
  };
}]);

koodohub_app.directive('projectMediaFiles', ['$compile', 'ImageViewService',
  function ($compile, ImageViewService) {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        lsrc: '@'
      },
      link: function (scope, element, attrs) {
        attrs.$observe('lsrc', function(value) {
          if (value !== "") {
            console.log(value);
            var mediaLinks = value.split(';');
            console.log(mediaLinks.length);
            var html="";
            for (var i = 0; i<mediaLinks.length; i++) {
              if (mediaLinks[i] !== "") {
                var fileType = ImageViewService.parseFileTypeByName(mediaLinks[i]);
                var templateTemp = ImageViewService.defaultViewTemplates[fileType];

                var file_html = "\n" + templateTemp.replace(/\{type\}/g, fileType
                  + "/" + mediaLinks[i].split('.').pop())
                  .replace(/\{data\}/g, mediaLinks[i])
                  .replace(/\{width\}/g, "100%")
                  .replace(/\{controls\}/g, "controls");
                html += file_html;
              }
            }
            element.html(html);
            $compile(element.contents())(scope);
          }
        });
      }
    }
  }]);

koodohub_app.directive('projectImage', ['$compile', 'ImageViewService',
  function ($compile, ImageViewService) {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      src: '@'
    },
    link: function (scope, element, attrs) {
      var fileType = ImageViewService.parseFileTypeByName(scope.src);
      var templateTemp = ImageViewService.defaultViewTemplates[fileType];
      var html = "\n" + templateTemp.replace(/\{type\}/g, fileType+"/"+scope.src.split('.').pop())
        .replace(/\{data\}/g, scope.src).replace(/\{width\}/g, "280px").replace(/\{controls\}/g, "");
      element.html(html);
      $compile(element.contents())(scope);
    }
  }
}]);

koodohub_app.directive('dynamicPreview', ['$compile', 'ImageViewService',
  function ($compile, ImageViewService) {
  return {
    restrict: 'E',
    replace: true,
    link: function (scope, ele, attrs) {
      scope.$watch('project_files', function(html) {
        console.log("project_files changes.");
        var previewTemplate = '';
        for (var i = 0; i < scope.project_files.length; i++) {
          (function(file, parseFileType, defaultPreviewTemplates) {
            var vUrl = window.URL || window.webkitURL;
            var data = vUrl.createObjectURL(file)
            var fileType = ImageViewService.parseFileType(file);
            var templateTemp = ImageViewService.defaultPreviewTemplates[fileType];
            var footer = "<div class=\"file-thumbnail-footer\">\n" +
              "<div class=\"file-caption-name\" style=\"width: 150px;\">"+file.name+"</div>\n" +
              "<div class=\"file-actions\">\n"+
              "<div class=\"file-footer-buttons\">\n"+
              "   <button class=\"btn btn-xs btn-default\" title=\"Remove file\" " +
              "ng-click=\"removeFile(\'" +
              file.name+"\')\">" +
              "<i class=\"glyphicon glyphicon-trash text-danger\"></i></button>\n" +
              "   </div></div></div>";
            var html = "\n" + templateTemp.replace(/\{type\}/g, file.type)
              .replace(/\{data\}/g, data).replace(/\{footer\}/g, footer);
            previewTemplate += html;
          })(scope.project_files[i]);
        }
        if (scope.project_files.length > 0) {
          previewTemplate += ImageViewService.defaultPreviewTemplates['addLink'];
        }
        ele.html(previewTemplate);
        $compile(ele.contents())(scope);
      }, true);
    }
  };
}]);