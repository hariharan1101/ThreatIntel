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
HTreeMap ipMap = db.hashMap("myMap2").createOrOpen();

String ippara = request.getParameter("IPvalue");
int flag=0;
if(ippara=="")
{
	out.println("<div style='color:red'>Please, enter an IP address</div>");
}
else
{
for(int index =0;index<ipMap.size();index++)
	{
	if(String.valueOf(ipMap.get(String.valueOf(index))).trim().equals(String.valueOf(ippara).trim()))
		{					
			out.println("<div style='color:red;'> \""+ipMap.get(String.valueOf(index))+"\" is a malicious IP </div>");
			flag=1;
		}
	}
if(flag==0)
{
	out.println("<div style='color:blue;'> \""+ippara+"\" is not a malicious IP </div>");
}
}
%>
</body>
</html>