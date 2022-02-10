<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="org.mapdb.DB,org.mapdb.HTreeMap,org.mapdb.DBMaker" %>
 <html>
 <script
src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.js">
</script>
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
int i = db.hashMap("myMap1").createOrOpen().size();
int j = db.hashMap("myMap1").createOrOpen().size();
int k = db.hashMap("myMap2").createOrOpen().size();
out.println("Malicious URLs Count: <input type='text' value='"+i+"' id='stat1'>");
out.println("Malicious Domains Count: <input type='text' value='"+j+"' id='stat2'>");
out.println("Malicious IPs Count: <input type='text' value='"+k+"' id='stat3'>");
%>
</div>

<h1 style="background-color: lightgrey;">Threat Count</h1>
<canvas id="myChart" style="width:100%;max-width:600px"></canvas> 

<h1 style="background-color: lightgrey;">Threat Distribution</h1>
<canvas id="myChart1" style="width:100%;max-width:600px"></canvas>

<script>
var urlc=document.getElementById("stat1").value;
var domainc=document.getElementById("stat2").value;
var ipc=document.getElementById("stat3").value;
var xValues = ["","IP","URLs", "Domains"];
var yValues = [0,ipc,urlc, domainc];
var barColors = ["white","blue","red", "green"];

new Chart("myChart", {
  type: "bar",
  data: {
    labels: xValues,
    datasets: [{
      backgroundColor: barColors,
      data: yValues
    }]
  },
  options: {
    legend: {display: false},
    title: {
      display: false,
      text: "Threat Count"
    }
  }
});

new Chart("myChart1", {
	  type: "pie",
	  data: {
	    labels: xValues,
	    datasets: [{
	      backgroundColor: barColors,
	      data: yValues
	    }]
	  },
	  options: {
	    title: {
	      display: false,
	      text: "Threat Distribution"
	    }
	  }
	});
</script>
</body>
</html>