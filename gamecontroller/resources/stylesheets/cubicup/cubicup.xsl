<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Cubicup
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/title.xsl"/>
	<xsl:import href="../generic/header.xsl"/>
	<xsl:import href="../generic/history.xsl"/>
	<xsl:import href="../generic/playerInfo.xsl"/>
	<xsl:import href="../generic/playClock.xsl"/>

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
					<xsl:with-param name="xPos">310px</xsl:with-param>
					<xsl:with-param name="yPos">110px</xsl:with-param>
				</xsl:call-template>				

				<xsl:call-template name="playerInfo">
					<xsl:with-param name="xPos">310px</xsl:with-param>
					<xsl:with-param name="yPos">160px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="history">
					<xsl:with-param name="xPos">310px</xsl:with-param>
					<xsl:with-param name="yPos">260px</xsl:with-param>
				</xsl:call-template>

				<style type="text/css" media="all">
					#main{
						position: absolute;
						left:     10px;
						top:      110px;
						width:    280px;
						height:   490px;
						padding: 0px;
					}

				</style>

				<xsl:for-each select="match/state">
					<div id="main">
						<xsl:for-each select="/match/state/fact[prop-f='CUBE']">
							<xsl:sort select="arg[1]" order="ascending"/>
							<xsl:sort select="arg[3]" order="ascending"/>
							<xsl:sort select="arg[2]" order="ascending"/>
			
							<xsl:variable name="zCoord" select="arg[3]"/>
							<xsl:variable name="yCoord" select="arg[1]"/>
							<xsl:variable name="xCoord" select="arg[2]"/>
			
							<xsl:variable name="yCell" select="$yCoord * 37 - $zCoord * 19"/>
							<xsl:variable name="xCell" select="($xCoord * 38 + (6 - $yCoord) * 19) - $zCoord*19"/>
							
							<div>
								<xsl:attribute name="style">
									<xsl:value-of select="concat('position:absolute; left:', $xCell ,'px; top:', $yCell ,'px; height: 57px; width:39px; background-color: transparent')"/>
								</xsl:attribute>
								
								<img width="39px" height="57px">
									<xsl:attribute name="src">
										<xsl:text>../../stylesheets/cubicup/</xsl:text>
										<xsl:choose>
											<xsl:when test="arg[4]='YELLOW'">
												<xsl:text>yellow</xsl:text>
											</xsl:when>
											<xsl:when test="arg[4]='RED'">
												<xsl:text>red</xsl:text>
											</xsl:when>
											<xsl:when test="arg[4]='BASE'">
												<xsl:text>gray</xsl:text>
											</xsl:when>
										</xsl:choose>
										<xsl:text>_cube.png</xsl:text>
									</xsl:attribute>
								</img>
							</div>
						
						</xsl:for-each>
					</div>
				</xsl:for-each>
			</body>
		
		</html>
	</xsl:template>
</xsl:stylesheet>