import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer implements Serializable
{
   final int PORT = 4444;
   Integer clientTurnNumber = 0;
   int numPlayers = 0;
   Random rng = new Random();
   private final boolean DEAD = false;

   //holds all of the object writers to be used
   private Vector<ObjectOutputStream> clientWriteList = new Vector<>();

   //holds all of the fighter objects(boss/4 players)
   public Vector<Fighter> clientList = new Vector<Fighter>();

   protected Fighter bossMan;

   public static void main(String[] args)
   {
      new GameServer();
   }
   
   public GameServer()
   {
      ServerSocket server = null;

      Fighter[] bossList = {new Diablo(), new DemonKing(), new Saitama()};

      bossMan = bossList[rng.nextInt(3)];
      clientList.add(bossMan);

      System.out.println(bossMan.getName() + " is the boss");

      try
      {
         System.out.println("Server started on IP: " + InetAddress.getLocalHost());
         server = new ServerSocket(PORT);
      }
      catch(BindException be){ System.out.println("Server already running on this port..."); }
      catch(UnknownHostException uhe){ uhe.printStackTrace(); } 
      catch(IOException ioe){ ioe.printStackTrace(); }

      //allows up to three players to connect
      while(clientTurnNumber != 3)
      {
         try
         {
            Socket client = server.accept();
            
            Thread ch = new ClientHandler(client);
            ch.start();
            numPlayers++;
            System.out.println("Players: " + numPlayers);
         }
         catch(IOException ioe){ ioe.printStackTrace(); }
      }
      //close off server once three people have connected
      try
      {
         server.close();
      }
      catch(IOException ioe){ }
   }
   
   class ClientHandler extends Thread
   {
      Socket cs;
      ObjectInputStream clientBuffer;
      ObjectOutputStream clientWriter;

      public ClientHandler(Socket _cs)
      {
         cs = _cs;

         try
         {
            //add client to the object writer vector
            clientWriter = new ObjectOutputStream(new DataOutputStream(cs.getOutputStream()));
            clientWriteList.add(clientWriter);

            //add an object reader for each client as they connect
            clientBuffer = new ObjectInputStream(new DataInputStream(cs.getInputStream()));

            //add the entire fighter object to the clientList vector
            clientList.add((Fighter)clientBuffer.readObject());
            System.out.println("Vector Size: " + clientList.size());

            clientTurnNumber++;
            clientWriter.writeInt(clientTurnNumber);

            broadcastClientListToClients();

            if(clientTurnNumber == 3)
            {
               clientTurnNumber = 1;
               broadcastClientListToClients();
               broadcastTurnNumberToClients();
            }
         }
         catch(EOFException | SocketException e)
         {
            System.out.println("got a tester");
            numPlayers--;
            clientTurnNumber--;
            clientWriteList.remove(clientWriteList.size()-1);
            return;
         }
         catch(IOException ioe){ ioe.printStackTrace(); }
         catch(ClassNotFoundException cnfe){ cnfe.printStackTrace(); }
         catch(NullPointerException npe){ npe.printStackTrace(); }
      }
      
      public void run()
      {
         Object obj;

         try
         {
            while((obj = clientBuffer.readObject()) != null)
            {
               if(obj instanceof String)
               {
                  System.out.println("got a string");
                  broadcastChatToClients((String)obj);
               }
               //when a vector is received, update the server's copy of the vector and broadcast it out
               //then, increment the turn number and broadcast that out
               if(obj instanceof Vector)
               {
                  System.out.println("got a vector");

                  clientList = (Vector<Fighter>)obj;

                  if(clientTurnNumber == 3)
                  {
                     clientTurnNumber = 0;
                     doBossStuff();
                     broadcastClientListToClients();
                     broadcastTurnNumberToClients();
                  }
                  else
                  {
                     clientTurnNumber++;
                     broadcastClientListToClients();
                     broadcastTurnNumberToClients();
                  }
               }
            }
         }
         catch(SocketException se){ System.out.println("Client Disconnected"); }
         catch(IOException ioe) { ioe.printStackTrace(); }
         catch(ClassNotFoundException cnfe){ cnfe.printStackTrace(); }
         catch(NullPointerException npe){ npe.printStackTrace(); }
      }

      private void doBossStuff()
      {
         int abilityNum = rng.nextInt(3);

         if(abilityNum == 0)
         {
            int target = rng.nextInt(3) + 1;
            clientList.get(target).changeCurrentHealth( - bossMan.ability1());
            broadcastChatToClients(bossMan.getName() + " attacked " + clientList.get(target).getName() + " for " + bossMan.ability1() + " damage!");
         }
         if(abilityNum == 1)
         {
            clientList.get(0).changeCurrentHealth(bossMan.ability2());
            broadcastChatToClients(bossMan.getName() + " healed for " + bossMan.ability2());
         }
         if(abilityNum == 2)
         {
            int damage = bossMan.ability3();

            for(int i=1; i < clientList.size(); i++)
            {
               clientList.get(i).changeCurrentHealth( - damage);
            }
            broadcastChatToClients(bossMan.getName() + " attacked everyone for " + damage + " damage!");
         }

         clientTurnNumber++;
         broadcastClientListToClients();
         broadcastTurnNumberToClients();
      }

      public void broadcastChatToClients(String message)
      {
         //print out the received message to each client that is connected
         for(ObjectOutputStream oos : clientWriteList)
         {
            try
            {
               oos.writeObject(message);
               oos.flush();
               oos.reset();
            }
            catch(IOException ioe){ ioe.printStackTrace(); }
         }
      }

      public void broadcastClientListToClients()
      {
         checkHealths();
         //print out the updated client list to each client
         for(ObjectOutputStream oos : clientWriteList)
         {
            try
            {
               oos.writeObject(clientList);
               oos.flush();
               oos.reset();
            }
            catch(IOException ioe){ ioe.printStackTrace(); }
         }
      }

      public void broadcastTurnNumberToClients()
      {
         for(ObjectOutputStream oos : clientWriteList)
         {
            try
            {
               oos.writeObject(clientTurnNumber);
               oos.flush();
               oos.reset();
            }
            catch(IOException ioe){ ioe.printStackTrace(); }
         }
      }

      private void checkHealths()
      {
         //check if boss is dead
         if(clientList.get(0).getCurrentHealth() <= 0)
         {
            clientList.get(0).setCurrentHealth(0);
            broadcastChatToClients("You win!");
         }

         //doesn't allow players to heal past their base health
         for(Fighter a : clientList)
         {
            if(a.getCurrentHealth() > a.getBaseHealth())
            {
               a.setCurrentHealth(a.getBaseHealth());
            }
         }

         //if a player is dead, set their icon and status to dead
         for(int i=1; i < clientList.size(); i++)
         {
            if(clientList.get(i).getCurrentHealth() <= 0)
            {
               clientList.get(i).setCurrentHealth(0);
               clientList.get(i).setIcon("dead.png");
               clientList.get(i).setFighterAlive(DEAD);
            }
         }

         if(clientList.get(1).getCurrentHealth() == 0 && clientList.get(2).getCurrentHealth() == 0 && clientList.get(3).getCurrentHealth() == 0)
         {
            broadcastChatToClients("You lose!");
         }
      }
   }//End class Client Handler
}//End class file (GameServer)