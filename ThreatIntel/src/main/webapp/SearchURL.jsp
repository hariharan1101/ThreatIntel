<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="org.mapdb.DB,org.mapdb.HTreeMap,org.mapdb.DBMaker" %>
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
