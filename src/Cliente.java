import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.*;

public class Cliente {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MarcoCliente marco=new MarcoCliente();
		marco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class MarcoCliente extends JFrame{
	public MarcoCliente(){
		setBounds(600, 300, 250, 350);
		setTitle("Cliente");
		LaminaMarcoCliente lamina=new LaminaMarcoCliente();
		add(lamina);
		setVisible(true);
		addWindowListener(new envioIp());
	}
}
//-----------------Envío de señal online-------------------------
class envioIp extends WindowAdapter{
	
	public void windowOpened(WindowEvent e){
	
		try {
			Socket miSocket=new Socket("192.168.0.165", 9999);
			PaqueteEnvio datos=new PaqueteEnvio();
			datos.setMensaje(" online");
			ObjectOutputStream salida=new ObjectOutputStream(miSocket.getOutputStream());
			salida.writeObject(datos);
			miSocket.close();
		}catch (Exception e2){
			
		}
	}
}
//--------------------------------------------------------------
class LaminaMarcoCliente extends JPanel implements Runnable{
	public LaminaMarcoCliente(){
		JLabel nombreNick=new JLabel("Nick:");
		add(nombreNick);
		nick=new JLabel(JOptionPane.showInputDialog("Nick: "));
		add(nick);
		JLabel texto=new JLabel("Online");
		add(texto);
		ip=new JComboBox();
		add(ip);
		chat=new JTextArea(12, 20);
		add(chat);
		campo1=new JTextField(20);
		add(campo1);
		boton=new JButton("Enviar");
		EnviaTexto evento=new EnviaTexto();
		boton.addActionListener(evento);
		add(boton);
		
		Thread miHilo=new Thread(this);
		miHilo.start();
	}
	
	private class EnviaTexto implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				chat.append("Yo: "+campo1.getText()+"\n");
				Socket miSocket=new Socket("192.168.0.165", 9999);
				PaqueteEnvio datos=new PaqueteEnvio();
				datos.setIp(ip.getSelectedItem().toString());
				datos.setNick(nick.getText());
				datos.setMensaje(campo1.getText());
				ObjectOutputStream paqueteDatos=new ObjectOutputStream(miSocket.getOutputStream());
				paqueteDatos.writeObject(datos);
				miSocket.close();
				campo1.setText("");
				System.out.println("EStoy aqui");
				
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());		
			}
		}
	}
	

	
	private JTextArea chat;
	private JButton boton;
	private JTextField campo1;
	private JComboBox ip;
	private JLabel nick;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			ServerSocket servidorCliente=new ServerSocket(9090);
			Socket cliente;
			PaqueteEnvio paqueteRecibido;
			
			while(true){
				cliente=servidorCliente.accept();
				ObjectInputStream flujoEntrada=new ObjectInputStream(cliente.getInputStream());
				paqueteRecibido=(PaqueteEnvio) flujoEntrada.readObject();
				if (paqueteRecibido.getMensaje().equals(" online")){
					chat.append(""+paqueteRecibido.getIps());
					ArrayList<String> ipsMenu=new ArrayList<String>();
					ipsMenu=paqueteRecibido.getIps();
					ip.removeAllItems();
					for (String string : ipsMenu) {
						ip.addItem(string);
					}
				}else {
					chat.append(paqueteRecibido.getNick()+":"+paqueteRecibido.getMensaje()+"\n");
				}			
				cliente.close();
			}
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}	
}

class PaqueteEnvio implements Serializable{
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public ArrayList getIps() {
		return ips;
	}

	public void setIps(ArrayList ips) {
		this.ips = ips;
	}

	private String nick, ip, mensaje;
	private ArrayList ips;
}


