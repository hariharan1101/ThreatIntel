<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="org.mapdb.DB,org.mapdb.HTreeMap,org.mapdb.DBMaker" %>
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
