package net.etfbl.api;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.rpc.ServiceException;

import org.unibl.etf.mdp.soap.server.Token;

@Path("/tokeni")
public class APIServis {
	private TokenService servis = TokenService.getInstance();
	public static HashMap<String, String> potencijalnoZarazeni = new HashMap<String, String>();
	public static ArrayList<String> zarazeni = new ArrayList<String>();
	private static ArrayList<String> blokirani = new ArrayList<>();;
	private static boolean started = false;

	public APIServis() {
		if (!started) {
			started = true;
			RedisService.startStatusCheckThread();
		}
	}

	@GET
	@Path("/potencijalni")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<String> getPotencijalni() throws RemoteException, ServiceException {
		return new ArrayList<String>(potencijalnoZarazeni.keySet());
	}

	@GET
	@Path("/active")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Token> getActive() throws RemoteException, ServiceException {
		return servis.getAktivne();
	}

	@GET
	@Path("/{user}/time")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<String> getTimes(@PathParam("user") String user) throws RemoteException, ServiceException {
		return RedisService.times(user);
	}

	@POST
	@Path("/{user}/map/{days}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMapa(Token token, @PathParam("user") String user, @PathParam("days") int days)
			throws RemoteException, ServiceException {
		if (servis.verifyToken(token) && !blokirani.contains(user)) {
			return Response.status(200).entity(RedisService.getMap(user, days)).build();
		} else {
			return Response.status(404).build();
		}
	}

	@POST
	@Path("/map/{user}/{mapa}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setMap(Token token, @PathParam("user") String user, @PathParam("mapa") String mapa)
			throws RemoteException, ServiceException {
		if (servis.verifyToken(token) && !blokirani.contains(user)) {
			System.out.println(user + " " + mapa);
			RedisService.setMap(user, mapa);
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}

	@POST
	@Path("/{username}/odjava")
	@Produces(MediaType.APPLICATION_JSON)
	public Response odjavaIzRegistra(Token token, @PathParam("username") String username)
			throws RemoteException, ServiceException {
		if (servis.verifyToken(token) && !blokirani.contains(username)) {
			RedisService.odjava(username);
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}

	// provjera da li je zarazen
	@POST
	@Path("/{username}/status")
	public Response getStatus(Token token, @PathParam("username") String username)
			throws RemoteException, ServiceException {
		if (servis.verifyToken(token) && !blokirani.contains(username)) {
			switch (RedisService.check(username)) {
			case 1:
				return Response.status(202).entity("Obavjestenje o zarazi").build();
			case 2:
				return Response.status(201).entity(RedisService.sendPotZarazeniPicture(username)).build();
			case 0:
				return Response.status(200).build();
			}
		}
		return Response.status(404).build();
	}

	@POST
	@Path("/{username}/time/{vrijeme}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setTime(Token token, @PathParam("username") String username, @PathParam("vrijeme") String vrijeme)
			throws RemoteException, ServiceException {
		if (servis.verifyToken(token) && RedisService.setTime(username, vrijeme) && !blokirani.contains(username)) {
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}

	@PUT
	@Path("/{username}/potencijalno")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response oznaciPotencijalnoZarazen(@PathParam("username") String username)
			throws RemoteException, ServiceException {
		RedisService.potencijalnoZarazen(username);
		return Response.status(200).build();
	}

	@PUT
	@Path("/{username}/zarazen")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response oznaciZarazen(@PathParam("username") String username) throws RemoteException, ServiceException {
		RedisService.zarazen(username);
		return Response.status(200).build();
	}

	@PUT
	@Path("/{username}/nije")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response oznaciDaNijeZarazen(@PathParam("username") String username)
			throws RemoteException, ServiceException {
		RedisService.nijeZarazen(username);
		return Response.status(200).build();
	}

	@PUT
	@Path("/{username}/block")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response blokirajKorisnika(@PathParam("username") String username) throws RemoteException, ServiceException {
		if (blokirani.add(username)) {
			return Response.status(200).build();
		}
		return Response.status(404).build();
	}
	
	@PUT
	@Path("/{username}/map")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserMap(@PathParam("username") String username) throws RemoteException, ServiceException {
		if (servis.exists(username)) {
			return Response.status(200).entity(RedisService.getMap(username, 0)).build();
		} else {
			return Response.status(404).build();
		}
	}
}