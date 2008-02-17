var updateRowTimeoutId;

function updateRowControls(event, index)
{
	if(event.keyCode == null)
		return;
	else
	{
      switch(event.keyCode) {
       case Event.KEY_TAB:
       case Event.KEY_RETURN:
       case Event.KEY_ESC:
       case Event.KEY_LEFT:
       case Event.KEY_RIGHT:
       case Event.KEY_UP:
       case Event.KEY_DOWN:
         return;
      }
	}

	clearTimeout(updateRowTimeoutId);
	updateRowTimeoutId = setTimeout(
		"eval('updateRowAfterKeyPath_" + index + "();');", 500);
}

function startBusyForRow(index)
{
	$('busy_' + index).setStyle({ visibility: 'visible' });
}

function stopBusyForRow(index)
{
	$('busy_' + index).setStyle({ visibility: 'hidden' });
}
