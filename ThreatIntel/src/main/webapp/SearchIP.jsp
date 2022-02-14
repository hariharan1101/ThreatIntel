<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="org.mapdb.DB,org.mapdb.HTreeMap,org.mapdb.DBMaker" %>
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
