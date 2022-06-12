import java.util.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BerkeleyMaster 
{
    static long maxDelay;

    public static void main (String [] args)  throws IOException
    {
        if (args.length != 4) 
        {
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
            while(true)
            {
                byte[] request = new byte[1024];
                request = host.getBytes();
                DatagramPacket requestPacket = new DatagramPacket(request,request.length, grupo, port);

                long timestamp = time.getTime();
                System.out.println("Begin at: " + timestamp);
                System.out.println("-----------------");
                for (int i = 0; i < processQuantity; i++)
                {
                    byte[] response = new byte[1024];
                    DatagramPacket responsePacket = new DatagramPacket(response,response.length);
                    uniSocket.setSoTimeout(0);
                    uniSocket.receive(responsePacket);
                    String recebido = new String(responsePacket.getData(),0,responsePacket.getLength());
                    System.out.println("Received: " + recebido);
                    //Datagrama Recebido = pid time adelay
                    String vars[] = recebido.split("\\s");
                    //Insere as informações recebidas no slavesInfo[i]
                    int pid = Integer.parseInt(vars[0]);
                    long responseTimestamp = Long.parseLong(vars[1]);
                    long delay = responseTimestamp - timestamp + Long.parseLong(vars[2]);

                    Slave slave = new Slave(pid, responseTimestamp, delay);
                    slavesInfo[i] = slave;
                }
                //Chama a função para calcular os tempos a serem somados
                
                //Cria o pacote no formado pid1:tempo1, pid2:tempo2... pidN:tempoN
                String responseString = "";
                System.out.println("Response to slaves: " + responseString);
                //manda o pacote em multicast
                byte[] response = new byte[2048];
                response = responseString.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(response,response.length, grupo, port);
                socket.send(responsePacket);

                Thread.sleep(3000);
            }
        } 

        catch (IOException e) 
        {
        }

        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
		
		//socket.leaveGroup(grupo);
		//socket.close();
    }

    public static ArrayList<Long> adjustClocks(long masterTime, ArrayList<Long> slaveClockList) {
        ArrayList<Long> adjustList = new ArrayList<>();
        
        long totalDiff = 0;
        int counter = 0;

        // Adiciona diferencas para adjustList e soma os valores que nao ultrapassam o limite
        // na a variavel totalDiff, contando eles em counter
        for(int i=0; i<slaveClockList.size(); i++) {
            long curDiff = slaveClockList.get(i) - masterTime;
            adjustList.add(curDiff);

            if(curDiff < maxDelay) {
                totalDiff += curDiff;
                counter++;
            }
        }
        counter++;

        // fazer print dps
        // Calcula media dos valores validos
        long averageDiff = totalDiff/counter;

        // Calcula os ajustes de cada relogio com base na media
        for(int i=0; i<adjustList.size(); i++) {
            // Ajuste = media - (diferenca para o tempo mestre)
            adjustList.set(i,(averageDiff - adjustList.get(i)));
        }

        // Lembrar: Mestre sempre adiciona a media em seu proprio relogio
        return adjustList;
    }
}