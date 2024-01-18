package com.example.androidtvlibrary.main.adapter.factory;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.DataSpec;
import com.example.androidtvlibrary.main.adapter.DataSpecTest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public final class UdpDataSource extends BaseDataSource {

    /**
     * Thrown when an error is encountered when trying to read from a {@link UdpDataSource}.
     */
    public static final class UdpDataSourceException extends IOException {

        public UdpDataSourceException(IOException cause) {
            super(cause);
        }

    }

    /**
     * The default maximum datagram packet size, in bytes.
     */
    public static final int DEFAULT_MAX_PACKET_SIZE = 2000;

    /** The default socket timeout, in milliseconds. */
    public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 8 * 1000;

    private final int socketTimeoutMillis;
    private final byte[] packetBuffer;
    private final DatagramPacket packet;

    @Nullable
    private Uri uri;
    @Nullable private DatagramSocket socket;
    @Nullable private MulticastSocket multicastSocket;
    @Nullable private InetAddress address;
    @Nullable private InetSocketAddress socketAddress;
    private boolean opened;

    private int packetRemaining;

    public UdpDataSource() {
        this(DEFAULT_MAX_PACKET_SIZE);
    }

    /**
     * Constructs a new instance.
     *
     * @param maxPacketSize The maximum datagram packet size, in bytes.
     */
    public UdpDataSource(int maxPacketSize) {
        this(maxPacketSize, DEFAULT_SOCKET_TIMEOUT_MILLIS);
    }

    /**
     * Constructs a new instance.
     *
     * @param maxPacketSize The maximum datagram packet size, in bytes.
     * @param socketTimeoutMillis The socket timeout in milliseconds. A timeout of zero is interpreted
     *     as an infinite timeout.
     */
    public UdpDataSource(int maxPacketSize, int socketTimeoutMillis) {
        super(/* isNetwork= */ true);
        this.socketTimeoutMillis = socketTimeoutMillis;
        packetBuffer = new byte[maxPacketSize];
        packet = new DatagramPacket(packetBuffer, 0, maxPacketSize);
    }

    @Override
    public long open(DataSpecTest dataSpec) throws UdpDataSourceException {
        uri = dataSpec.uri;
        String host = uri.getHost();
        int port = uri.getPort();
        transferInitializing(dataSpec);
        try {
            address = InetAddress.getByName(host);
            socketAddress = new InetSocketAddress(address, port);
            if (address.isMulticastAddress()) {
                multicastSocket = new MulticastSocket(socketAddress);
                multicastSocket.joinGroup(address);
                socket = multicastSocket;
            } else {
                socket = new DatagramSocket(socketAddress);
            }
        } catch (IOException e) {
            throw new UdpDataSourceException(e);
        }

        try {
            socket.setSoTimeout(socketTimeoutMillis);
        } catch (SocketException e) {
            throw new UdpDataSourceException(e);
        }

        opened = true;
        transferStarted(dataSpec);
        return C.LENGTH_UNSET;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws UdpDataSourceException {
        if (readLength == 0) {
            return 0;
        }

        if (packetRemaining == 0) {
            // We've read all of the data from the current packet. Get another.
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new UdpDataSourceException(e);
            }
            packetRemaining = packet.getLength();
            bytesTransferred(packetRemaining);
        }

        int packetOffset = packet.getLength() - packetRemaining;
        int bytesToRead = Math.min(packetRemaining, readLength);
        System.arraycopy(packetBuffer, packetOffset, buffer, offset, bytesToRead);
        packetRemaining -= bytesToRead;
        return bytesToRead;
    }

    @Override
    @Nullable
    public Uri getUri() {
        return uri;
    }

    @Override
    public void close() {
        uri = null;
        if (multicastSocket != null) {
            try {
                multicastSocket.leaveGroup(address);
            } catch (IOException e) {
                // Do nothing.
            }
            multicastSocket = null;
        }
        if (socket != null) {
            socket.close();
            socket = null;
        }
        address = null;
        socketAddress = null;
        packetRemaining = 0;
        if (opened) {
            opened = false;
            transferEnded();
        }
    }

}
