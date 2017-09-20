/*
 CLEditor Icon Plugin v1.0
 http://premiumsoftware.net/cleditor
 requires CLEditor v1.2 or later

 Copyright 2010, Chris Landowski, Premium Software, LLC
 Dual licensed under the MIT or GPL Version 2 licenses.
 
 Adjusted for commentBox by Nick Russler
 
 Adjusted for PrimeFaces TextEditor by Marcel Trotzek
 */
function initEmoticonPlugin(namespace, baseIconURL, smileyCount, smileyPopupWidth) {
	// Constants
	var baseSplit = baseIconURL.split('1.gif')
		FOLDER = baseSplit[0],
		EXT = ".gif" + baseSplit[1],
		BUTTON_COUNT = smileyCount,
		POPUP_WIDTH = smileyPopupWidth;

	// Build the popup content
	var content = $('<div id="emoticon-menu" style="display: none; width: ' + POPUP_WIDTH + 'px">');
	for ( var x = 0; x < BUTTON_COUNT; x++) {
		$('<div><img src="' + FOLDER + (x + 1) + EXT + '" />').css("display", "inline-block").css('padding', '1px 2px').appendTo(content);
	}
	$('.comments-' + namespace).append(content);
}
	
function initEmoticonPopup(commentId, editor) {
	var topOffset = 25;
	var leftOffset = 6;
	var content = $(commentId + ' #emoticon-menu');

	$(editor.container).parents('form').find('.editor-emoticons').on('click', function(event) {
		var icon = $(this);
		var position = icon.position();
		
		icon.parents('form').append(content);
		
		content.css({'display': 'block', 'left': position.left + leftOffset + 'px', 'top': position.top + topOffset + 'px'});
		
		content.find('img').off('click').on('click', function() {
			var emoticon = $(this);
			
			var selection = editor.getSelection();
			var index = 0;
			if (selection) {
				if (selection.length == 0) {
				    index = selection.index;
			    } else {
			    	var text = editor.getText(selection.index, selection.length);
			    	editor.insertText(selection.index + selection.length, ' ');
			    	index = selection.index + selection.length + 1;
			    }
			} else {
				index = editor.getLength();
			}
			
			editor.insertEmbed(index, 'image', emoticon.attr('src'));
		});

		event.stopPropagation();
		event.preventDefault();
	});
	
	$('body').on('click', function() {
		content.css('display', 'none');
	});
}