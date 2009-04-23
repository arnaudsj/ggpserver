<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	should work for most chess like games (chess, checkers, ...)
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/title.xsl"/>
	<xsl:import href="../generic/header.xsl"/>
	<xsl:import href="../generic/history.xsl"/>
	<xsl:import href="../generic/playerInfo.xsl"/>
	<xsl:import href="../generic/playClock.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>

	<xsl:template name="main" match="/">
		<html>
		
			<head>
				<xsl:call-template name="title"/>
			</head>	

			<body style="color: #111; background: #ffc;">

				<xsl:call-template name="header">	
					<xsl:with-param name="xPos">10px</xsl:with-param>
					<xsl:with-param name="yPos">10px</xsl:with-param>
				</xsl:call-template>	

				<xsl:call-template name="playClock">
					<xsl:with-param name="xPos">420px</xsl:with-param>
					<xsl:with-param name="yPos">110px</xsl:with-param>
				</xsl:call-template>				

				<xsl:call-template name="playerInfo">
					<xsl:with-param name="xPos">420px</xsl:with-param>
					<xsl:with-param name="yPos">160px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="history">
					<xsl:with-param name="xPos">420px</xsl:with-param>
					<xsl:with-param name="yPos">240px</xsl:with-param>
				</xsl:call-template>


				<!-- paint board -->
				
				<xsl:call-template name="chess_board">
					<xsl:with-param name="xPos">10</xsl:with-param>
					<xsl:with-param name="yPos">110</xsl:with-param>
					<xsl:with-param name="Width">8</xsl:with-param>
					<xsl:with-param name="Height">8</xsl:with-param>
				</xsl:call-template>
				
				<!-- TODO: add disclaimer, e.g.:
					images are from: http://commons.wikimedia.org/wiki/Category:Standard_chess_tiles
					and are freely redistributable under the <a href="http://www.gnu.org/copyleft/fdl.html">GNU Free Documentation License</a>.
				-->
				
			</body>
		
		</html>
	</xsl:template>
	
	
</xsl:stylesheet>