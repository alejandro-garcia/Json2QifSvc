package com.json2qifsvc.restsvc;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
@Path("/json2qif")
public class Json2Qif {
	
	@POST
	@Path("/createqif")
	@Consumes(MediaType.APPLICATION_JSON)
	public void createQif(@FormParam("jsonData") String jsonData) throws JSONException, IOException{
		JSONObject jObj = new JSONObject(jsonData);
		
		String fileName = "c:\\pruebas\\javasvc\\cuentas-" + jObj.getString("Tipo") + "-" + String.valueOf(new Date()); 
		
		Writer qFile = openFile(fileName);
		
		// write account type header
		String accountType = jObj.getString("Tipo");
		if (accountType == "Efectivo"){
			qFile.write("!Type:Cash");
		}
		else if (accountType == "Banco"){
			qFile.write("!Type:Bank");
		}
		
		JSONArray jArray = jObj.getJSONArray("Trans");
		
		if (jArray.length() == 0)
			return;
		
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject item = jArray.getJSONObject(i);
			
			// transaction date
			String[] sDate = item.getString("Date").split("T")[0].split("-");
			String qDate = "D" + sDate[2] + "." + sDate[1] + "." + sDate[0];
			qFile.write(qDate);
			
			//  amount
			qFile.write("T" + item.getString("Amount"));
			
			// payee
			qFile.write("P" + item.getString("Payee")); 
			
			// Category/Tag
			String sCategory = "L" + item.getString("Category");  //+ "/" + item.getString("Tag");
			qFile.write(sCategory);
			
			// end of record mark
			qFile.write("^");
			
			
		}
		
		qFile.close();	
		
	}
	
	private Writer openFile(String fileName){
		
		try {
			return new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(fileName), "utf-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
			return null;
		}
	}
	
}
