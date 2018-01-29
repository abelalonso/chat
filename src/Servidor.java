import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Servidor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MarcoServidor marco=new MarcoServidor();
		marco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}

class MarcoServidor extends JFrame implements Runnable{
	public MarcoServidor(){
		setBounds(1200, 300, 280, 350);
		setTitle("Servidor");
		JPanel lamina=new JPanel();
		lamina.setLayout(new BorderLayout());
		areaTexto=new TextArea();
		lamina.add(areaTexto, BorderLayout.CENTER);
		add(lamina);
		setVisible(true);
		
		Thread miHilo=new Thread(this);
		miHilo.start();
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket servidor=new ServerSocket(9999);
			String nick, ip, mensaje;
			PaqueteEnvio paqueteRecibido=new PaqueteEnvio();
			ArrayList<String> listaIp=new ArrayList<String>();
			
			while(true){
				Socket miSocket=servidor.accept();
				ObjectInputStream paqueteDatos=new ObjectInputStream(miSocket.getInputStream());
				paqueteRecibido=(PaqueteEnvio) paqueteDatos.readObject();
				//----------------Detecta online--------------------
				if (paqueteRecibido.getMensaje().equals(" online")){
					InetAddress direccionCliente=miSocket.getInetAddress();
					String ipRemota=direccionCliente.getHostAddress();
					System.out.println("online "+ipRemota);
					listaIp.add(ipRemota);
					paqueteRecibido.setIps(listaIp);
					for (String string : listaIp) {
						Socket envioIp=new Socket(string, 9090);
						ObjectOutputStream streamIp=new ObjectOutputStream(envioIp.getOutputStream());
						streamIp.writeObject(paqueteRecibido);
						streamIp.close();
						envioIp.close();
						miSocket.close();
					}
					continue;
				}
				//--------------------------------------------------
				nick=paqueteRecibido.getNick();
				ip=paqueteRecibido.getIp();
				mensaje=paqueteRecibido.getMensaje();
				areaTexto.append(nick+": "+mensaje+" para "+ip+"\n");
				
				//Parte cliente 
				Socket enviaDestinatario=new Socket(ip, 9090);
				ObjectOutputStream paqueteReenvio=new ObjectOutputStream(enviaDestinatario.getOutputStream());
				paqueteReenvio.writeObject(paqueteRecibido);
				enviaDestinatario.close();
				paqueteReenvio.close();
				
				miSocket.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private TextArea areaTexto;
}
