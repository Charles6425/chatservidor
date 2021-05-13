/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Infra;

import Model.ModelChat;
import Model.ModelChat.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.bind.Marshaller;

/**
 *
 * @author charles
 */
public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();
    private Map<String, ObjectOutputStream> mapGrupo = new HashMap<String, ObjectOutputStream>();
    private Map<String, String> mapUserGroup = new HashMap<String, String>();

    public ServidorService() {
        try {
            serverSocket = new ServerSocket(12345);
            while (true) {
                socket = serverSocket.accept();
                new Thread(new ListenerSocket(socket)).start();

            }

        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private class ListenerSocket implements Runnable {

        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            Model.ModelChat message = null;
            try {

                while ((message = (ModelChat) input.readObject()) != null) {
                    Action action = message.getAction();

                    if (action.equals(action.CONNECT)) {
                        boolean isConnect = connect(message, output);
                        if (isConnect) {
                            mapOnlines.put(message.getNome(), output);
                            mapGrupo.put(message.getGrupo(), output);
                            mapUserGroup.put(message.getNome(), message.getGrupo());
                            sendOnlines();
                        }
                    } else if (action.equals(action.DISCONNECT)) {
                        disconnect(message, output);
                        sendOnlines();
                        return;
                    } else if (action.equals(action.SEND_ONE)) {
                        sendOne(message);
                    } else if (action.equals(action.SEND_ALL)) {
                        sendAll(message);
                    } else if (action.equals(action.SEND_GROUP)) {
                        sendGroup(message);
                    } else if (action.equals(action.USERS_ONLINE)) {

                    }

                }

            } catch (IOException ex) {
                ModelChat cm = new ModelChat();
                cm.setNome(message.getNome());
                //charles
                cm.setGrupo(message.getGrupo());
                disconnect(cm, output);
                sendOnlines();
                
                //System.out.println(message.getNome()+ " deixou o chat!");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(
                        ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*} catch (IOException ex) {
                disconnect(message, output);
                sendOnlines();
                message.setTexto(message.getNome() + " Deixou o chat!");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }*/

        }
    }

    private boolean connect(ModelChat message, ObjectOutputStream output) {
        if (mapOnlines.size() == 0) {
            message.setTexto("sim");
            send(message, output);
            return true;
        }

        if (mapOnlines.containsKey(message.getNome())) {
            message.setTexto("nao");
            send(message, output);
            return false;
        } else {
            message.setTexto("sim");
            send(message, output);
            return true;
        }
    }

    private void send(ModelChat message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendOne(ModelChat message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (kv.getKey().equals(message.getNomeReservado())) {
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    //14/10/18 - Charles Müller envio de mensagem para o grupo
    private void sendGroup(ModelChat message) {

        for (Map.Entry<String, ObjectOutputStream> kv : mapGrupo.entrySet()) {
            if (kv.getKey().equals(message.getGrupoReservado())) {
                String grupo = kv.getKey();
                Set<String> chaves = mapUserGroup.keySet();

                for (String chave : chaves) {
                    if (mapUserGroup.get(chave).equals(grupo)) {
                        message.setAction(Action.SEND_ONE);
                        try {
                            mapOnlines.get(chave).writeObject(message);
                        } catch (IOException ex) {
                            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

    }

    private void sendAll(ModelChat message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (!kv.getKey().equals(message.getNome())) {
                message.setAction(Action.SEND_ONE);
                try {
                    kv.getValue().writeObject(message);

                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    private void disconnect(ModelChat message, ObjectOutputStream output) {
        mapOnlines.remove(message.getNome());
        message.setTexto(" Até Logo! Desconectado do chat!");
        message.setAction(Action.SEND_ONE);
        sendAll(message);

    }

    private void sendOnlines() {
        Set<String> setNames = new HashSet<String>();
        Set<String> setGrupo = new HashSet<String>();

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            setNames.add(kv.getKey());

        }
        for (Map.Entry<String, ObjectOutputStream> kv : mapGrupo.entrySet()) {
            setGrupo.add(kv.getKey());

        }
        ModelChat message = new ModelChat();
        message.setAction(Action.USERS_ONLINE);
        message.setSetOnlines(setNames);
        message.setSetGrupo(setGrupo);

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            message.setNome(kv.getKey());

            try {

                kv.getValue().writeObject(message);

            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
        for (Map.Entry<String, ObjectOutputStream> kv : mapGrupo.entrySet()) {
            message.setGrupo(kv.getKey());

            try {

                kv.getValue().writeObject(message);

            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}
