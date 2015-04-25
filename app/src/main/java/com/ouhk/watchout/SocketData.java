package com.ouhk.watchout;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by Jacky Li on 29/1/2015.
 */
public class SocketData implements Serializable {
    private static Socket socket;
    private static int ServerPort;
    private static String ServerIP;
    private static OutputStream OutStream ;
    private static InputStream InStream ;
    private static DataOutputStream DataOut;
    private static DataInputStream DataIn;
    private static int SocketTimeout;
    private InputStreamReader InStreamReader;
    private BufferedReader br;

    public BufferedReader getBr() {
        return br;
    }
    public void setBr(BufferedReader br) {
        this.br = br;
    }
    public InputStreamReader getInStreamReader() {
        return InStreamReader;
    }
    public void setInStreamReader(InputStreamReader inStreamReader) {
        InStreamReader = inStreamReader;
    }
    public static int getSocketTimeout() {
        return SocketTimeout;
    }
    public static void setSocketTimeout(int socketTimeout) {
        SocketTimeout = socketTimeout;
    }
    public static Socket getSocketObj(){
        return socket;
    }
    public static void setSocketObj(Socket _Socket){
        socket = _Socket;
    }
    public static int getServerPort(){
        return ServerPort;
    }
    public static void setServerPort(int _port){
        ServerPort = _port;
    }
    public static String getServerIP(){
        return ServerIP;
    }
    public static void setServerIP(String _IP){
        ServerIP = _IP;
    }
    public static OutputStream getOutStream(){
        return OutStream;
    }
    public static void setOutStream(OutputStream _OutStream){
        OutStream=_OutStream;
    }
    public static DataOutputStream getDataOut() {
        return DataOut;
    }
    public static void setDataOut(DataOutputStream _DataOut){
        DataOut = _DataOut;
    }
    public static InputStream getInStream(){
        return InStream;
    }
    public static void setInStream(InputStream _InStream){
        InStream=_InStream;
    }
    public static DataInputStream getDataIn() {
        return DataIn;
    }
    public static void setDataIn(DataInputStream _DataIn){
        DataIn = _DataIn;
    }
}
