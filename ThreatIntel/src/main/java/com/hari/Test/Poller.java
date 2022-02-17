package com.hari.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/Poller")
public class Poller extends HttpServlet{	
	static JSONObject urlv;
	static JSONObject domainv;
	static JSONObject ipv;
	static int urlsize;
	static int ipsize;
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
	{	
		
		String d1,d2,t1,t2;
		 d1=req.getParameter("date1").trim();
		 d2=req.getParameter("date2").trim();
		 t1=req.getParameter("time1").trim();
		 t2=req.getParameter("time2").trim();
		 
		//POST REQUEST-1
		String request= "<taxii_11:Poll_Request\r\n"
				+ "    xmlns:taxii_11=\"http://taxii.mitre.org/messages/taxii_xml_binding-1.1\"\r\n"
				+ "    message_id=\"1\"\r\n"
				+ "    collection_name=\"guest.phishtank_com\">\r\n"
				+ "    <taxii_11:Exclusive_Begin_Timestamp>"+d1+"T"+t1+"Z</taxii_11:Exclusive_Begin_Timestamp>\r\n"
				+ "    <taxii_11:Inclusive_End_Timestamp>"+d2+"T"+t2+"Z</taxii_11:Inclusive_End_Timestamp>\r\n"
				+ "    <taxii_11:Poll_Parameters allow_asynch=\"false\">\r\n"
				+ "        <taxii_11:Response_Type>FULL</taxii_11:Response_Type>\r\n"
				+ "    </taxii_11:Poll_Parameters>\r\n"
				+ "</taxii_11:Poll_Request>";
		
		
		
		         
		HttpSession session = req.getSession();	  //SESSION FOR SHARING "DB"(database) ACROSS SERVLET AND JSPs
		PrintWriter out = res.getWriter();
		
		//SETTING UP URL CONNECTION FOR REQUEST-1
		URL url = new URL("http://hailataxii.com/taxii-data");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		//CONNECTION TIMEOUT OF 20
		connection.setConnectTimeout(20000);
		connection.setReadTimeout(20000);
		connection.setDoOutput(true);
		connection.setUseCaches(true);
		
		//CONTENT HEADERS
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("X-TAXII-Content-Type", "urn:taxii.mitre.org:message:xml:1.1");
		
		//SENDING REQUEST
		OutputStream outputStream = connection.getOutputStream();
		byte[] b = request.getBytes("UTF-8");
		outputStream.write(b);
		outputStream.flush();
		outputStream.close();		
		
		//GETTING RESPONSE
		InputStream inputStream = connection.getInputStream();
		byte[] res1 = new byte[2048];
		int i = 0;
		StringBuilder response = new StringBuilder();
		while ((i = inputStream.read(res1)) != -1) {
			
			//THE SERVER RESPONSE(STIX XML) FOR REQUEST-1 IS STORED IN "response"
			response.append(new String(res1, 0, i));
		}
		inputStream.close();
		
		
		
		//POST REQUEST-1
		String request1 = "<taxii_11:Poll_Request\r\n"
				+ "    xmlns:taxii_11=\"http://taxii.mitre.org/messages/taxii_xml_binding-1.1\"\r\n"
				+ "    message_id=\"1\"\r\n"
				+ "    collection_name=\"guest.Abuse_ch\">\r\n"
				+ "    <taxii_11:Exclusive_Begin_Timestamp>2018-01-10T02:00:00Z</taxii_11:Exclusive_Begin_Timestamp>\r\n"
				+ "    <taxii_11:Inclusive_End_Timestamp>2018-05-25T03:00:00Z</taxii_11:Inclusive_End_Timestamp>\r\n"
				+ "    <taxii_11:Poll_Parameters allow_asynch=\"false\">\r\n"
				+ "        <taxii_11:Response_Type>FULL</taxii_11:Response_Type>\r\n"
				+ "    </taxii_11:Poll_Parameters>\r\n"
				+ "</taxii_11:Poll_Request>";
		
		//SETTING UP URL CONNECTION FOR REQUEST-1
		URL url1 = new URL("http://hailataxii.com/taxii-data");
		HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();	
		
		//CONNECTION TIMEOUT OF 20
		connection1.setConnectTimeout(20000);
		connection1.setReadTimeout(20000);
		connection1.setDoOutput(true);
		connection1.setUseCaches(true);
		
		//CONTENT HEADERS
		connection1.setRequestMethod("POST");
		connection1.setRequestProperty("Accept", "application/xml");
		connection1.setRequestProperty("Content-Type", "application/xml");
		connection1.setRequestProperty("X-TAXII-Content-Type", "urn:taxii.mitre.org:message:xml:1.1");
		
		//SENDING REQUEST
		OutputStream outputStream1 = connection1.getOutputStream();
		byte[] b1 = request1.getBytes("UTF-8");
		outputStream1.write(b1);
		outputStream1.flush();
		outputStream1.close();	
		i=0;
		
		//GETTING RESPONSE
		InputStream inputStream1 = connection1.getInputStream();
		byte[] res2 = new byte[2048];
		StringBuilder response1 = new StringBuilder();
		while ((i = inputStream1.read(res2)) != -1) {
			//THE SERVER RESPONSE(STIX XML) FOR REQUEST-2 IS STORED IN "response1"
			response1.append(new String(res2, 0, i));
		}
		inputStream1.close();
		
		//MAPDB DATABASE FOR STORING THREAT DATA
		DB db = DBMaker.memoryDB().make();
		
		//3 HTREEMAPS IN THE DB FOR URLs,DOMAINs and IPs
		HTreeMap urlMap = db.hashMap("myMap").createOrOpen();
		HTreeMap domainMap = db.hashMap("myMap1").createOrOpen();
		HTreeMap ipMap = db.hashMap("myMap2").createOrOpen();	
		JSONObject json=null;
		JSONObject json1=null;
		
		StringBuilder xmlStringBuilder = new StringBuilder();
		xmlStringBuilder.append("<?xml version=\"1.0\"?>");
		xmlStringBuilder.append(response.toString());
		ByteArrayInputStream input1 = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
		
		StringBuilder xmlStringBuilder1 = new StringBuilder();
		xmlStringBuilder1.append("<?xml version=\"1.0\"?>");
		xmlStringBuilder1.append(response1.toString());
		ByteArrayInputStream input2 = new ByteArrayInputStream(xmlStringBuilder1.toString().getBytes("UTF-8"));

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc1 = builder.parse(input1);
				
				Element root = doc1.getDocumentElement();
				NodeList nl = root.getElementsByTagName("taxii_11:Content_Block");
				int xmlurlindex=0;
					for(int x =0;x<nl.getLength();x++)
					{
					NodeList nl2=nl.item(x).getChildNodes();
					for(i =0;i< nl2.getLength();i++)
					{	
					if(nl2.item(i).getNodeName()=="taxii_11:Content")
					{
					Node nl3 = nl2.item(i).getFirstChild();
					NodeList nl4=nl3.getChildNodes();
					for(int j=0;j<nl4.getLength();j++)
					{
					if(nl4.item(j).getNodeName()=="stix:Observables")
					{
					NodeList nl5=nl4.item(j).getChildNodes();
					for(int k=0;k<nl5.getLength();k++)
					{
					if(nl5.item(k).getNodeName()=="cybox:Observable")
					{
					NodeList nl6 = nl5.item(k).getChildNodes();
					for(int l =0;l<nl6.getLength();l++)
					{
					if(nl6.item(l).getNodeName()=="cybox:Object")
					{
					NodeList nl7 = nl6.item(l).getChildNodes();
					for(int m=0;m<nl7.getLength();m++)
					{
					if(nl7.item(m).getNodeName()=="cybox:Properties")
					{
					NodeList nl8 = nl7.item(m).getChildNodes();
					for(int n=0;n<nl8.getLength();n++)
					{
					if(nl8.item(n).getNodeName()=="URIObj:Value")
					{
					urlMap.put(String.valueOf(xmlurlindex), nl8.item(n).getTextContent());
					xmlurlindex++;
					}}}}}}}}}}}}}
			Document doc2 = builder.parse(input2);
			Element root2 = doc2.getDocumentElement();
			NodeList nnl = root2.getElementsByTagName("taxii_11:Content_Block");
			int xmlipindex=0;
				for(int x =0;x<nnl.getLength();x++)
				{
				NodeList nl2=nnl.item(x).getChildNodes();
				for(i =0;i< nl2.getLength();i++)
				{	
				if(nl2.item(i).getNodeName()=="taxii_11:Content")
				{
				Node nl3 = nl2.item(i).getFirstChild();
				NodeList nl4=nl3.getChildNodes();
				for(int j=0;j<nl4.getLength();j++)
				{
				if(nl4.item(j).getNodeName()=="stix:Observables")
				{
				NodeList nl5=nl4.item(j).getChildNodes();
				for(int k=0;k<nl5.getLength();k++)
				{
				if(nl5.item(k).getNodeName()=="cybox:Observable")
				{
				NodeList nl6 = nl5.item(k).getChildNodes();
				for(int l =0;l<nl6.getLength();l++)
				{
				if(nl6.item(l).getNodeName()=="cybox:Object")
				{
				NodeList nl7 = nl6.item(l).getChildNodes();
				for(int m=0;m<nl7.getLength();m++)
				{
				if(nl7.item(m).getNodeName()=="cybox:Properties")
				{
				NodeList nl8 = nl7.item(m).getChildNodes();
				for(int n=0;n<nl8.getLength();n++)
				{
				if(nl8.item(n).getNodeName()=="AddressObj:Address_Value")
				{
				ipMap.put(String.valueOf(xmlipindex), nl8.item(n).getTextContent());
				xmlipindex++;
				}}}}}}}}}}}}}
					
			} 
			catch (ParserConfigurationException e1) 
			{			
				e1.printStackTrace();
			} catch (SAXException e2) 
			{
				e2.printStackTrace();
			}

		
		
		//DOMAIN COUNT IS NOT DIRECTLY OBTAINED FROM THE STIX FEED
		//INSTED IT IS PARSED FROM A URL OF A MALICIOUS SITE
		
		
		int domaincount=0;  //KEY
		for(int index =0;index<urlMap.size();index++)   //ITERATE THROUGH URL MAP
		{
				//SPLITS THE URL INTO 2 SEPERATE PARTS "https:" and "url"	
				String[] domainarr=  ((String) urlMap.get(String.valueOf(index))).split("//");
				//SPLITS THE URL WITH "/" AS A DELIMITER
				String[] domain = domainarr[1].split("/");
				//"domain[0]" CONTAINS THE DOMAIN NAME
				//IT IS STORED IN A MAP IN THE DB AS KEY:VALUE PAIRS (i.e) INDEX:DOMAINNAME
				domainMap.put(String.valueOf(domaincount), domain[0]);
				domaincount++; //KEY UPDATION
		}
		urlv = new JSONObject(urlMap);
		domainv = new JSONObject(domainMap);
		ipv = new JSONObject(ipMap);
		urlsize=urlMap.getSize();
		ipsize=ipMap.getSize();

	//CALL "MainMeu.jsp"
		RequestDispatcher rd = req.getRequestDispatcher("/MainMenu.html");
		rd.forward(req,res);

	}		
}

