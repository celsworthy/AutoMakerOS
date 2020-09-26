function probeServer()
{
    var aData = 
        {
            url: 'http://localhost:8080/api/discovery/whoareyou/',
            beforeSend: function (xhr)
                        {
                            xhr.setRequestHeader('Authorization', 'Basic ' + $.base64.encode('root:1111'));
                        },
            type: 'POST',
            success:function (data, textStatus, jqXHR)
                    {
                        // The server has responded, so go to the index page.
                        document.getElementById('linkToIndex').click();
                    },
            error:function (jqXHR, textStatus, errorThrown)
                  {
                    if (jqXHR.status != 0)
                    {
                        // The server has responded so go to the index page.
                        document.getElementById('linkToIndex').click();
                    }
                  }
        };
    $.ajax(aData);
}

setInterval(probeServer, 1000);