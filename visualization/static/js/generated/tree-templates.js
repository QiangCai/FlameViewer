// This file was automatically generated from tree-templates.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace templates.tree.
 * @public
 */

if (typeof templates == 'undefined') { var templates = {}; }
if (typeof templates.tree == 'undefined') { templates.tree = {}; }


templates.tree.getCallTreeSection = function(opt_data, opt_ignored) {
    return soydata.VERY_UNSAFE.ordainSanitizedHtml('<section><h2 class="call-tree-header">' + soy.$$escapeHtml(opt_data.threadId) + '</h2><canvas id="canvas-' + soy.$$escapeHtmlAttribute(opt_data.threadId) + '" width="' + soy.$$escapeHtmlAttribute(opt_data.canvasWidth) + '" height="' + soy.$$escapeHtmlAttribute(opt_data.canvasHeight) + '" style="margin-left:' + soy.$$escapeHtmlAttribute(soy.$$filterCssValue(opt_data.canvasOffset)) + 'px"></canvas><canvas id="canvas-zoomed-' + soy.$$escapeHtmlAttribute(opt_data.threadId) + '" width="' + soy.$$escapeHtmlAttribute(opt_data.canvasWidth) + '" height="' + soy.$$escapeHtmlAttribute(opt_data.canvasHeight) + '" style="display: none;"></canvas></section>');
};
if (goog.DEBUG) {
  templates.tree.getCallTreeSection.soyTemplateName = 'templates.tree.getCallTreeSection';
}


templates.tree.getAccumulativeTreeSection = function(opt_data, opt_ignored) {
    return soydata.VERY_UNSAFE.ordainSanitizedHtml('<section>' + ((opt_data.header) ? '<h2 class="accumulative-tree-header">' + soy.$$escapeHtml(opt_data.header) + '</h2>' : '') + '<canvas id="canvas" width="' + soy.$$escapeHtmlAttribute(opt_data.canvasWidth) + '" height="' + soy.$$escapeHtmlAttribute(opt_data.canvasHeight) + '"></canvas><canvas id="canvas-zoomed" width="' + soy.$$escapeHtmlAttribute(opt_data.canvasWidth) + '" height="' + soy.$$escapeHtmlAttribute(opt_data.canvasHeight) + '" style="display: none;"></canvas></section>');
};
if (goog.DEBUG) {
  templates.tree.getAccumulativeTreeSection.soyTemplateName = 'templates.tree.getAccumulativeTreeSection';
}


templates.tree.callTreePopup = function(opt_data, opt_ignored) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('<div class="popup"><h3>' + soy.$$escapeHtml(opt_data.className) + '.<b>' + soy.$$escapeHtml(opt_data.methodName) + '</b></h3><p>Start time: ' + soy.$$escapeHtml(opt_data.startTime) + 'ms</p><p>Duration: ' + soy.$$escapeHtml(opt_data.duration) + 'ms</p></div>');
};
if (goog.DEBUG) {
  templates.tree.callTreePopup.soyTemplateName = 'templates.tree.callTreePopup';
}


templates.tree.accumulativeTreePopup = function(opt_data, opt_ignored) {
    return soydata.VERY_UNSAFE.ordainSanitizedHtml('<div class="popup"><ul><li><a class="outgoing-link">Outgoing Calls</a></li><li><a class="incoming-link">Incoming Calls</a></li></ul><h3></h3><p class="p-calls-num"></p></div>');
};
if (goog.DEBUG) {
  templates.tree.accumulativeTreePopup.soyTemplateName = 'templates.tree.accumulativeTreePopup';
}


templates.tree.listOfFiles = function(opt_data, opt_ignored) {
  var output = '<ol class="file-list">';
    var fileNameList61 = opt_data.fileNames;
    var fileNameListLen61 = fileNameList61.length;
    for (var fileNameIndex61 = 0; fileNameIndex61 < fileNameListLen61; fileNameIndex61++) {
        var fileNameData61 = fileNameList61[fileNameIndex61];
        output += '<li id="' + soy.$$escapeHtmlAttribute(fileNameData61) + '"><a href="/flamegraph-profiler/' + soy.$$escapeHtmlAttribute(opt_data.pageName) + '?file=' + soy.$$escapeUri(fileNameData61) + '&amp;project=' + soy.$$escapeUri(opt_data.projectName) + '">' + soy.$$escapeHtml(fileNameData61) + '</a></li>';
  }
  output += '</ol>';
  return soydata.VERY_UNSAFE.ordainSanitizedHtml(output);
};
if (goog.DEBUG) {
  templates.tree.listOfFiles.soyTemplateName = 'templates.tree.listOfFiles';
}


templates.tree.fileInput = function (opt_data, opt_ignored) {
    return soydata.VERY_UNSAFE.ordainSanitizedHtml('<form class="file-form"><input type="file" name="file" id="file" class="inputfile" multiple/><label for="file"><span>Upload file</span></label><p class="file-form-header">.jfr or .ser</p></form>');
};
if (goog.DEBUG) {
    templates.tree.fileInput.soyTemplateName = 'templates.tree.fileInput';
}
