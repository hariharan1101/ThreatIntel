fetch("webapi/fetchurl").then(Response => Response.json()).then(data => urljson=data)
fetch("webapi/fetchip").then(Response => Response.json()).then(data => ipjson=data)
fetch("webapi/fetchdomain").then(Response => Response.json()).then(data => domainjson=data)
fetch("webapi/fetchurlsize").then(Response => Response.json()).then(data => urlsize=data)
fetch("webapi/fetchipsize").then(Response => Response.json()).then(data=> ipsize=data)

function viewURLFN()
{	
	var urlstring="";
	for(let i=0;i<urlsize;i++)
		{
			urlstring=urlstring.concat(urljson[i]);
			urlstring=urlstring.concat("\n");
		}
	document.getElementById("arrPrint").innerHTML = urlstring;
}

function viewDOMAINFN()
{	
	var domainstring="";
	for(let i=0;i<urlsize;i++)
		{
			domainstring=domainstring.concat(domainjson[i]);
			domainstring=domainstring.concat("\n");
		}
	document.getElementById("arrPrint").innerHTML = domainstring;
}

function viewIPFN()
{	
	var ipstring="";
	for(let i=0;i<ipsize;i++)
		{
			ipstring=ipstring.concat(ipjson[i]);
			ipstring=ipstring.concat("\n");
		}
	document.getElementById("arrPrint").innerHTML = ipstring;
}

function searchURLFN()
{	var f1=0;
	for(let i=0;i<urlsize;i++)
		{		
		if(urljson[i].toString().trim()==(document.getElementById("urlsearch").value.toString().trim()))
			{	
				document.getElementById("area1").innerHTML="Malicious URL";
				f1=1;
			}
		}
	if(f1==0)
		{
			document.getElementById("area1").innerHTML="Not a Malicious URL";
		}	
}

function searchDOMAINFN()
{	var f3=0;
	for(let i=0;i<urlsize;i++)
		{
		if(domainjson[i].toString().trim()==document.getElementById("domainsearch").value.toString().trim())
			{
				document.getElementById("area2").innerHTML="Malicious Domain";
				f3=1;
			}
		}
	if(f3==0)
		{
			document.getElementById("area2").innerHTML="Not a Malicious Domain";
		}	
}

function searchIPFN()
{	var f2=0;
	for(let i=0;i<ipsize;i++)
		{
		if(ipjson[i].toString().trim()==document.getElementById("ipsearch").value.toString().trim())
			{
				document.getElementById("area3").innerHTML="Malicious IP";
				f2=1;
			}
		}
	if(f2==0)
		{
			document.getElementById("area3").innerHTML="Not a Malicious IP";
		}	
}