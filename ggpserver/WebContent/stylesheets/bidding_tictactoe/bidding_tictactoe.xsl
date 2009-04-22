<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Bidding-TicTacToe
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
					<xsl:with-param name="xPos">170px</xsl:with-param>
					<xsl:with-param name="yPos">110px</xsl:with-param>
				</xsl:call-template>				

				<xsl:call-template name="playerInfo">
					<xsl:with-param name="xPos">170px</xsl:with-param>
					<xsl:with-param name="yPos">160px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="history">
					<xsl:with-param name="xPos">170px</xsl:with-param>
					<xsl:with-param name="yPos">230px</xsl:with-param>
				</xsl:call-template>

				<style type="text/css" media="all">
					#main
					{
						position: absolute;
						left:     10px;
						top:      110px;
						width:    150px;
						height:   150px;
						font: bold 40px fixed;
					}					
					#coins
					{
						position: absolute;
						left:     10px;
						top:      270px;
						width:    150px;
					}					
					#cell
					{
						width:  46px;
						height: 46px;
						float:	left;
						border: 2px solid #777777;
						background-color: #EEEEEE;
						text-align: center;
						<!-- vertical-align: middle;-->
						<!--font: bold 30px arial;-->
					}
				</style>

				<xsl:for-each select="match/state">
					<div id="main">
						<xsl:for-each select="fact[prop-f='CELL']">
							<xsl:sort select="arg[2]" order="ascending"/>
							<xsl:sort select="arg[1]"/>
							<div id="cell">
								<!--<p style="text-align: center;">-->
								<xsl:choose>
									<xsl:when test="arg[3]='X'">
										X
									</xsl:when>
									<xsl:when test="arg[3]='O'">
										O
									</xsl:when>
								</xsl:choose>
								<!--</p>-->
							</div>
						</xsl:for-each>
					</div>
					
					<div id="coins">
						<strong>Coins:</strong>
						<table border="1">
							<tr><th style="width:50px; background-color: #CCCCCC;">X</th><th style="width:50px; background-color: #CCCCCC;">O</th></tr>
							<tr>
								<xsl:for-each select="fact[prop-f='COINS']">
									<xsl:sort select="arg[1]" order="descending"/>
									<xsl:variable name="who" select="arg[1]"/>
									<td style="text-align:center;">
										<xsl:value-of select="arg[2]"/>
										<xsl:if test="../fact[prop-f='TIEBREAKER']/arg[1]=$who">
											*
										</xsl:if>
									</td>
								</xsl:for-each>
							</tr>
						</table>
					</div>
				</xsl:for-each>
			</body>
		
		</html>
	</xsl:template>
</xsl:stylesheet>