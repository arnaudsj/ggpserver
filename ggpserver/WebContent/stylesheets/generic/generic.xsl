<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	generic style sheet (just prints the fluents)
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="title.xsl"/>
	<xsl:import href="header.xsl"/>
	<xsl:import href="history.xsl"/>
	<xsl:import href="playerInfo.xsl"/>
	<xsl:import href="playClock.xsl"/>
	<xsl:import href="state.xsl"/>

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
					<xsl:with-param name="xPos">270px</xsl:with-param>
					<xsl:with-param name="yPos">110px</xsl:with-param>
				</xsl:call-template>				

				<xsl:call-template name="playerInfo">
					<xsl:with-param name="xPos">270px</xsl:with-param>
					<xsl:with-param name="yPos">160px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="history">
					<xsl:with-param name="xPos">270px</xsl:with-param>
					<xsl:with-param name="yPos">260px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="state">
					<xsl:with-param name="xPos">10px</xsl:with-param>
					<xsl:with-param name="yPos">110px</xsl:with-param>
				</xsl:call-template>
			</body>
		
		</html>
	</xsl:template>
</xsl:stylesheet>
