import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main one = new Main();
		one.dowork();
	}
	
	public void dowork() {
		Boolean go = true;
		String key = "";
		String location="";
		while(go) {
			if(location.length()==0) location = getOsuDir();
			while(location.length()==0) {
				System.out.println("osu! 노래 폴더를 찾지 못했습니다.\n직접 입력해 주세요 : ");
				String pathinput = getinput();
				File songsdirtemp = new File(pathinput);
				if(songsdirtemp.exists()) location = pathinput;
			}
			System.out.println("osu! 노래 폴더 : " + location);
			
			System.out.println("비트맵 복붙 by junheah\n\n1.비트맵 백업\n2.비트맵 복원\n3.종료\n");
			System.out.print("모드 : ");
	        int input = Integer.parseInt(getinput());
			System.out.println();
			if(input==1) {
				System.out.println("백업 모드");
				String bname="";
				File output;
				while(bname.length()==0) {
					System.out.print("backup file name : ");
					bname=getinput();
					output = new File(bname + ".osubak");
					if(output.exists()) {
						System.out.println("file already exists.");
						bname="";
					}
				}
				//get song id list to array list
				List<Integer> songs = getSongs(location);
				output = new File(bname + ".osubak");
				//write to file
				BufferedWriter outputWriter = null;
				  
				try {
					outputWriter = new BufferedWriter(new FileWriter(output));
					for(int i=0; i<songs.size(); i++) {
						if(i==songs.size()-1) outputWriter.write(songs.get(i));
						else outputWriter.write(songs.get(i)+",");
					}			
					outputWriter.flush();  
					outputWriter.close(); 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("백업을 성공적으로 완료했습니다\n출력 파일 : "+bname+".osubak\n");
				
			}else if(input==2) {
				System.out.println("복원 모드");
				//get user id & pw then test login
				String user="", password="";
				while(true) {
					System.out.print("username : ");
					user = getinput();
					System.out.print("password : ");
					password = getinput(); 
					if(logintest(user,password)) {
						System.out.println("Login Success!\n");
						break;
					}
					else System.out.println("Login failed. Try again");
				}
				//parse backup
				String bname = "";
				File output;
				while(bname.length()==0) {
					System.out.print("백업 파일의 이름을 입력해 주세요 (같은 폴더에 있어야 함, 확장자 포함) : ");
					bname=getinput();
					output = new File(bname);
					if(output.exists()) {
						System.out.println("file found!\n");
					}else {
						System.out.println("file not found!");
						bname="";
					}
				}
				List<Integer> songlist = getSongs(location);
				
				int listmax = songlist.size();
				System.out.println("총 " + listmax + "개의 맵셋을 다운로드 합니다");
				String cookie = login(user,password);
				for(int i=0;i<listmax;i++) {
					System.out.println("["+(i+1)+"/"+ listmax+"]");
					downloadbeatmapset(songlist.get(i),cookie,location);
				}
				
				
				//check if bloodcat available
				//preferences : novid, no hitsounds,  no background
			}else if(input==3) {
				bloodcatcaptcha(517064);
				
				
				//go = false;
			}			
		}
	}
	public void bloodcatcaptcha(int id) {
		  
		  String cookie = "";
		  Boolean captcha = false;
		 //while(!captcha) {
			  HttpURLConnection connection = null;
			  try {
			    //Create connection
			    URL url = new URL("http://bloodcat.com/osu/s/"+id);
			    connection = (HttpURLConnection) url.openConnection();
			    connection.setRequestMethod("POST");
			    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			    connection.setRequestProperty("User-Agent", "");
			    connection.setDoOutput(true); 
			    connection.setDoInput(true); 
			    if(cookie.length()>0) {
			    	connection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
			    }
			    //Send request
			    DataOutputStream wr = new DataOutputStream (
			        connection.getOutputStream());
			    wr.close();
			    
			    //Get Response  
			    cookie = connection.getHeaderField("Set-cookie");
			    //System.out.println(cookie);
			    int code = connection.getResponseCode();
			    if(code==200) {
			    	captcha=true;
			    }else {
			    	 //Get Response  
			        //InputStream is = connection.getInputStream();
			        //BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			        //String line;
			        //int lineno=1;
//			        while ((line = rd.readLine()) != null) {
//			        	System.out.println(lineno+" "+line);
//			        	lineno++;
//			        }
//			        rd.close();
			    	captcha=true;
			    }
			  } catch (Exception e) {
			    e.printStackTrace();
			  } finally {
			    if (connection != null) {
			      connection.disconnect();
			    }
			  }
			  //return cookie
		  //}
		
	}
	public void downloadbeatmapset(int id,String cookie, String dir){
		Boolean downloadable = false;
		System.out.println("BeatmapSet id : " + id);
		HttpsURLConnection connection = null;
		String tarlink = "";
		try {
			System.out.print("fetching link...");
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
				System.out.println("failed");
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
			System.out.println("complete!");
			download(tarlink,dir+"\\"+id+".osz");
			System.out.println("Download complete!\n");
		}
	}
	
	public static void download(String remotePath, String localPath) {
	    BufferedInputStream in = null;
	    FileOutputStream out = null;

	    try {
	        URL url = new URL(remotePath);
	        URLConnection conn = url.openConnection();
	        int size = conn.getContentLength();

	        if (size < 0) {
	            System.out.println("Could not get the file size");
	        } else {
	            System.out.println("File size: " + size);
	        }

	        in = new BufferedInputStream(url.openStream());
	        out = new FileOutputStream(localPath);
	        byte data[] = new byte[1024];
	        int count;
	        double sumCount = 0.0;
	        System.out.print("[");
	        int showpercent = 0;
	        while ((count = in.read(data, 0, 1024)) != -1) {
	            out.write(data, 0, count);

	            sumCount += count;
	            int percentage = (int)(sumCount / size * 100.0);
	            if (size > 0 && showpercent<percentage) {
	            	System.out.print(">");
	            	showpercent+=2;
	            }
	        }
	        System.out.println("]");

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
	}
	
	public String login(String user, String password) {
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
		    //String cookie = connection.getHeaderField("cookie");
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
	
	String getOsuDir() {
		String path = System.getenv("LOCALAPPDATA")+"\\osu!\\Songs";
		File dir = new File(path);
		if(dir.exists()) {
			return path;
		}
		//return "";
		return "D:\\Program Files (x86)\\osu!\\Songs";
		
	}
	
	public List<Integer> getSongs(String directoryPath) {
	    File directory = new File(directoryPath);
		
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
