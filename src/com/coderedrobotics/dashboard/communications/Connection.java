package com.coderedrobotics.dashboard.communications;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.coderedrobotics.dashboard.communications.exceptions.ConnectionResetException;
import com.coderedrobotics.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.dashboard.communications.exceptions.RootRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.RouteException;
import com.coderedrobotics.dashboard.communications.listeners.ConnectionListener;
import com.coderedrobotics.dashboard.communications.listeners.MultiplexingListener;
import com.coderedrobotics.dashboard.communications.listeners.SubsocketListener;

/**
 * The main Connection Thread that manages the TCP connection, sends and listens
 * for data, as well as many other things.
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class Connection {

    private ServerSocket serverSocket;
    private Socket tcpConnection;
    private BufferedInputStream input;
    private BufferedOutputStream output;

    private final ControlSubsocket controlSocket;
    private final RootSubsocket rootSocket;
    private final IDFactory idFactory;

    private static Connection connection = null;
    private final PrivateStuff privateStuff;
    private Thread thread;

    private static String ADDRESS = "10.27.71.2";
    private static int PORT = 1180;

    private static final ArrayList<ConnectionListener> connectionListeners = new ArrayList<>();

    private Connection() {
        idFactory = new IDFactory(this);
        rootSocket = new RootSubsocket("root", this);
        controlSocket = new ControlSubsocket("control", this);
        privateStuff = new PrivateStuff();
        setupListeners();
        start();
    }

    /**
     * Set the connection information.
     *
     * @param address the IP of the robot.
     * @param port the port that the robot is listening on. 1180 by default.
     */
    public void setAddress(String address, int port) {
        ADDRESS = address;
        PORT = port;
    }

    /**
     * Set the connection information.
     *
     * @param address the IP of the robot.
     */
    public void setAddress(String address) {
        ADDRESS = address;
    }

    private void setupListeners() {
        MultiplexingAlerts.addConnection(privateStuff);
        controlSocket.addListener(privateStuff);
    }

    private void start() {
        thread = new Thread(privateStuff);
        thread.start();
    }

    /**
     * Returns the instance of the Connection object. If there is not currently
     * running Connection instance, one is created.
     *
     * @return a Connection object instance.
     */
    public static Connection getInstance() {
        if (connection == null) {
            connection = new Connection();
        }
        return connection;
    }

    /**
     * Receive alerts for when the network becomes connected or disconnected.
     *
     * @param listener a ConnectionListener
     */
    public static void addConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
        connectionListeners.add(listener);
    }

    /**
     * Stop receiving alerts for when the network becomes connected or
     * disconnected.
     *
     * @param listener a ConnectionListener
     */
    public static void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    private static void alertConnected() {
        for (ConnectionListener listener : connectionListeners) {
            listener.connected();
        }
        System.out.println("[NETWORK] Connected");
    }

    private static void alertDisconnected() {
        for (ConnectionListener listener : connectionListeners) {
            listener.disconnected();
        }
        System.out.println("[NETWORK] Disconnected");
    }

    /**
     * Get the root Subsocket.
     *
     * @return the root Subsocket.
     * @see RootSubsocket
     */
    public RootSubsocket getRootSubsocket() {
        return rootSocket;
    }

    @SuppressWarnings("SleepWhileInLoop")
    private void reconnect() {
        alertDisconnected();

        boolean retry = true;
        while (retry) {
            retry = false;
            try {
                if (input != null) {//close if open
                    input.close();
                }
                if (output != null) {//close if open
                    output.close();
                }
                if (tcpConnection != null) {//close if open
                    tcpConnection.close();
                }
            } catch (IOException ex) {
            }
            tcpConnection = null;
            while (tcpConnection == null) {//keeps trying to connect
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Connection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                try {
                    tcpConnection = new Socket(ADDRESS, PORT);
                } catch (IOException ex) {
                    tcpConnection = null;
                    System.out.println("[NETWORK] Failed to connect to server.");
                }
            }
            //setup the reader and writer objects
            try {
                output = new BufferedOutputStream(tcpConnection.getOutputStream());
                input = new BufferedInputStream(tcpConnection.getInputStream());
            } catch (IOException ex) {
                retry = true;
            }
        }

        alertConnected();
    }

    synchronized byte readByte() throws ConnectionResetException {
        if (tcpConnection == null) {
            reconnect();
        }
        try {
            return (byte) (input.read());
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    void writeByte(byte b) {
        try {
            output.write(b);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    void writeBytes(byte[] toByteArray) {
        for (byte b : toByteArray) {
            writeByte(b);
        }
    }

    void flush() {
        if (tcpConnection == null) {
            reconnect();
        }
        try {
            output.flush();
        } catch (IOException ex) {
            reconnect();
        }
    }

    private int readSubsocketID() {
        switch (IDFactory.getBytesRequiredToTransmit()) {
            case 1:
                return (int) readByte();
            case 2:
                byte[] data = {readByte(), readByte()};
                return (int) (PrimitiveSerializer.bytesToShort(data));
            case 4:
                byte[] bytes = {readByte(), readByte(), readByte(), readByte()};
                return PrimitiveSerializer.bytesToInt(bytes);
        }
        return 0;
    }

    /**
     * This stuff should <b>never</b> be called outside of the Connection object
     * or the communications package.
     */
    private class PrivateStuff implements Runnable, MultiplexingListener, SubsocketListener {

        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public void run() {
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }

                flush();

                try {
                    while (input.available() > 0 || tcpConnection.getInputStream().available() > 0) {
                        int id = readSubsocketID();
                        byte[] length = {readByte(), readByte()};
                        int l = (int) (PrimitiveSerializer.bytesToChar(length));
                        byte[] data = new byte[(int) (PrimitiveSerializer.bytesToChar(length))];
                        for (int j = 0; j < data.length; j++) {
                            data[j] = readByte();
                        }
                        try {
                            String route = IDFactory.getRoute(id);
                            if ("control".equals(route)) {
                                controlSocket.pushData(data);
                            } else {
                                rootSocket.getSubsocket(route).pushData(data);
                            }
                        } catch (RouteException ex) {
                            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (ConnectionResetException ex) {
                } catch (IOException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                    reconnect();
                }
            }
        }

        @Override
        public void multiplexingEnabled(String route) {
            byte[] routeBytes = PrimitiveSerializer.toByteArray(route);
            byte[] data = new byte[routeBytes.length + 1];
            data[0] = 3; //Multiplex Enabled Code
            System.arraycopy(routeBytes, 0, data, 1, routeBytes.length);
            controlSocket.sendData(data);
        }

        @Override
        public void multiplexingDisabled(String route) {
            byte[] routeBytes = PrimitiveSerializer.toByteArray(route);
            byte[] data = new byte[routeBytes.length + 1];
            data[0] = 4; //Multiplex Disabled Code
            System.arraycopy(routeBytes, 0, data, 1, routeBytes.length);
            controlSocket.sendData(data);
        }

        @Override
        public void routeAdded(String route) {
            byte[] routeBytes = PrimitiveSerializer.toByteArray(route);
            byte[] data = new byte[routeBytes.length + 1];
            data[0] = 1; // New Route Code
            System.arraycopy(routeBytes, 0, data, 1, routeBytes.length);
            controlSocket.sendData(data);
        }

        @Override
        public void routeRemoved(String route) {
            byte[] routeBytes = PrimitiveSerializer.toByteArray(route);
            byte[] data = new byte[routeBytes.length + 1];
            data[0] = 2; // Route Removed Code
            System.arraycopy(routeBytes, 0, data, 1, routeBytes.length);
            controlSocket.sendData(data);
        }

        @Override
        public void incomingData(byte[] data, Subsocket subsocket) {
            try {
                byte[] routeBytes = new byte[data.length - 1];
                System.arraycopy(data, 1, routeBytes, 0, data.length - 1);
                String route = PrimitiveSerializer.bytesToString(routeBytes);
                switch (data[0]) {
                    case 1:
                        rootSocket.createNewRoute(route, false);
                        break;
                    case 2:
                        rootSocket.destroyRoute(route, false);
                        break;
                    case 3:
                        rootSocket.getSubsocket(route).enableMultiplexing(false);
                        break;
                    case 4:
                        rootSocket.getSubsocket(route).disableMultiplexing(false);
                        break;
                }
            } catch (NotMultiplexedException | InvalidRouteException | RootRouteException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

/**
 * Differences between this and the client side:
 *
 * CONSTRUCTOR:
 *
 * private Connection() { idFactory = new IDFactory(this); rootSocket = new
 * RootSubsocket("root", this); controlSocket = new ControlSubsocket("control",
 * this); setupListeners(); start(); }
 *
 *
 *
 * RECONNECT():
 *
 * @SuppressWarnings("SleepWhileInLoop") private void reconnect() {
 * alertDisconnected();
 *
 * boolean retry = true; while (retry) { retry = false; try { if (input != null)
 * {//close if open input.close(); } if (output != null) {//close if open
 * output.close(); } if (tcpConnection != null) {//close if open
 * tcpConnection.close(); } } catch (IOException ex) { } tcpConnection = null;
 * while (tcpConnection == null) {//keeps trying to connect try {
 * Thread.sleep(200); } catch (InterruptedException ex) {
 * Logger.getLogger(Connection.class.getName()).log(java.util.logging.Level.SEVERE,
 * null, ex); } try { tcpConnection = new Socket("localhost", 1180); } catch
 * (IOException ex) { System.out.println("couldn't connect"); } } //setup the
 * reader and writer objects try { output = new
 * BufferedOutputStream(tcpConnection.getOutputStream()); input = new
 * BufferedInputStream(tcpConnection.getInputStream()); } catch (IOException ex)
 * { retry = true; } }
 *
 * alertConnected(); } *
 */
