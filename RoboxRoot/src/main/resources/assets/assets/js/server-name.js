function saveServerName()
{
    var newName = $('#sname-input').val();
    promisePostCommandToRoot('admin/setServerName', newName)
        .then(goToPage(serverSettingsMenu))
        .catch(function(error) { handleException(error, 'server-name-save-error', false); });
}

function updateServerName(serverData)
{
    $('#sname-input').val(serverData.name);
    $('#right-button').removeClass('disabled')
}

function serverNameInit()
{
    $('#right-button').on('click', saveServerName);
    promiseGetCommandToRoot('discovery/whoareyou', null)
        .then(updateServerName)
        .catch(function(error) { handleException(error, 'server-name-init-error', true); });
}
