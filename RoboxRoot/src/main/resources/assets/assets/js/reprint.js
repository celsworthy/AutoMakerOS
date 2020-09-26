function reprintJob()
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
        var printJobID = $(this).attr('job-id');
        promisePostCommandToRoot(selectedPrinter + "/remoteControl/reprintJob",
                                 printJobID)
            .then(goToHomePage);
    }
}

function updateSuitableJobData(suitablePrintJobs, detailsPrefix, printFunction)
{
    var currentPage =  getUrlParameter('p');
    if (suitablePrintJobs.jobs.length == 0 ||
        currentPage == null)
        currentPage = 0;
    else
        currentPage = parseInt(currentPage);
    
    var jobsPerPage = 4;
    var startIndex = currentPage * jobsPerPage;

    if (suitablePrintJobs.status == "OK") 
    {
        $("#navigator").removeClass('rbx-hidden')
                        .parent()
                        .removeClass('inactive')
                        .addClass('active')
        $("#job-row-none").addClass('rbx-hidden')
        for (let jobIndex = 0; jobIndex < jobsPerPage; jobIndex++)
        {
            var jobRow = "#job-row-" + (jobIndex + 1);
            var pjIndex = startIndex + jobIndex;
            if (pjIndex < suitablePrintJobs.jobs.length)
            {
                var job = suitablePrintJobs.jobs[pjIndex]

                $(jobRow).removeClass('rbx-hidden')
                         .attr('job-id', job.printJobID)
                         .attr('job-path', job.printJobPath)
                         .off('click') // Remove all callbacks
                         .on('click', printFunction);
                $(jobRow + " .job-name").html(job.printJobName);
                $(jobRow + " .job-created").html(nbsp);
                $(jobRow + " .job-duration").html(secondsToHM(job.durationInSeconds));
                $(jobRow + " .job-profile").html(job.printProfileName);
            }
            else
            {
                $(jobRow).addClass('rbx-hidden')
                         .attr('job-id', "")
                         .off('click');
                $(jobRow + " .job-name").html(nbsp);
                $(jobRow + " .job-created").html(nbsp);
                $(jobRow + " .job-duration").html(nbsp);
                $(jobRow + " .job-profile").html(nbsp);
            }
        }
    }
    else
    {
        $("#navigator").addClass('rbx-hidden')
                       .parent()
                       .removeClass('active')
                       .addClass('inactive')
        $("#job-row-none").removeClass('rbx-hidden')
        $(".rbx-reprint-job").addClass('rbx-hidden')
                              .attr('job-id', "")
                              .attr('job-path', "")
                              .off('click');
        $(".rbx-reprint-job" + " .job-name").html(nbsp);
        $(".rbx-reprint-job" + " .job-created").html(nbsp);
        $(".rbx-reprint-job" + " .job-duration").html(nbsp);
        $(".rbx-reprint-job" + " .job-profile").html(nbsp);
        $("#job-row-none").removeClass('rbx-hidden')
        $("#" + detailsPrefix + "details").html(i18next.t(detailsPrefix+ suitablePrintJobs.status.toLowerCase()));
    }

    if (startIndex == 0)
        $('#previous-sub-button').addClass('disabled')
                                 .attr('href', '#');
    else
    {   
        $('#previous-sub-button').removeClass('disabled')
                                 .attr('href', reprintPage + '?p=' + (currentPage - 1));
    }
    if (startIndex + jobsPerPage >= suitablePrintJobs.jobs.length)
        $('#next-sub-button').addClass('disabled')
                             .attr('href', '#');
    else
        $('#next-sub-button').removeClass('disabled')
                             .attr('href', reprintPage + '?p=' + (currentPage + 1));

    var pageNumber = i18next.t('page-x-of-n');
    var nPages = Math.floor(suitablePrintJobs.jobs.length / jobsPerPage);
    if ((suitablePrintJobs.jobs.length % jobsPerPage) > 0 || nPages == 0)
        nPages++;

    pageNumber = pageNumber.replace('$1', currentPage + 1)
                           .replace('$2', nPages);
    $('#page-number').html(pageNumber);
}

function updateReprintData(suitablePrintJobs)
{
    updateSuitableJobData(suitablePrintJobs, "no-reprints-", reprintJob)
}

function reprintInit()
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
        setMachineLogo();
        promisePostCommandToRoot(selectedPrinter + '/remoteControl/listReprintableJobs', null)
                    .then(updateReprintData)
                    .catch(function(error) { handleException(error, 'reprint-init-error', true); });
    }
	else
		goToHomeOrPrinterSelectPage();
}
