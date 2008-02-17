/**
 * Displays the busy indicator for the assignment container, hiding the
 * current assignment list.
 */
function startBusy()
{
	$('assignmentContainer').hide();
	$('assignmentContainerBusy').show();
}

/**
 * Hides the busy indicator for the assignment container and displays the
 * current assignment list.
 */
function stopBusy()
{
	$('assignmentContainerBusy').hide();
	$('assignmentContainer').show();
}
