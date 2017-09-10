CKEDITOR.plugins.add( 'inlinecancel',
{
	init: function( editor )
	{
		editor.addCommand( 'inlinecancel',
			{
				exec : function( editor )
				{    
					//if(confirm("Cancel and reload page? (All progress since last save will be lost.)"))
					//location.reload(true);
						
					//var callback = editor.element.$.cancelCallback || function(editor, data) { console.log("Please add a cancelCallback property to your element."); }; 
					//callback(editor, data);
					
					var element = editor.element.$;
					element.restoreAndClose();
				}
			});
		editor.ui.addButton( 'Inlinecancel',
		{
			label: 'Cancel edit- revert changes',
			command: 'inlinecancel',
			icon: this.path + 'images/inlinecancel.png'
		} );
	}
} );