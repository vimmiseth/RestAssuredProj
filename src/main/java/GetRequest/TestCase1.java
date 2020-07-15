package GetRequest;

import static io.restassured.RestAssured.get;
import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class TestCase1 
{
	
@Test
public static void fCmpFromFile1()
{
	   try 
       {
    	 System.out.println( "Reading JSON files of Actual & Expected Result and parse it" );
         // FileReader fileReader = new FileReader("C:\\vimmi\\new-study\\Microservices\\weather-in.txt");
        
    	 String strExpectedFile = "file";
    	 String strUrl = "url" ;
    	
    	 
	   	 try (InputStream strExpected = new FileInputStream("C:\\vimmi\\eclipse-workspace\\RestAssuredProj\\src\\main\\resources\\config.properties")) //	   	 try (InputStream strExpected = new FileInputStream("C:\\vimmi\\eclipse-workspace\\RestAssuredProj\\src\\main\\resources\\config.properties")) 
	 	 {
	            Properties prop = new Properties();	
	            prop.load(strExpected);	  // load a properties file

	            // get the property value and print it out
	            strExpectedFile = prop.getProperty("Expectedfile");
		        strUrl = prop.getProperty("url");
	     } 
	   	 catch (IOException ex) 
	   	 {
	   		 	ex.printStackTrace();
	   	 }
	   	 
/*
    	 System.out.println( "url should be in ..\\data\\url.txt & output in ..\\data\\datafile.txt" );
    	 
	   		try (BufferedReader br = new BufferedReader(new FileReader("..\\data\\url.txt"))) 
	   		{ 
	   		    strUrl = br.readLine(); 
	   			strExpectedFile = "..\\data\\datafile.txt";
	   		} 
	   		catch (IOException e) 
	   		{ 
	   			e.printStackTrace(); 
	   		}

	    */	 
    	 System.out.println("Expected file from config : " + strExpectedFile );
	   	 System.out.println("Url from config file : " + strUrl);

	   	 //Run the service to get the actual result and parse it 
	     RestAssured.defaultParser = Parser.JSON;
	 	
//	 	String strUrl1 = "https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22";
	 	 Response oResponse = get(strUrl);

	 	 int code = oResponse.getStatusCode();
	 	 Assert.assertEquals(code, 200);	
	 			
	 	 String strResponse = oResponse.asString();
	 	 JsonPath oActualJason = new JsonPath(strResponse);
	 			
	   	 System.out.println("Url Fetched : " + strResponse);
	 	 
	   	 //parse the file of Expected Result
	   	 FileReader fReaderExpected = new FileReader(strExpectedFile);    
         JsonParser parsedExpected = new JsonParser();
        
         com.google.gson.JsonObject oJasonExpected = (JsonObject) parsedExpected.parse( fReaderExpected );
         Set <Map.Entry<String, JsonElement>> oSetExpected = oJasonExpected.entrySet();
         if ( oSetExpected.isEmpty() ) 
         {
            System.out.println( "Empty JSON Object" );
         }
         else 
         {
        	int keysize = oSetExpected.size();
        	System.out.println("Total keys at level 1 : " +  keysize);
        	
            Map<String, Object> oMapExpected = fActualVsExpectedNew( oActualJason, oSetExpected, parsedExpected );
            System.out.println("Json 2 Map : "+ oMapExpected);
         }
      } 
      catch (IOException ex) 
      {
        System.out.println("Input File Does not Exists.");
      } 
}
	
	
public static Map<String, Object> fActualVsExpectedNew( JsonPath ActualJason, Set <Map.Entry<String, JsonElement>> oSetExpected , JsonParser parser)
{
	
    Map<String, Object> jsonMap = new HashMap<String, Object>();
    
    for (Entry<String, JsonElement> entryExpected : oSetExpected) 
    {
        String keyEntryExpected = entryExpected.getKey();
        JsonElement valuesEntryExpected =  entryExpected.getValue();
        
        if (valuesEntryExpected.isJsonNull()) 
        {
            System.out.println("Value Null for : " + keyEntryExpected);
            jsonMap.put(keyEntryExpected, valuesEntryExpected);
        }
        else if (valuesEntryExpected.isJsonPrimitive()) 
        {
            jsonMap.put(keyEntryExpected, valuesEntryExpected);
  
            String ValueFromFile = valuesEntryExpected.getAsString();
            String Actualvalue = ActualJason.get(keyEntryExpected).toString();
            
            System.out.println("Primitive : " + keyEntryExpected + " : " + valuesEntryExpected.getAsString());
            System.out.println("ValueFromFile vs Actualvalue : " +  ValueFromFile + " : " + Actualvalue);
        	assertEquals(ValueFromFile, Actualvalue);
        }
        else if (valuesEntryExpected.isJsonArray()) 
        {
            System.out.println("Expected file has isJsonArray : " );
            
        	JsonArray oExpectedArray = valuesEntryExpected.getAsJsonArray();
            List<Object> ExpectedListofArray = new ArrayList<Object>();
            
            
            for (JsonElement jsonElements : oExpectedArray) 
            {
                System.out.println("Expected Array - "+jsonElements);
                
                ExpectedListofArray.add(jsonElements);   
                jsonMap.put(keyEntryExpected, ExpectedListofArray);
            }
           
            
            ArrayList<Object> oActualArray = ActualJason.get(keyEntryExpected);
            for(int i = 0; i < oActualArray.size(); i++)
            {
                System.out.println("oActualArray " + i + " : " + oActualArray.get(i));
            }
            
            
        }
        else if (valuesEntryExpected.isJsonObject()) 
        {      
            System.out.println("Expected file has isJsonObject : " );

             com.google.gson.JsonObject obj = (JsonObject) parser.parse(valuesEntryExpected.toString());                    
             Set <java.util.Map.Entry<String, com.google.gson.JsonElement>> obj_key = obj.entrySet();
             jsonMap.put(keyEntryExpected, json_UnKnown_Format( obj_key, parser));
        }
    }
    return jsonMap;
}


public static Map<String, Object> json_UnKnown_Format( Set <java.util.Map.Entry<String, com.google.gson.JsonElement>> keys , JsonParser parser)
{
    Map<String, Object> jsonMap = new HashMap<String, Object>();
    
    for (Entry<String, JsonElement> entry : keys) 
    {
        String keyEntry = entry.getKey();
        //System.out.println(keyEntry + " : ");
        JsonElement valuesEntry =  entry.getValue();
        
        
        if (valuesEntry.isJsonNull()) 
        {
            //System.out.println(valuesEntry);
            jsonMap.put(keyEntry, valuesEntry);
        }
        else if (valuesEntry.isJsonPrimitive()) 
        {
           // System.out.println("P - "+valuesEntry);
            jsonMap.put(keyEntry, valuesEntry);
        }
        else if (valuesEntry.isJsonArray()) 
        {
            JsonArray array = valuesEntry.getAsJsonArray();
            List<Object> array2List = new ArrayList<Object>();
            for (JsonElement jsonElements : array) 
            {
                //System.out.println("A - "+jsonElements);
                array2List.add(jsonElements);
            }
            jsonMap.put(keyEntry, array2List);
        }
        else if (valuesEntry.isJsonObject()) 
        {             
             com.google.gson.JsonObject obj = (JsonObject) parser.parse(valuesEntry.toString());                    
             Set <java.util.Map.Entry<String, com.google.gson.JsonElement>> obj_key = obj.entrySet();
             jsonMap.put(keyEntry, json_UnKnown_Format(obj_key, parser));
        }
    }
    return jsonMap;
}
	
}
