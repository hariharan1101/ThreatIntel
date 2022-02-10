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

@WebServlet("/poller")
public class Poller extends HttpServlet{	
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
	{	
		
		String request= "<taxii_11:Poll_Request\r\n"
				+ "    xmlns:taxii_11=\"http://taxii.mitre.org/messages/taxii_xml_binding-1.1\"\r\n"
				+ "    message_id=\"1\"\r\n"
				+ "    collection_name=\"guest.phishtank_com\">\r\n"
				+ "    <taxii_11:Exclusive_Begin_Timestamp>2018-05-25T00:00:00Z</taxii_11:Exclusive_Begin_Timestamp>\r\n"
				+ "    <taxii_11:Inclusive_End_Timestamp>2018-05-25T03:00:00Z</taxii_11:Inclusive_End_Timestamp>\r\n"
				+ "    <taxii_11:Poll_Parameters allow_asynch=\"false\">\r\n"
				+ "        <taxii_11:Response_Type>FULL</taxii_11:Response_Type>\r\n"
				+ "    </taxii_11:Poll_Parameters>\r\n"
				+ "</taxii_11:Poll_Request>";
		
		URL url = new URL("http://hailataxii.com/taxii-data");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();		
		connection.setConnectTimeout(20000);
		connection.setReadTimeout(20000);
		connection.setDoOutput(true);
		connection.setUseCaches(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("X-TAXII-Content-Type", "urn:taxii.mitre.org:message:xml:1.1");
		OutputStream outputStream = connection.getOutputStream();
		byte[] b = request.getBytes("UTF-8");
		outputStream.write(b);
		outputStream.flush();
		outputStream.close();		
		InputStream inputStream = connection.getInputStream();
		byte[] res1 = new byte[2048];
		int i = 0;
		StringBuilder response = new StringBuilder();
		while ((i = inputStream.read(res1)) != -1) {
			response.append(new String(res1, 0, i));
		}
		inputStream.close();
		
		
		
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
		
		URL url1 = new URL("http://hailataxii.com/taxii-data");
		HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();		
		connection1.setConnectTimeout(20000);
		connection1.setReadTimeout(20000);
		connection1.setDoOutput(true);
		connection1.setUseCaches(true);
		connection1.setRequestMethod("POST");
		connection1.setRequestProperty("Accept", "application/xml");
		connection1.setRequestProperty("Content-Type", "application/xml");
		connection1.setRequestProperty("X-TAXII-Content-Type", "urn:taxii.mitre.org:message:xml:1.1");
		OutputStream outputStream1 = connection1.getOutputStream();
		byte[] b1 = request1.getBytes("UTF-8");
		outputStream1.write(b1);
		outputStream1.flush();
		outputStream1.close();	
		i=0;
		InputStream inputStream1 = connection1.getInputStream();
		byte[] res2 = new byte[2048];
		StringBuilder response1 = new StringBuilder();
		while ((i = inputStream1.read(res2)) != -1) {
			response1.append(new String(res2, 0, i));
		}
		inputStream1.close();
						
		DB db = DBMaker.memoryDB().make();
		HTreeMap urlMap = db.hashMap("myMap").createOrOpen();
		HTreeMap domainMap = db.hashMap("myMap1").createOrOpen();
		HTreeMap ipMap = db.hashMap("myMap2").createOrOpen();		
		JSONObject json=null;
		JSONObject json1=null;
			
		try 
		{	
			int urlcount=0;	
			json = XML.toJSONObject(response.toString());
			JSONArray jarr = json.getJSONObject("taxii_11:Poll_Response").getJSONArray("taxii_11:Content_Block");			
			for( int index =0;index<jarr.length();index++)
			{
				JSONObject stixpackagejsonobj = jarr.getJSONObject(index).getJSONObject("taxii_11:Content").getJSONObject("stix:STIX_Package");				
				if(stixpackagejsonobj.has("stix:Observables"))
				{									
					JSONObject urivalueobj = stixpackagejsonobj.getJSONObject("stix:Observables").getJSONObject("cybox:Observable").
							getJSONObject("cybox:Object").getJSONObject("cybox:Properties").getJSONObject("URIObj:Value");
					
					urlMap.put(String.valueOf(urlcount), urivalueobj.getString("content"));
					urlcount++;
				}				
			}
			
			
			int ipcount =0;	
			json1 = XML.toJSONObject(response1.toString());
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
									JSONObject AddressObj = cyboxpropertiesobj.getJSONObject("AddressObj:Address_Value");
									ipMap.put(String.valueOf(ipcount), AddressObj.getString("content"));
									ipcount++;
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
		
		int domaincount=0;
		for(int index =0;index<urlMap.size();index++)
		{
		if(urlMap.containsKey(String.valueOf(index)))
			{					
				String[] domainarr=  ((String) urlMap.get(String.valueOf(index))).split("//");
				String[] domain = domainarr[1].split("/");
				domainMap.put(String.valueOf(domaincount), domain[0]);
				domaincount++;
			}
		}
		
		session.setAttribute("db", db);		
		RequestDispatcher rd = req.getRequestDispatcher("/MainMenu.jsp");
		rd.forward(req,res);
		
	}		
}

