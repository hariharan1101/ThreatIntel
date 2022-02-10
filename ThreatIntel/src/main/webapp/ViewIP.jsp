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

<div style="background-color:lightgrey;">

<%
DB db = (DB) session.getAttribute("db");
HTreeMap ipMap = db.hashMap("myMap2").createOrOpen();
int i=1;
for(int index =0;index<ipMap.size();index++)
	{					
			out.println((index+1)+") "+ipMap.get(String.valueOf(index)));
			out.println("<br>");			
	}
%>
</div>
</body>
</html>