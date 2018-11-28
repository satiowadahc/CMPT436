
import java.iio.*;
import java.net.*;
import java.util.Scanner;


public class Server{
	
	// Room list
	static Vector<RoomHandler> rh = new Vector<>();
	// Room Counter
	static int rhc = 0;
	
	// Client List
	static Vector<ClientHandler> ch = new Vector<>();
	static int chc = 0;
	
	public static void main(String[] args) throws IOException{
		ServerSocket ss = new ServerSocket(1234);
		Socket s;
		
		while(true){
			
			s = ss.accept();
			System.out.println("Client Joined at: " + s);
			
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
		
			//TODO Change names? is outta scope of the assignment
			ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos); 
			Thread t = new Thread(mtch); 
			
			ch.add(mtch);
			t.start();
			
			chc++;
		} // end loop
	
	}//end main

class ClientHandler implements Runnable{
	Scanner scn = new Scanner(System.in);
	private String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s; 
	boolean isloggedin;
	

	public ClientHandler(Socket s, String name, 
							DataInputStream dis, DataOutputStream dos) { 
		this.dis = dis; 
		this.dos = dos; 
		this.name = name; 
		this.s = s; 
		this.isloggedin=true; 
	} 
	
	@Override
	public void run(){
		String received;
		while(true){
			try{
				received.readUTF();
				System.out.println(received); 
				//Parse this
				if(received.equals("logout")){ 
					this.isloggedin=false; 
					this.s.close(); 
					break; 
				} else if(received.equals("getRooms")){
					this.dos.writeUTF("Rooms" + Server.rh);
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}//end run
}//end Client Handler

class RoomHandler implements Runnable{
	private String name;
	public Vector<String> messages = new Vector<>();
	public Vector<ClientHandler> actives = new Vector<>();
	
	public RoomHandler(String name){
		this.name = name;
	}
}
	
}//end Server