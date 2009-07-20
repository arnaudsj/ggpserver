<%@ page language="java" contentType="application/x-zip" pageEncoding="UTF-8"%><%@ page import="tud.ggpserver.util.StateXMLExporter" %><%@ page import="java.util.zip.ZipOutputStream" %><%
	String tournamentID = request.getParameter("tournamentID");
	String matchID = request.getParameter("matchID");
	String zipName = null;
	if(tournamentID!=null){
		zipName = tournamentID;
	}else{
		zipName = matchID;
	}
	if(zipName!=null){
		response.setContentType("application/x-zip");
		response.addHeader("Content-Disposition", "attachment; filename=\""+zipName+".zip\";");
		ZipOutputStream zip = new ZipOutputStream(response.getOutputStream());
		if(tournamentID != null) {
			StateXMLExporter.exportTournament(tournamentID, zip, "");
		}else{
			StateXMLExporter.exportMatch(matchID, zip, "");
		}
		zip.close();
	}else{
		response.setContentType("text/html");
		response.getWriter().print("I don't know what to export!");
	}
	response.flushBuffer();
%>