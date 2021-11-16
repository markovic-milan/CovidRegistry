package org.unibl.etf.mdp.soap.server;

public class TokenServerProxy implements org.unibl.etf.mdp.soap.server.TokenServer {
  private String _endpoint = null;
  private org.unibl.etf.mdp.soap.server.TokenServer tokenServer = null;
  
  public TokenServerProxy() {
    _initTokenServerProxy();
  }
  
  public TokenServerProxy(String endpoint) {
    _endpoint = endpoint;
    _initTokenServerProxy();
  }
  
  private void _initTokenServerProxy() {
    try {
      tokenServer = (new org.unibl.etf.mdp.soap.server.TokenServerServiceLocator()).getTokenServer();
      if (tokenServer != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)tokenServer)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)tokenServer)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (tokenServer != null)
      ((javax.xml.rpc.Stub)tokenServer)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.unibl.etf.mdp.soap.server.TokenServer getTokenServer() {
    if (tokenServer == null)
      _initTokenServerProxy();
    return tokenServer;
  }
  
  public boolean exists(java.lang.String subject) throws java.rmi.RemoteException{
    if (tokenServer == null)
      _initTokenServerProxy();
    return tokenServer.exists(subject);
  }
  
  public org.unibl.etf.mdp.soap.server.Token getToken(java.lang.String params) throws java.rmi.RemoteException{
    if (tokenServer == null)
      _initTokenServerProxy();
    return tokenServer.getToken(params);
  }
  
  public void clearRedis() throws java.rmi.RemoteException{
    if (tokenServer == null)
      _initTokenServerProxy();
    tokenServer.clearRedis();
  }
  
  public org.unibl.etf.mdp.soap.server.Token[] getAktivne() throws java.rmi.RemoteException{
    if (tokenServer == null)
      _initTokenServerProxy();
    return tokenServer.getAktivne();
  }
  
  public org.unibl.etf.mdp.soap.server.Token[] getNeaktivne() throws java.rmi.RemoteException{
    if (tokenServer == null)
      _initTokenServerProxy();
    return tokenServer.getNeaktivne();
  }
  
  public void odjavaIzRegistra(java.lang.String username) throws java.rmi.RemoteException{
    if (tokenServer == null)
      _initTokenServerProxy();
    tokenServer.odjavaIzRegistra(username);
  }
  
  public boolean verifyToken(org.unibl.etf.mdp.soap.server.Token token) throws java.rmi.RemoteException{
    if (tokenServer == null)
      _initTokenServerProxy();
    return tokenServer.verifyToken(token);
  }
  
  public void deactivateToken(java.lang.String subject) throws java.rmi.RemoteException{
    if (tokenServer == null)
      _initTokenServerProxy();
    tokenServer.deactivateToken(subject);
  }
  
  
}