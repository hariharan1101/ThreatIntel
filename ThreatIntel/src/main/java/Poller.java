import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

@WebServlet("/Poller")
public class Poller extends HttpServlet{	
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
			
		try 
		{	
			int urlcount=0;	//KEY FOR THE MAP
			
			json = XML.toJSONObject(response.toString()); //PARSING XML TO JSON
			
			//NAVIGATING THROUGH NESTED JSON OBJECTS TO GET THE NECESSARY INFORMATION
			JSONArray jarr = json.getJSONObject("taxii_11:Poll_Response").getJSONArray("taxii_11:Content_Block");			
			for( int index =0;index<jarr.length();index++)
			{
				JSONObject stixpackagejsonobj = jarr.getJSONObject(index).getJSONObject("taxii_11:Content").getJSONObject("stix:STIX_Package");				
				if(stixpackagejsonobj.has("stix:Observables"))
				{									
					JSONObject urivalueobj = stixpackagejsonobj.getJSONObject("stix:Observables").getJSONObject("cybox:Observable").
							getJSONObject("cybox:Object").getJSONObject("cybox:Properties").getJSONObject("URIObj:Value");
					
					//"URIObj:Value" HAS THE VALUE OF THE URL OF THE MALICIOUS WEBSITE
					//IT IS STORED IN A MAP IN THE DB AS KEY:VALUE PAIRS (i.e) INDEX:URL 
					urlMap.put(String.valueOf(urlcount), urivalueobj.getString("content"));
					urlcount++; //KEY UPDATION
				}				
			}
			
			
			int ipcount =0;	//KEY FOR THE MAP
			
			json1 = XML.toJSONObject(response1.toString());  //PARSING XML TO JSON
			
			//NAVIGATING THROUGH NESTED JSON OBJECTS TO GET THE NECESSARY INFORMATION
			JSONArray jarr1 = json1.getJSONObject("taxii_11:Poll_Response").getJSONArray("taxii_11:Content_Block");			
					
			for( int index1 =0;index1<jarr1.length();index1++)
			{
				JSONObject stixpackageobj = jarr1.getJSONObject(index1).getJSONObject("taxii_11:Content").
						getJSONObject("stix:STIX_Package");	
																	
				if(stixpackageobj.has("stix:Observables"))
				{
					JSONObject stixobservablesobj = stixpackageobj.getJSONObject("stix:Observables");					
					if(stixobservablesobj.has("cybox:Observable")) 
					{
						JSONObject cyboxobservableobj = stixobservablesobj.getJSONObject("cybox:Observable");
						JSONObject cyboxpropertiesobj=null;
						if(cyboxobservableobj.has("cybox:Object")) 
						{
							JSONObject cyboxobjectobj = cyboxobservableobj.getJSONObject("cybox:Object");						
							if(cyboxobjectobj.has("cybox:Properties"))
							{
								cyboxpropertiesobj = cyboxobjectobj.getJSONObject("cybox:Properties");														
								if(cyboxpropertiesobj.has("AddressObj:Address_Value"))
								{	
									//"AddressObj:Address_Value" HAS THE VALUE OF THE IP OF THE MALICIOUS WEBSITE
									JSONObject AddressObj = cyboxpropertiesobj.getJSONObject("AddressObj:Address_Value");
									//IT IS STORED IN A MAP IN THE DB AS KEY:VALUE PAIRS (i.e) INDEX:IP
									ipMap.put(String.valueOf(ipcount), AddressObj.getString("content"));
									ipcount++; //KEY UPDATION
								}
							}
						}
					}						
				}
			}
		}
		catch(JSONException e)
		{
			System.out.println(e);
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
		
		
		JSONObject urlv = new JSONObject();
		urlv.put("urls", urlMap);
		JSONObject domainv = new JSONObject(domainMap);
		domainv.put("domains", domainMap);
		JSONObject ipv = new JSONObject(ipMap);
		ipv.put("ips", ipMap);
		
		String urlxml = XML.toString(urlv);
		String domainxml = XML.toString(domainv);
		String ipxml = XML.toString(ipv);
				
		session.setAttribute("db", db);	
		session.setAttribute("urlxml", urlxml);
		session.setAttribute("domainxml", domainxml);
		session.setAttribute("ipxml", ipxml);
		
		
	//CALL "MainMeu.jsp"
		RequestDispatcher rd = req.getRequestDispatcher("/MainMenu.jsp");
		rd.forward(req,res);

	}		
}

