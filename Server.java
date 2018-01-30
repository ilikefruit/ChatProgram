package chat;

import java.io.*;//java library
import java.net.*;//java library
import java.util.*;//java library
@SuppressWarnings("unchecked")
public class Server{//server class
	ArrayList clientOutputStreams;//output streams list
	ArrayList<Integer> clientList = new ArrayList<Integer>();//client ports list
	Socket clientSocket ;//client socket

	public class ClientHandler implements Runnable{//run
		BufferedReader reader;//buffer
		Socket sock;//socket

		public ClientHandler(Socket clientSocket){//handler
			try{//socket set
				sock = clientSocket;
				//buffer
				InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
			} catch (Exception ex){//exception
				ex.printStackTrace();
			}
		}
		public void run(){//start
			String message, wd;//message save
			int cs;//port num save
			try{//client send message
				while((message = reader.readLine()) != null){
					System.out.println(message);//print message
					tellEveryone(message);//send to all client
					//message token -> port : message==List?
					StringTokenizer tok = new StringTokenizer(message,":");
					int p = Integer.parseInt(tok.nextToken());//port num save
					String pg = tok.nextToken();//message save
					if(pg.equals("list")){//if list request
						for(int i : clientList){//print client portnums
							tellEveryone("**chat member : "+i);
						}
					}
				}
			} catch(Exception ex){//exception
				ex.printStackTrace();
			}
		}
	}
	public static void main(String[] args){//main
		System.out.println("2014722084 Kim,Yun-Jeong");//print name
		new Server().go();//start
	}
	public void go(){
		clientOutputStreams = new ArrayList();//make arraylist
		try {//server socket set
			ServerSocket serverSock = new ServerSocket(5000);
			while(true){//client accept
				clientSocket = serverSock.accept();
				clientList.add(clientSocket.getPort());//client port num add
				//buffer save
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer);
				//send to message client's portnum
				tellEveryone("<<"+clientSocket.getPort());
				//make a thread
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();//start
				//print connection message
				System.out.println(clientSocket.getPort()+" got a connection");
				//send message to clients
				tellEveryone(clientSocket.getPort()+":"+clientSocket.getPort()+" is come to room");
			}
		} catch(Exception ex){//exception
			ex.printStackTrace();
		}
		//연결 꾾었을 때 close() 필요없나..
		
	}
	public void tellEveryone(String message){//send message to all clients
		Iterator it = clientOutputStreams.iterator();
		while(it.hasNext()){//if send message exist
			try{//writer
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);//send message
				writer.flush();//reset
			} catch(Exception ex){//exception
				ex.printStackTrace();
			}
		}
	}
}