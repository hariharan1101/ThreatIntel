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
HTreeMap domainMap = db.hashMap("myMap1").createOrOpen();

String domainpara = request.getParameter("domainvalue");
int flag=0;
if(domainpara=="")
{
	out.println("<div style='color:red'>Please, enter a Domain name</div>");
}
else
{
for(int index =0;index<domainMap.size();index++)
	{
	if(String.valueOf(domainMap.get(String.valueOf(index))).trim().equals(String.valueOf(domainpara).trim()))
		{					
			out.println("<div style='color:red;'> \""+domainMap.get(String.valueOf(index))+"\" is a malicious domian </div>");
			flag=1;
		}
	}
if(flag==0)
{
	out.println("<div style='color:blue;'> \""+domainpara+"\" is not a malicious domain </div>");
}
}
%>
</body>
</html>