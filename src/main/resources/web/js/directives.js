'use strict';

koodohub_app.directive('dynamicPreview', function ($compile) {
  return {
    restrict: 'E',
    replace: true,
    link: function (scope, ele, attrs) {
      var defaultPreviewTypes = ['image', 'video'];
      var defaultFileTypeSettings = {
        image: function(vType, vName) {
          return (typeof vType !== "undefined") ? vType.match('image.*') : vName.match(/\.(gif|png|jpe?g)$/i);
        },
        video: function (vType, vName) {
          return typeof vType !== "undefined" && vType.match(/\.video\/(ogg|mp4|webm)$/i)
            || vName.match(/\.(og?|mp4|webm)$/i);
        }
      };
      var defaultPreviewTemplates = {
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
          '    ng-multiple="true" allow-dir="true" accept="image/*,video/*" ng-file-change="addFiles(new_files)">\n'+
          '    <span class="glyphicon glyphicon-camera"></span>\n'+
          '    <span class="glyphicon glyphicon-facetime-video"></span>\n'+
          '    </div></div>\n'
      };
      var parseFileType = function(file) {
        var isValid, vType;
        for (var i = 0; i < defaultPreviewTypes.length; i++) {
          var cat = defaultPreviewTypes[i];
          vType = defaultFileTypeSettings[cat](file.type, file.name) ? cat : '';
          if (vType != '') {
            return vType;
          }
        }
        return 'other';
      };
      scope.$watch('project_files', function(html) {
        var previewTemplate = '';
        for (var i = 0; i < scope.project_files.length; i++) {
          (function(file, parseFileType, defaultPreviewTemplates) {
            var vUrl = window.URL || window.webkitURL;
            var data = vUrl.createObjectURL(file)
            var fileType = parseFileType(file);
            var templateTemp = defaultPreviewTemplates[fileType];
            var footer = "<div class=\"file-thumbnail-footer\">\n" +
              "<div class=\"file-caption-name\" style=\"width: 150px;\">"+file.name+"</div>\n" +
              "<div class=\"file-actions\">\n"+
              "<div class=\"file-footer-buttons\">\n"+
              "   <button class=\"btn btn-xs btn-default\" title=\"Remove file\" ng-click=\"removeFile(\'"
              +file.name+"\')\">" + "<i class=\"glyphicon glyphicon-trash text-danger\"></i></button>\n" +
              "   </div></div></div>";
            var html = "\n" + templateTemp.replace(/\{type\}/g, file.type)
              .replace(/\{data\}/g, data).replace(/\{footer\}/g, footer);
            previewTemplate += html;
          })(scope.project_files[i], parseFileType, defaultPreviewTemplates);
        }
        if (scope.project_files.length > 0) {
          previewTemplate += defaultPreviewTemplates['addLink'];
        }
        ele.html(previewTemplate);
        $compile(ele.contents())(scope);
      }, true);
    }
  };
});