<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="org.mapdb.DB,org.mapdb.HTreeMap,org.mapdb.DBMaker" %>
 <html>
 <title>ThreatView</title>
<body style="background-color: powderblue;">
<h2>Data Polled Successfully</h2>
<form action="ViewURL.jsp"><input type=submit value="View URLs"/></form>
<form action="SearchURL.jsp"><input type="text" name="urlvalue"> <br><input type=submit value="Search URL"/></form>
<br>
<br>
<br>
<form action="ViewDomain.jsp"><input type=submit value="View Domains"/></form>
<form action="SearchDomains.jsp"><input type="text" name="domainvalue"> <br><input type=submit value="Search Domains"/></form>
<br>
<br>
<br>
<form action="ViewIP.jsp"><input type=submit value="View IPs"/></form>
<form action="SearchIP.jsp"><input type="text" name="IPvalue"> <br><input type=submit value="Search IPs"/></form>
<br>
<br>
<br>
<form action="Stats.jsp"><input type=submit value="Show statistics"/></form>


<%
DB db = (DB) session.getAttribute("db");
HTreeMap urlMap = db.hashMap("myMap").createOrOpen();

String urlpara = request.getParameter("urlvalue");
int flag=0;
if(urlpara=="")
{
	out.println("<div style='color:red'>Please, enter an URL</div>");
}
else
{
for(int index =0;index<urlMap.size();index++)
	{
	if(String.valueOf(urlMap.get(String.valueOf(index))).trim().equals(String.valueOf(urlpara).trim()))
		{					
			out.println("<div style='color:red;'> \""+urlMap.get(String.valueOf(index))+"\" is a malicious website url </div>");
			flag=1;
		}
	}

if(flag==0)
{
	out.println("<div style='color:blue;'> \""+urlpara+"\" is not a malicious website url </div>");
}
}
%>
</body>
</html>