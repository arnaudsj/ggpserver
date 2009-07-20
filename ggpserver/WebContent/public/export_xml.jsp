<%@ page language="java" contentType="application/x-zip" pageEncoding="UTF-8"%><%@ page import="tud.ggpserver.util.StateXMLExporter" %><%@ page import="java.util.zip.ZipOutputStream" %><%@ page import="java.io.File" %><%@ page import="java.io.FileOutputStream" %><%@ page import="java.io.FileInputStream" %><%@ page import="java.io.BufferedInputStream" %><%@ page import="java.io.OutputStream" %><%
	String tournamentID = request.getParameter("tournamentID");
	String matchID = request.getParameter("matchID");
	String zipName = null;
	if(tournamentID!=null){
		zipName = tournamentID;
	}else{
		zipName = matchID;
	}
	if(zipName!=null){
	
		// create a temporary file
		File temp = File .createTempFile( "temp_"+zipName, ".zip.tmp", new File(System.getProperty("java.io.tmpdir")));
		temp.deleteOnExit();

		// write zip file
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(temp));
		if(tournamentID != null) {
			StateXMLExporter.exportTournament(tournamentID, zip);
		}else{
			StateXMLExporter.exportMatch(matchID, zip);
		}
		zip.close();
		
		// send zip file as response
		response.setContentType("application/x-zip");
		response.addHeader("Content-Disposition", "attachment; filename=\""+zipName+".zip\";");
		response.setContentLength((int)temp.length());
		OutputStream os=response.getOutputStream();
		BufferedInputStream is=new BufferedInputStream(new FileInputStream(temp));
		int len=0;
		byte[] buf=new byte[4096];
		while((len=is.read(buf, 0, 4096)) != -1) {
			os.write(buf, 0, len);
		}
		os.flush();
		is.close();
		// delete temp file
		temp.delete();
	}else{
		response.setContentType("text/html");
		response.getWriter().print("I don't know what to export!");
		response.flushBuffer();
	}
%>