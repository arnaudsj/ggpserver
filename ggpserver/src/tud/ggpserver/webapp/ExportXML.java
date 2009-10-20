/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.webapp;

import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tud.ggpserver.util.StateXMLExporter;

public class ExportXML extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4236703577700881151L;
	private static final Logger logger = Logger.getLogger(ExportXML.class.getName());
	
	public void init() throws ServletException {
	}

	//Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		try {
			String tournamentID = request.getParameter("tournamentID");
			String matchID = request.getParameter("matchID");
			String zipName = null;
			if(tournamentID!=null){
				zipName = tournamentID;
			}else{
				zipName = matchID;
			}
	
			if(tournamentID!=null || matchID!=null){
//				// create a temporary file
//				File temp = File.createTempFile( "temp_"+zipName+"_", ".zip.tmp", new File(System.getProperty("java.io.tmpdir")));
//				temp.deleteOnExit();
	
				response.setContentType("application/x-zip");
				response.addHeader("Content-Disposition", "attachment; filename=\""+zipName+".zip\";");

				try {
					// write zip file
					ZipOutputStream zip = new ZipOutputStream(response.getOutputStream());
					if(tournamentID != null) {
						StateXMLExporter.exportTournament(tournamentID, zip);
					}else if (matchID != null) {
						StateXMLExporter.exportMatch(matchID, zip);
					}
					zip.close();
				} catch (SocketException e) {
					logger.warning("exception while writing zip: " + e);
				}
				
//				// send zip file as response
//				response.setContentLength((int)temp.length());
//				OutputStream os=response.getOutputStream();
//				BufferedInputStream is=new BufferedInputStream(new FileInputStream(temp));
//				int len=0;
//				byte[] buf=new byte[4096];
//				while((len=is.read(buf, 0, 4096)) != -1) {
//					os.write(buf, 0, len);
//				}
//				os.flush();
//				is.close();
//				// delete temp file
//				temp.delete();
			}else{
				response.setContentType("text/html");
				response.getWriter().print("I don't know what to export!");
				response.flushBuffer();
			}
		} catch (SQLException e) {
			response.setContentType("text/html");
			logger.severe("database problem:" + e);
			response.getWriter().print("");
			response.flushBuffer();
		}
	}

	//Process the HTTP Post request
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	//Process the HTTP Put request
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	//Clean up resources
	public void destroy() {
	}

}