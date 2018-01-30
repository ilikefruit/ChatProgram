package chat;

import java.io.*;//java library
import java.net.*;//java library
import java.util.*;//java library
import javax.swing.*;//java library
import java.awt.*;//java library
import java.awt.event.*;//java library

public class Client{//client class
	JTextArea incoming;//text print variable
	JTextField outgoing;//text field variable
	BufferedReader reader;//buffer
	PrintWriter writer;//writer
	Socket sock;//socket
	int myport;//this client port num

	public static void main(String[] args){//main function
		Client client = new Client();//client
		client.go();//start
	}
	public void go(){
		JFrame frame = new JFrame("2014722084 Kim,Yun-Jeong client");//name print
		JPanel mainPanel = new JPanel();//panel make
		incoming = new JTextArea(15,50);//text field
		incoming.setLineWrap(true);//text area set
		incoming.setWrapStyleWord(true);//text area set
		incoming.setEditable(false);//text area set
		JScrollPane qScroller = new JScrollPane(incoming);//make scroller
		//scroller set
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outgoing = new JTextField(20);//text field
		JButton sendButton = new JButton("Send");//make a button
		sendButton.addActionListener(new SendButtonListener());//action listener set
		//add to main panel
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		setUpNetworking();
		//make a thread
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		//frame set
		frame.getContentPane().add(BorderLayout.CENTER,mainPanel);
		frame.setSize(400,500);
		frame.setVisible(true);
		frame.pack();
	}
	private void setUpNetworking(){//setting client information
		try{
			sock = new Socket("127.0.0.1",5000);//socket ip and portnum
			//make buffers
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			System.out.println("networking established");//link to server
		} catch(IOException ex){//exception
			ex.printStackTrace();
		}
	}
	//action
	public class SendButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent ev){
			try{//send message to server
				writer.println(myport+":"+outgoing.getText().trim());
				writer.flush();//reset
			} catch(Exception ex){//exception
				ex.printStackTrace();
			}
			outgoing.setText("");//textfield
			outgoing.requestFocus();
		}
	}
	//runnable
	public class IncomingReader implements Runnable{
		public void run(){
			incoming.append("2014722084 Kim,Yun-Jeong\n");//print name
			String message;
			try{//if server sends message
				while((message = reader.readLine()) != null){
					char[] temp = new char[2];
					message.getChars(0,2,temp,0);
					if (temp[0]=='<'&&temp[1]=='<'){//client's port num set
						StringTokenizer tok = new StringTokenizer(message,"<<");//token
						int p = Integer.parseInt(tok.nextToken());//port num set
						if(myport==0){//if haven't port num
							myport = p;
						}
						incoming.append(message+"\n");//print text area
					}
					else if (temp[0]=='*'&&temp[1]=='*'){//chat member list
						incoming.append(message+"\n");//print members port num
					}
					//private or general message
					else{
						StringTokenizer tok = new StringTokenizer(message,":");//token
						int sendP = Integer.parseInt(tok.nextToken());//send client port num
						String pg = tok.nextToken();//send message
						//if the private message is mine? 한 글자만 전송했는데 private메시지인줄 확인하기 위해 두 글자를 읽어서 에러 발생 예외처리 필요
						pg.getChars(0,2,temp,0);
						if (temp[0]=='>'&&temp[1]=='>'){//if private message
							StringTokenizer toktok = new StringTokenizer(pg,">>");//token
							int cs = Integer.parseInt(toktok.nextToken());//the message is mine?
							String wd = toktok.nextToken();//main message
							if(cs == myport)//if the message is mine
								incoming.append(sendP+":<private message>"+wd+"\n");
							else {}
						}
						else{//general message print
							incoming.append(sendP+":"+pg+"\n");
						}
					}
				}
			} catch(Exception ex){//exception 
				ex.printStackTrace();
			}
		}
	}
}
