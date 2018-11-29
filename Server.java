

import java.io.*;
import java.net.*;
import java.util.*;


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
                ClientHandler mtch = new ClientHandler(s,"client " + chc, dis, dos); 
                Thread t = new Thread(mtch); 

                ch.add(mtch);
                t.start();

                chc++;
            } // end loop
	
	}//end main
        
        public static void makeRoom(){
            RoomHandler newroom = new RoomHandler(rhc);
            //TODO Start Timer?
            rh.add(newroom);
            rhc++;
        }
        
        public static String getRooms(){
            String out= "";
            if(!rh.isEmpty()){
                for(RoomHandler room : rh){
                    out = out + " " + room.name;
                }
            }
            else{
                out = "No Rooms Open";
            }
            return out;
        }
        
        public static RoomHandler getRoom(String r){
            for(RoomHandler f : rh){
               if(f.name == Integer.parseInt(r)){
                   return f;
               } 
            }
                System.out.println("Room Not open");
                return null;
        }

static class ClientHandler implements Runnable{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    RoomHandler currentRoom = null;
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
        
            //Server Greeting
            try{
            this.dos.writeUTF("Welcome to the Server!");
            this.dos.writeUTF("CurrentRooms: ");
            this.dos.writeUTF(Server.getRooms());
            }catch(IOException E){
                E.printStackTrace();
            }
          
            String received;
            while(true){
                try{
                    received = dis.readUTF();
                    System.out.println(received); 
                    //Parse this
                    if(received.equals("#logout")){ 
                        this.isloggedin=false; 
                        this.s.close(); 
                        break; 
                    } else if(received.equals("#getRooms")){
                      this.dos.writeUTF(Server.getRooms());
                    } else if(received.startsWith("#join")){
                        if(currentRoom==null){
                            StringTokenizer st = new StringTokenizer(received, " "); 
                            String join = st.nextToken(); 
                            String room = st.nextToken();
                            getRoom(room).addClient(this);
                            currentRoom = getRoom(room);
                            currentRoom.listAllMessage(this);
                        }
                    } else if(received.equals("#create")){
                      Server.makeRoom();
                    } else if(received.equals("#listMembers")){
                        currentRoom.getMembers();
                    } else if(received.startsWith("#name")){
                        StringTokenizer st = new StringTokenizer(received, " "); 
                            String named = st.nextToken(); 
                            String newName = st.nextToken();
                            this.name = newName;
                    }
                      else if(received.equals("#exit")){
                      StringTokenizer st = new StringTokenizer(received, " "); 
                            String exit = st.nextToken(); 
                            String room = st.nextToken();
                            getRoom(room).removeClient(this);
                            currentRoom = null;
                    } else if(received.equals("#logout")){
                        break;
                    }
                      else{
                        currentRoom.sendMessage(this.name + ":" + received);
                    }
                }catch(IOException e){
                    System.out.println(e);
                }
            }
            try{ 
                this.dis.close(); 
		this.dos.close(); 	
            }catch(IOException e){ 
		System.out.println(e); 
	    } 

    }//end run
}//end Client Handler


static class RoomHandler{
    final int name;
    public Vector<String> messages = new Vector<>();   
    public Vector<ClientHandler> chr = new Vector<>();
    
    public RoomHandler(int name){
        this.name = name;
    }
    
    public void addClient(ClientHandler ch){
        this.chr.add(ch);
    }
    
    public void removeClient(ClientHandler ch){
        this.chr.remove(ch);
    }
    
    public void getMembers(){
        String out= "";
        if(!chr.isEmpty()){
            for(ClientHandler here : chr){
                out = out + " " + here.name;
            }
        }
        else{
            out = "No one here";
        }
    }
    
    public void listAllMessage(ClientHandler ch){
        try{
        for(String msg: this.messages){
            ch.dos.writeUTF(msg);
        }
        }catch(IOException e){
            System.out.println(e);
        }
    }
    
    
    public void sendMessage(String msg){
        this.messages.add(msg);
        try{
        for(ClientHandler client : this.chr){
            client.dos.writeUTF(msg);
        }
        }catch(IOException e){
            System.out.println(e);
        }
        
    }
    
    public String ToString(){
        return "Room " + this.name;
    }
    
}//End roomhandler
	
}//end Server