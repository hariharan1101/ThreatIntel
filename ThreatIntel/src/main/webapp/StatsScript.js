fetch("webapi/fetchurlsize").then(Response1 => Response1.json()).then(data1 => urlsize=data1)
fetch("webapi/fetchipsize").then(Response1 => Response1.json()).then(data1=> ipsize=data1)

function show()
{
	document.getElementById("stat1").value=urlsize
	document.getElementById("stat2").value=urlsize
	document.getElementById("stat3").value=ipsize
var xValues = ["IP","URLs","Domains",""];
var yValues = [ipsize,urlsize,urlsize,0];
var barColors = ["blue","yellow","green","white"];
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
}