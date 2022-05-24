package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @Author Hx
 * @Date 2022/5/17 20:17
 */
public class ClientThread implements Runnable {

    //���̸߳����Socket
    private final Socket socket;
    //�ַ�����
    BufferedReader in;
    //��Client������
    private String client_name;

    //������
    public ClientThread(String ip, int port, String name) throws IOException {
        socket = new Socket(ip, port);
        client_name = name;
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            //������Ϣ�����߳�
            new Thread(new MsgInput(socket)).start();
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(client_name + "����������");
            out.flush();
            String msg;
            //��ѭ��������Ϣ
            while ((msg = in.readLine()) != null) {
                out.println(client_name + "˵��" + msg);
                out.flush();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    //��ȡclient����
    public String getClient_name() {
        return client_name;
    }

    //����client����
    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    //���ص�ǰ�ͻ����̵߳�Socket
    public Socket getSocket() {
        return socket;
    }
}

class MsgInput implements Runnable {

    BufferedReader out;
    private final Socket s;

    public MsgInput(Socket s) throws IOException {
        this.s = s;
        out = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String msg = null;
            while ((msg = out.readLine()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

