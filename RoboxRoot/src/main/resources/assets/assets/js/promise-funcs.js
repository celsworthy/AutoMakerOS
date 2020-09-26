function promiseCommandToRoot(requestType, service, dataToSend)
{
	return new Promise(function(resolve, reject)
	{
		var printerURL = serverURL + "/api/" + service + "/";
        //console.log(printerURL);
		var base64EncodedCredentials = $.base64.encode(defaultUser + ":" + localStorage.getItem(applicationPINVar));
        var aData = 
            {
                url: printerURL,
                beforeSend: function (xhr) {
                        xhr.setRequestHeader("Authorization", "Basic " + base64EncodedCredentials);
                    },
                contentType: 'application/json', // send as JSON
                type: requestType,
                success:function (data, textStatus, jqXHR)
                    {
                        resolve(data);
                    },
                error:function (jqXHR, textStatus, errorThrown)
                    {
                        var statusCode = jqXHR.statusCode;
                        console.log('PromiseCommandToRoot error!');
                            console.log('    textStatus = \"' + textStatus + '\"');
                        if (errorThrown !== null)
                            console.log('    errorThrown = \"' + errorThrown + '\"');
                        var e = Error(jqXHR.statusText)
                        if (jqXHR.status === 500)
                            e.name = 'InternalError'
                        else
                            e.name = textStatus

                        reject(e);
                    }
            };

        if (dataToSend !== null)
        {
            aData.data = JSON.stringify(dataToSend);
        }
        $.ajax(aData);
     });
}

function promiseGetCommandToRoot(service, dataToSend)
{
    return promiseCommandToRoot('GET', service, dataToSend);
}

function promisePostCommandToRoot(service, dataToSend)
{
    return promiseCommandToRoot('POST', service, dataToSend);
}
