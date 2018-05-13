import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

public class osabru{

	//this solves bloodcats captcha and returns cookie("obm_human") in string
	
	public static String bloodcatcaptchacheck(String cinput, String sync, String hash) {
		  HttpURLConnection connection = null;
		int id = 8023;
		  String cookie ="";
		  try {
		    //Create connection
		    URL url = new URL("http://bloodcat.com/osu/s/"+id);
		    connection = (HttpURLConnection) url.openConnection();
		    
		    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36");
		    connection.setDoOutput(true); 
		    connection.setDoInput(true); 
		    	connection.setRequestMethod("POST");
		    	//connection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
		    	String urlParameters ="response="+cinput+"&sync="+sync+"&hash="+hash;
		    	connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));				    
			    DataOutputStream wr = new DataOutputStream (
			        connection.getOutputStream());
			    wr.writeBytes(urlParameters);
			    wr.close();
			    int response = connection.getResponseCode();
			    if(response==200) {
			    	cookie=connection.getHeaderField("Set-Cookie");
			    	return cookie;
			    }else {
			    	return "";
			    }
		  }catch(IOException e) {
			  
		  }
		  return "";
	}
	public static String[] bloodcatcaptcha() {
		int id = 8023;
		  String hash="",sync="",b64s="";
		  String cookie = "";
		  String cinput = "";
		  //String b64="";

		  //Boolean requested = false;

			  HttpURLConnection connection = null;
			  try {
			    //Create connection
			    URL url = new URL("http://bloodcat.com/osu/s/"+id);
			    connection = (HttpURLConnection) url.openConnection();
			    
			    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36");
			    connection.setDoOutput(true); 
			    connection.setDoInput(true); 
			    
			    connection.setRequestMethod("GET");
			
			    //Send request
			    DataOutputStream wr = new DataOutputStream (
			        connection.getOutputStream());
			    wr.close();
			    
			    //Read cookie from response header
			    cookie = connection.getHeaderField("Set-cookie");

			    	//read body and get line for captcha image
			        InputStream is = connection.getErrorStream();
			        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			        String line;
			        int lineno=1;
			        while ((line = rd.readLine()) != null) {
			        	if(lineno==23) b64s = line.split("\"")[1];
			        	else if(lineno==33) sync = line.split("\"")[5];
			        	else if(lineno==34) hash = line.split("\"")[5];
			        	lineno++;
			        }
			        rd.close();
			        //string print for debugging
//			        System.out.println("b64s : " + b64s);
//			        System.out.println("sync : " + sync);
//			        System.out.println("hash : " + hash);
			        
			        //manipulate string to obtain base 64 string
			        String base64string = b64s;
			        //base 64 string >> image file
			        
			        String base64Image = base64string.split(",")[1];
			        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
			        File output = new File("captcha.png");
			        FileOutputStream osf = new FileOutputStream(output);
			        osf.write(imageBytes);
			        osf.flush();
			        osf.close();
			        //byte[] imageBytes = Base64.getDecoder().decode(base64Image);
			        //asks for user input
			        
			       
			  } catch (Exception e) {
			    e.printStackTrace();
			  } finally {
			    if (connection != null) {
			      connection.disconnect();
			    }
			  }
			  String[] result = {sync,hash};
			  return result;
		
	}
	
	
	public static String login(String user, String password) {
		String cookie = "";
		String targetURL = "https://osu.ppy.sh/session";
		String urlParameters = "username="+user+"&password="+password;
		HttpsURLConnection connection = null;
		  try {
		    //Create connection
		    URL url = new URL(targetURL);
		    connection = (HttpsURLConnection) url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		    connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
		    connection.setRequestProperty("User-Agent", "");  
		    connection.setDoOutput(true); 
		    connection.setDoInput(true); 
		    
		    
		    
		    
		    //Send request
		    DataOutputStream wr = new DataOutputStream (
		        connection.getOutputStream());
		    wr.writeBytes(urlParameters);
		    wr.close();

		    //Get Response
		    
		    int code = connection.getResponseCode();
		    if(code!=200) return "";
		    
		    cookie = connection.getHeaderField("Set-Cookie");
		    return cookie;

		  } catch (Exception e) {
		    e.printStackTrace();
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		  }
		  return cookie;
	}
	
	
	public Boolean logintest(String user, String password) {
		String targetURL = "https://osu.ppy.sh/session";
		String urlParameters = "username="+user+"&password="+password;
		  HttpsURLConnection connection = null;

		  try {
		    //Create connection
		    URL url = new URL(targetURL);
		    connection = (HttpsURLConnection) url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		    connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
		    connection.setRequestProperty("User-Agent", "");  
		    connection.setDoOutput(true); 
		    connection.setDoInput(true); 

		    //Send request
		    DataOutputStream wr = new DataOutputStream (
		        connection.getOutputStream());
		    wr.writeBytes(urlParameters);
		    wr.close();

		    //Get Response  
		    //String cookie = connection.getHeaderField("Set-Cookie");
		    //System.out.println(cookie);
		    int code = connection.getResponseCode();
		    if(code==200) return true;
		    else return false;
		  } catch (Exception e) {
		    e.printStackTrace();
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		  }
		  return false;
		  
		  
		}
	
	public String getinput() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input="";
        try {
			input = br.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        return input;
	}
	
	static String getOsuDir() {
		String path = System.getenv("LOCALAPPDATA")+"\\osu!\\Songs";
		File dir = new File(path);
		if(dir.exists()) {
			return path;
		}
		return "";
		//return "D:\\Program Files (x86)\\osu!\\Songs";
		
	}
	
	public List<Integer> getSongs(String directoryPath) {
	    File directory = new File( directoryPath);
		
	    FileFilter directoryFileFilter = new FileFilter() {
	        public boolean accept(File file) {
	            return file.isDirectory();
	        }
	    };
			
	    File[] directoryListAsFile = directory.listFiles(directoryFileFilter);
	    List<Integer> foldersInDirectory = new ArrayList<Integer>(directoryListAsFile.length);
	    for (File directoryAsFile : directoryListAsFile) {
	        String name = directoryAsFile.getName();
	        String[] names = name.split(" ");
	        int songid = 0;
	        try {
	        	 songid = Integer.parseInt(names[0]);
	        }catch(NumberFormatException ex) {
	        	continue;
	        }
	    	foldersInDirectory.add(songid);
	    }
	    return foldersInDirectory;
	}
}
