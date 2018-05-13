import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main one = new Main();
		one.dowork();
	}
	
	
	public void dowork() {
		Boolean go = true;
		//String key = "";
		String location="";
		while(go) {
			System.out.println("비트맵 복 붙 유틸 (OSABRU) v0.2   -   By junheah [osu.ppy.sh/user/junheah]\n");
			if(location.length()==0) location = getOsuDir();
			while(location.length()==0) {
				System.out.println("osu! 노래 폴더를 찾지 못했습니다.\n직접 입력해 주세요 : ");
				String pathinput = getinput();
				File songsdirtemp = new File(pathinput);
				if(songsdirtemp.exists()) location = pathinput;
			}
			System.out.println("osu! 노래 폴더 : " + location+"\n");
			
			
			System.out.println("1.비트맵 백업\n2.비트맵 복원\n3.종료\n");
			System.out.print("모드 : ");
	        int input = Integer.parseInt(getinput());
			System.out.println();
			if(input==1) {
				System.out.println(" << 백업 모드 >> ");
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
				System.out.println(" << 복원 모드 >> ");
				String human = "";
				int choice = 0;
				while(true) {
					System.out.println("\n1. osu.ppy.sh\n2. bloodcat.com/osu\n3. bloodcat/osu  >>>  osu.ppy.sh");
					System.out.print("\n다운로드 방식을 선택해 주세요 : ");
					choice = Integer.parseInt(getinput());
					if(choice == 1 || choice == 2 || choice==3) break;
				}
				String user="", password="";
				if(choice==1 || choice==3) {
					//get user id & pw then test login
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
				}
				if(choice==2 || choice==3) {
					human = bloodcatcaptcha();
				}
				//parse backup
				String bname = "";
				File output;
				while(bname.length()==0) {
					System.out.print("백업 파일의 이름을 입력해 주세요 (같은 폴더에 있어야 함, 확장자 포함) : ");
					bname = getinput();
					output = new File(bname);
					if(output.exists()) {
						System.out.println("file found!\n");
					}else {
						System.out.println("file not found!");
						bname="";
					}
				}
				
		        String[] listtmp = {};
		        try {
		        	BufferedReader br;
		        	br = new BufferedReader(new FileReader(output));
		            StringBuilder sb = new StringBuilder();
		            String line = br.readLine();

		            while (line != null) {
		                sb.append(line);
		                sb.append(System.lineSeparator());
		                line = br.readLine();
		            }
		            listtmp = sb.toString().split(",");
		            br.close();
		        } catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        
		        
				//lol what the fuck?
				//List<Integer> songlist = getSongs(location);
				
				int listmax = listtmp.size();
				System.out.println("총 " + listmax + "개의 맵셋을 다운로드 합니다");
				String cookie = login(user,password);
				
				if(choice ==1) {
					for(int i=0;i<listmax;i++) {
						System.out.println("["+(i+1)+"/"+ listmax+"]");
						downloadbeatmapset(Integer.parseInt(listtmp[i]),cookie,location);
					}
				}else if(choice ==2) {
					for(int i=0;i<listmax;i++) {
						System.out.println("["+(i+1)+"/"+ listmax+"]");
						if(downloadbloodcat(human,Integer.parseInt(listtmp[i]),location)!=0) {
							System.out.println("skipping...");
						}
					}
				}else if(choice ==3) {
					for(int i=0;i<listmax;i++) {
						System.out.println("["+(i+1)+"/"+ listmax+"]");
						if(downloadbloodcat(human,songlist[i],location)!=0) {
							downloadbeatmapset(Integer.parseInt(listtmp[i]),cookie,location);
						}
					}
				}
				
				
				//check if bloodcat available
				//preferences : novid, no hitsounds,  no background
				
				}else if(input==3) {
					go = false;
				}
//				else if(input==4) {
//				//temp mode for testing bloodcat
//				
//				String human = bloodcatcaptcha();
//				System.out.print("비트맵 셋 id를 입력해 주세요 : ");
//				int id = Integer.parseInt(getinput());
//				downloadbloodcat(human,id,location);
//				
//			}
		}
	}
	public int downloadbloodcat(String human, int id, String path) {
		System.out.println("BeatmapSet id : " + id);
		return download("http://bloodcat.com/osu/s/"+id,path+"\\"+id+".osz",human);
	}
	//this solves bloodcats captcha and returns cookie("obm_human") in string
	public String bloodcatcaptcha() {
		int id = 8023;
		  String hash="",sync="",b64s="";
		  String cookie = "";
		  String cinput = "";
		  Boolean captcha = false;
		  //Boolean requested = false;
		 while(!captcha) {
			  HttpURLConnection connection = null;
			  try {
			    //Create connection
			    URL url = new URL("http://bloodcat.com/osu/s/"+id);
			    connection = (HttpURLConnection) url.openConnection();
			    
			    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36");
			    connection.setDoOutput(true); 
			    connection.setDoInput(true); 
			    if(cinput.length()>0) {
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
				    	System.out.println("success!\n");
				    	cookie=connection.getHeaderField("Set-Cookie");
						   captcha = true;
						   break;
				    }else {
				    	System.out.println("fail\n");
				    	cinput="";
				    	continue;
				    }
			    }else {
			    	connection.setRequestMethod("GET");
			    }
			    //Send request
			    DataOutputStream wr = new DataOutputStream (
			        connection.getOutputStream());
			    wr.close();
			    
			    //Read cookie from response header
			    cookie = connection.getHeaderField("Set-cookie");
			    int code = connection.getResponseCode();
			    if(code==200) {
			    	captcha=true;
			    	break;
			    }else {
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
			        //asks for user input
			        System.out.print("\ncaptcha.png를 보고 보이는 숫자을 입력해 주세요 : ");
			        cinput = getinput();
			        
			       }
			  } catch (Exception e) {
			    e.printStackTrace();
			  } finally {
			    if (connection != null) {
			      connection.disconnect();
			    }
			  }
		  }
		 return cookie.split(";",2)[0];
		
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
			download(tarlink,dir+"\\"+id+".osz","");
			System.out.println("Download complete!\n");
		}
	}
	
	public static int download(String remotePath, String localPath, String cookies) {
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
	            System.out.println("Could not get the file size");
	        }else if(size==33) {
	        	System.out.println("beatmap inaccessible!");
	        	return 1;
	    	}else {
	            System.out.println("File size: " + size);
	        }

	        in = new BufferedInputStream(conn.getInputStream());
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
	    //success
	    return 0;
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
	
	String getOsuDir() {
		String path = System.getenv("LOCALAPPDATA")+"\\osu!\\Songs";
		File dir = new File(path);
		if(dir.exists()) {
			return path;
		}
		return "";
		//return "D:\\Program Files (x86)\\osu!\\Songs";
		
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
