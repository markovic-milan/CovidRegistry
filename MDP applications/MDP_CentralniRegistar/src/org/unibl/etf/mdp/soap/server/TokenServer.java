/**
 * TokenServer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unibl.etf.mdp.soap.server;

public interface TokenServer extends java.rmi.Remote {
    public boolean exists(java.lang.String subject) throws java.rmi.RemoteException;
    public org.unibl.etf.mdp.soap.server.Token getToken(java.lang.String params) throws java.rmi.RemoteException;
    public void clearRedis() throws java.rmi.RemoteException;
    public org.unibl.etf.mdp.soap.server.Token[] getAktivne() throws java.rmi.RemoteException;
    public org.unibl.etf.mdp.soap.server.Token[] getNeaktivne() throws java.rmi.RemoteException;
    public void odjavaIzRegistra(java.lang.String username) throws java.rmi.RemoteException;
    public boolean verifyToken(org.unibl.etf.mdp.soap.server.Token token) throws java.rmi.RemoteException;
    public void deactivateToken(java.lang.String subject) throws java.rmi.RemoteException;
}
