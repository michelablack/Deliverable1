package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {
	
   private JSONUtil() {
   	  super();
   }
   
   private static String readAll(Reader rd) throws IOException {
      StringBuilder sb = new StringBuilder();
      int cp;
      while ((cp = rd.read()) != -1) {
         sb.append((char) cp);
      }
      return sb.toString();
   }

  public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
     InputStream is = new URL(url).openStream();
     try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
        String jsonText = readAll(rd);
        return new JSONArray(jsonText);
     }
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
     InputStream is = new URL(url).openStream();
     try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
        String jsonText = readAll(rd);
        return new JSONObject(jsonText);
     }
  }
}
