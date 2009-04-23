<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Pentago
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
					<xsl:with-param name="xPos">320px</xsl:with-param>
					<xsl:with-param name="yPos">110px</xsl:with-param>
				</xsl:call-template>				

				<xsl:call-template name="playerInfo">
					<xsl:with-param name="xPos">320px</xsl:with-param>
					<xsl:with-param name="yPos">160px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="history">
					<xsl:with-param name="xPos">320px</xsl:with-param>
					<xsl:with-param name="yPos">230px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="chess_board">
					<xsl:with-param name="xPos">10</xsl:with-param>
					<xsl:with-param name="yPos">110</xsl:with-param>
					<xsl:with-param name="Width">6</xsl:with-param>
					<xsl:with-param name="Height">6</xsl:with-param>
					<xsl:with-param name="checkered">no</xsl:with-param>
				</xsl:call-template>

				<style type="text/css" media="all">
					div.pentago_cell
					{
						position: absolute;
						width:    44px;
						height:   44px;
					}
					div.pentago_vertical_line
					{
						position: absolute;
						width:    2px;
						height:   288px;
						left:     153px;
						top:      110px;
						background-color: black;
					}
					div.pentago_horizontal_line
					{
						position: absolute;
						width:    288px;
						height:   2px;
						left:     10px;
						top:      253px;
						background-color: black;
					}
				</style>

				<div class="pentago_vertical_line"/>
				<div class="pentago_horizontal_line"/>

				<!-- Draw Marks -->
				<xsl:for-each select="/match/state/fact[prop-f='CELLHOLDS']">

				    <xsl:variable name="quad" select="./arg[1]"/>
					<xsl:variable name="x"    select="48 * (./arg[2]-1) + 12"/>
					<xsl:variable name="y"    select="48 * (3-./arg[3]) + 112"/>

					<xsl:variable name="xPosCell">
						<xsl:choose>
							<xsl:when test="$quad='1' or $quad='4'"><xsl:value-of select="$x+144"/></xsl:when>
							<xsl:otherwise><xsl:value-of select="$x"/></xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					
					<xsl:variable name="yPosCell">
						<xsl:choose>
							<xsl:when test="$quad='3' or $quad='4'"><xsl:value-of select="$y+144"/></xsl:when>
							<xsl:otherwise><xsl:value-of select="$y"/></xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					
					<div class="pentago_cell">
						<xsl:attribute name="style">
							left: <xsl:value-of select="$xPosCell"/>px;
							top:  <xsl:value-of select="$yPosCell"/>px;
						</xsl:attribute>
						
						<xsl:variable name="piece">
							<xsl:choose>
								<xsl:when test="./arg[4]='RED'">O2</xsl:when>
								<xsl:when test="./arg[4]='BLACK'">O0</xsl:when>
							</xsl:choose>
						</xsl:variable>
						
						<xsl:call-template name="make_chess_img">
							<xsl:with-param name="piece" select="$piece"/>
						</xsl:call-template>
					</div>
				</xsl:for-each>
				
			</body>
		
		</html>
	</xsl:template>
</xsl:stylesheet>