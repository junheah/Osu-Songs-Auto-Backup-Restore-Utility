import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class downloader implements Runnable{
	String captcha="";
	String login="";
	String[] idlist= {};
	String location ="";
	int mode=0;
	public downloader(String c,String l, String loc, int m, String[] tmp){
		captcha = c;
		login = l;
		location = loc;
		mode =m;
		idlist = tmp;
	}
	
	public void run() {
		
		int listmax = idlist.length;
		
		
		if(mode ==0) {
			for(int i=0;i<listmax;i++) {
				Main.changecounter("["+(i+1)+"/"+ listmax+"]");
				downloadbeatmapset(Integer.parseInt(idlist[i]),login,location);
			}
		}else if(mode ==1) {
			for(int i=0;i<listmax;i++) {
				Main.changecounter("["+(i+1)+"/"+ listmax+"]");
				if(downloadbloodcat(captcha,Integer.parseInt(idlist[i]),location)!=0) {
					//skips
				}
			}
		}else if(mode ==2) {
			for(int i=0;i<listmax;i++) {
				Main.changecounter("["+(i+1)+"/"+ listmax+"]");
				if(downloadbloodcat(captcha,Integer.parseInt(idlist[i]),location)!=0) {
					downloadbeatmapset(Integer.parseInt(idlist[i]),login,location);
				}
			}
		}

		
	}
	public int downloadbloodcat(String human, int id, String path) {
		return download("http://bloodcat.com/osu/s/"+id,path+"\\"+id+".osz",human);
	}
	public void downloadbeatmapset(int id,String cookie, String dir){
		Boolean downloadable = false;
		HttpsURLConnection connection = null;
		String tarlink = "";
		try {
			URL url = new URL("https://osu.ppy.sh/beatmapsets/"+id+"/download");	
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("User-Agent", "");  
			connection.setDoOutput(true); 
			connection.setDoInput(true); 
			connection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
			int code = connection.getResponseCode();
			if(code==302) {
				tarlink = connection.getHeaderField("location");
				downloadable = true;
			}else {
				downloadable = false;
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    if (connection != null) {
			      connection.disconnect();
			    }
			  }
		if(downloadable) {
			download(tarlink,dir+"\\"+id+".osz","");
		}
	}
	public int download(String remotePath, String localPath, String cookies) {
	    BufferedInputStream in = null;
	    FileOutputStream out = null;
	    
	    try {
	        URL url = new URL(remotePath);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36");
		    conn.setRequestMethod("GET");
	        if(cookies.length()>0) {
	        	conn.addRequestProperty("Cookie", cookies);
	        }
	        int size = conn.getContentLength();

	        if (size < 0) {
	        }else if(size==33) {
	        	return 1;
	    	}else {
	        }

	        in = new BufferedInputStream(conn.getInputStream());
	        out = new FileOutputStream(localPath);
	        byte data[] = new byte[1024];
	        int count;
	        double sumCount = 0.0;
	        int showpercent = 0;
	        while ((count = in.read(data, 0, 1024)) != -1) {
	            out.write(data, 0, count);

	            sumCount += count;
	            int percentage = (int)(sumCount / size * 100.0);
	            Main.changeprogress(percentage);
	            if (size > 0 && showpercent<percentage) {
	            	Main.changeprogress(percentage);
	            	showpercent+=1;
	            }
	        }

	    } catch (MalformedURLException e1) {
	        e1.printStackTrace();
	    } catch (IOException e2) {
	        e2.printStackTrace();
	    } finally {
	        if (in != null)
	            try {
	                in.close();
	            } catch (IOException e3) {
	                e3.printStackTrace();
	            }
	        if (out != null)
	            try {
	                out.close();
	            } catch (IOException e4) {
	                e4.printStackTrace();
	            }
	    }
	    //success
	    return 0;
	}
}
