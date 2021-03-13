package logic;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import bean.Date;

import org.json.JSONArray;

public class RetrieveTicketsID {

   static String folder = "C:\\Users\\miche\\Downloads\\fold";
   static String projName ="STDCXX";
   static String git = "git -C ";
   static String exception = "process is null";
   
   /**
    * Function to start a new process, which executes the code given in input.
    */
   public static Process process (String code){  
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(code);
			exception(p);
		} catch (IOException e) {
			Logger logger = Logger.getAnonymousLogger();
			logger.log(Level.SEVERE, "an exception was thrown",e);
			Thread.currentThread().interrupt();
		}
		return p;
   }
   
   /**
    * Function to find the dates' bounds for the commits done for all the project's tickets,
    * from the first to the last one.
    * After that, it creates a list of objects Date with attributes month and year of the commit.
    * 
    */
   public static List<Date> getBounds(Integer i, Integer j, JSONArray issues, Integer total) throws IOException{
	   
	   String s =null;
	   String actualMonth;
	   List<Date> dateList = new ArrayList<>();
	   List<String> list = new ArrayList<>();
	   
	   for (; i < total && i < j; i++) {
           //Iterate through each ticket, considering the committer date 
           String key = issues.getJSONObject(i%1000).get("key").toString();
           Process p = process(git +folder+"\\"+projName+" log\r\n" + 
           		" --pretty=format:'%cd' --grep="+key);
           exception(p);
           BufferedReader stdInput = new BufferedReader(new 
	                 InputStreamReader(p.getInputStream()));
       	   
           while ((s = stdInput.readLine()) != null) {
        	   list.add(s);
           }
        }       
	   
	   String lowerMonth = list.get(0).substring(5, 8);
	   Integer lowerYear = Integer.parseInt(list.get(list.size()-1).substring(21, 25));
	   String upperMonth = list.get(0).substring(5, 8);
	   Integer upperYear = Integer.parseInt(list.get(0).substring(21, 25));
	   
       Date lowerDate = new Date(lowerMonth, list.get(list.size()-1).substring(21, 25));
       Date upperDate = new Date(upperMonth,list.get(0).substring(21, 25));
       
       actualMonth = lowerMonth;
       
       dateList.add(lowerDate);
       int d = 0;
       
       /*
        * For each year, it's considered a month starting from the first to the last one found.
        * The while loop continue until the last date is reached and it breaks when the month is December, 
        * so to start from the next year.
        */
       for (Integer y = lowerYear; y < (upperYear+1); y++) {
    	   while ( !((dateList.get(d).getMonth().equals(upperMonth)) && (Integer.parseInt(dateList.get(d).getYear()) == upperYear))) {
    		   Date date = new Date();
    		   String nextMonth = date.nextMonth(actualMonth);
    		   date.setMonth(nextMonth);
    		   date.setYear(y.toString());
    		   dateList.add(date);
    		   d++;
    		   actualMonth = nextMonth;
    		   if (actualMonth.equals("Dec")) break;
    	   }
       }
       
       dateList.add(upperDate);
       

	return dateList;
	   
   }

   
   public static void main(String[] args) throws IOException, JSONException {
	   
	   File file = new File("file.csv");
	   String s =null;
	   List<Date> dateList;
	   int[] count;
	   StringBuilder sb = new StringBuilder();
	   String projUrl = "https://github.com/apache/stdcxx";
	   Integer j = 0;
	   Integer i = 0;
	   Integer totalFixed = 1;
	   Integer totalAll = 1;
	   
	   try (PrintWriter writer = new PrintWriter(file)){	   
		   sb.append("Date");
		   sb.append(';');
		   sb.append("NumFixed");
		   sb.append('\n');
	      do {
	         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
	         j = i + 1000;
	         
	         String urlAll = QueryGenerator.getAllTickets(projName, i, j);
	         JSONObject jsonAll = JSONUtil.readJsonFromUrl(urlAll);
	         JSONArray issuesAll = jsonAll.getJSONArray("issues");
	         totalAll = jsonAll.getInt("total");
	         
	         String urlFixed = QueryGenerator.getFixedTickets(projName, i, j);
	         JSONObject jsonFixed = JSONUtil.readJsonFromUrl(urlFixed);
	         JSONArray issuesFixed = jsonFixed.getJSONArray("issues");
	         totalFixed = jsonFixed.getInt("total");
	         
	         //Command to clone the project repository into the chosen directory. 
	         Process q = process(git +folder+ " clone " +projUrl);
	         exception(q);
	        
	         try {
				q.waitFor();
			} catch (InterruptedException e) {
				Logger logger = Logger.getAnonymousLogger();
				logger.log(Level.SEVERE, "an exception was thrown",e);
				Thread.currentThread().interrupt();
			}
	         dateList = getBounds(i, j, issuesAll, totalAll);
	        
	         count = new int[dateList.size()];
	         for (; i < totalFixed && i < j; i++) {
	            //Iterate through each fixed ticket, considering the committer date
	            String key = issuesFixed.getJSONObject(i%1000).get("key").toString();
	            Process p = process(git +folder+"\\"+projName+" log\r\n" + 
	            		" --pretty=format:'%cd' --grep="+key);
	            exception(p);
	            
	            BufferedReader stdInput = new BufferedReader(new 
		                 InputStreamReader(p.getInputStream()));
	        	List<String> list = new ArrayList<>();
	            while ((s = stdInput.readLine()) != null) {
	            	list.add(s);
	            }
	            
	            /*
	             * If the list is empty it means that there isn't a commit for this fixed ticket.
	             * Otherwise, a commit can have more fixing dates. 
	             * In this case, it is chosen the first available date, that is the older one.
	             * This is the date in which the ticket has been completely fixed.
	             */
	            if (!list.isEmpty()){
	                String month = list.get(0).substring(5, 8);
	                String year = list.get(0).substring(20, 25).replace(" ","");
	                Date date = new Date();
	                date.setMonth(month);
	                date.setYear(year);
	            	int ind = dateList.indexOf(date);
	            	count[ind] += 1;
	            }
	         }       
	      } while (i < totalFixed);
	      
	      
	      for (int k=0; k< dateList.size(); k++) {
	     	 
	     	 sb.append(dateList.get(k).getYear());
	     	 sb.append(" ");
	     	 sb.append(dateList.get(k).getMonth());
	     	 sb.append(';');
			 sb.append(count[k]);
			 sb.append('\n');
	      }
	      
	      writer.write(sb.toString());
	   }
   }
   
   
   private static void exception(Process p) {
	
	if (p == null) {
		throw new IllegalStateException(exception);
	}
	
}


}