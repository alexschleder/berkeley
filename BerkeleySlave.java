import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BerkeleySlave 
{
    public static void main (String [] args)  throws IOException
    {
        if (args.length != 5) 
        {
			System.out.println("Uso: java BerkeleySlave <id> <host> <port> <ptime> <adelay>");
			return;
		}

        int id = Integer.parseInt(args[0]);
        String host = args[1];
        InetAddress grupo = InetAddress.getByName("225.0.0.0");
        int port = Integer.parseInt(args[2]);
        int ptime = Integer.parseInt(args[3]);
        long adelay = Long.parseLong(args[4]);

        Clock time = new Clock(System.currentTimeMillis());
        time.start();

        MulticastSocket socket = new MulticastSocket(port);
        socket.joinGroup(grupo);
        DatagramSocket uniSocket = new DatagramSocket(port + 1);  
        
        while (true)
        {
            byte[] request = new byte[1024];
            DatagramPacket requestPacket = new DatagramPacket(request,request.length);
            socket.setSoTimeout(0);
            System.out.println("Waiting for master request...");
            socket.receive(requestPacket);
            String recebido = new String(requestPacket.getData(),0,requestPacket.getLength());
            String recebidoSplit[] = recebido.split("\\s");
            InetAddress masterAddress = InetAddress.getByName(recebidoSplit[0]);
            int portToSend = Integer.parseInt(recebidoSplit[1]);
            System.out.println("Received: " + recebido);

            try
            {
                Thread.sleep(ptime);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

            //Datagrama enviado = pid time adelay
            String responseString = id + " " + time.getTime() + " " + adelay;
            System.out.println("Response to master: " + responseString);
            byte[] response = new byte[1024];
            response = responseString.getBytes();
            //TODO: Mudar porta para porta do master
            DatagramPacket responsePacket = new DatagramPacket(response,response.length, masterAddress, portToSend);
            uniSocket.send(responsePacket);

            byte[] masterResponse = new byte[1024];
            DatagramPacket masterResponsePacket = new DatagramPacket(masterResponse,masterResponse.length);
            socket.setSoTimeout(0);
            socket.receive(masterResponsePacket);
            String masterResponseString = new String(masterResponsePacket.getData(),0,masterResponsePacket.getLength());
            System.out.println("Response from master: " + masterResponseString);
            String[] masterDataSplit = masterResponseString.split("\\s");

            for (int i = 0; i < masterDataSplit.length; i++)
            {
                String current[] = masterDataSplit[i].split(":");
                if (Integer.parseInt(current[0]) == id)
                {
                    System.out.println("Time changed by: " + Long.parseLong(current[1]));
                    time.addToTime(Long.parseLong(current[1]));
                    break;
                }
            }
        }
    }
}