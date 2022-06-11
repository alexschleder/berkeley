import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class BerkeleyMaster 
{

    private class Slave
    {
        public int pid;
        public int delay;
        public long time;

        public Slave(int pid, int delay, long time)
        {
            this.pid = pid;
            this.delay = delay;
            this.time = time;
        }
    }

    public static void main (String [] args)  throws IOException
    {
		if (args.length != 4) {
			System.out.println("Uso: java BerkeleyMaster <id> <host> <port> <processquantity>");
			return;
		}

        String host = args[1];
        InetAddress grupo = InetAddress.getByName("224.0.0.1");
        int port = Integer.parseInt(args[2]);
        int processQuantity = Integer.parseInt(args[3]);

        Slave[] slavesInfo = new Slave[processQuantity];

        Clock time = new Clock(System.currentTimeMillis());
        time.start();

        MulticastSocket socket = new MulticastSocket(port);
        socket.joinGroup(grupo);
        DatagramSocket uniSocket = new DatagramSocket(port + 1);
        
        try 
        {
            byte[] request = new byte[1024];
            request = host.getBytes();
            DatagramPacket requestPacket = new DatagramPacket(request,request.length, grupo, port);

            long timestamp = time.getTime();
            for (int i = 0; i < processQuantity; i++)
            {
                byte[] response = new byte[1024];
                DatagramPacket responsePacket = new DatagramPacket(response,response.length);
                socket.setSoTimeout(500);
                socket.receive(responsePacket);
                String recebido = new String(responsePacket.getData(),0,responsePacket.getLength());
                String vars[] = recebido.split("\\s");
                //Insere as informações recebidas no slavesInfo[i]

            }
            //Chama a função para calcular os tempos a serem somados
            
            //Cria o pacote no formado pid1:tempo1, pid2:tempo2... pidn:tempon

            //manda o pacote em multicast
        } 

        catch (IOException e) 
        {
        }
		
		//socket.leaveGroup(grupo);
		//socket.close();
    }

}