/*
dojo.declare("webcat.reporter.GeneratedReportWatcher", null,
{
    // ----------------------------------------------------------
    constructor: function(pageRPC, pageContainerID)
    {
        this._pageRPC = pageRPC;
        this._currentPage = 0;
        this._pagesSoFar = 0;
        this._isComplete = false;
        this._pageContainerID = pageContainerID;
    },
    
    
    // ----------------------------------------------------------
    initializeFromCompleteReport: function(numPages)
    {
        this._currentPage = 1;
        this._pagesSoFar = numPages;
        this._isComplete = true;
    },


    // ----------------------------------------------------------
    start: function()
    {
        dojo.style("progressArea", "display", "block");

        this._interval = setInterval(dojo.hitch(this, function()
        {        
            this._pageRPC.pollReportStatus(dojo.hitch(this, function(result, e)
            {
                dijit.byId("reportProgressBar").update({
                    'progress': result.progress
                });

                this._isComplete = result.isComplete;
                this._pagesSoFar = result.highestRenderedPageNumber;

                if (this._pagesSoFar == 0 && result.highestRenderedPageNumber > 0)
                {
                    // If the first page just got rendered for the first time,
                    // go ahead and force-refresh the content pane so that it
                    // gets displayed to the user.

                    this._currentPage = 1;
                    this._loadPage();
                    this._updatePageIndicator();
                }
                else
                {
                    this._updatePageIndicator();
                }
                
                if (result.isComplete == true)
                {
                    this.stop();
                    dojo.style("progressArea", "display", "none");
                }
            }));
        }), 5000);
    },
    
    
    // ----------------------------------------------------------
    _loadPage: function()
    {
        this._pageRPC.setCurrentPageNumber(dojo.hitch(this, function()
        {
            dijit.byId("reportPageContainer").refresh();
            this._updatePageIndicator();
        }), this._currentPage);
    },
    
    
    // ----------------------------------------------------------
    _updatePageIndicator: function()
    {
        var pageIndicator = "Page "
           + this._currentPage + " (of " + this._pagesSoFar;
           
        pageIndicator += (this._isComplete == true) ?
            " total)" : " so far)";
        dojo.byId("pageIndicator").innerHTML = pageIndicator;
    },


    // ----------------------------------------------------------
    stop: function()
    {
        clearInterval(this._interval);
        delete this._interval;
    },
    
    
    // ----------------------------------------------------------
    goToFirstPage: function()
    {
        if (this._pagesSoFar == 0) return;

        this._currentPage = 1;
        this._loadPage();
    },
    
    
    // ----------------------------------------------------------
    goToPreviousPage: function()
    {
        if (this._pagesSoFar == 0) return;

        if (this._currentPage > 1)
            this._currentPage--;

        this._loadPage();
    },

    
    // ----------------------------------------------------------
    goToNextPage: function()
    {
        if (this._pagesSoFar == 0) return;

        if (this._currentPage < this._pagesSoFar)
            this._currentPage++;

        this._loadPage();
    },


    // ----------------------------------------------------------
    goToLastPage: function()
    {
        if (this._pagesSoFar == 0) return;

        this._currentPage = this._pagesSoFar;
        this._loadPage();
    }
});
*/
