<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	TicTacToe 3D
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
					<xsl:with-param name="xPos">330px</xsl:with-param>
					<xsl:with-param name="yPos">110px</xsl:with-param>
				</xsl:call-template>				

				<xsl:call-template name="playerInfo">
					<xsl:with-param name="xPos">330px</xsl:with-param>
					<xsl:with-param name="yPos">160px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="history">
					<xsl:with-param name="xPos">330px</xsl:with-param>
					<xsl:with-param name="yPos">330px</xsl:with-param>
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
						<xsl:for-each select="/match/state/fact[prop-f='CELL']">
							<xsl:sort select="arg[3]" order="descending"/> <!-- z  -->
							<xsl:sort select="arg[1]" order="descending"/> <!-- y  -->
							<xsl:sort select="arg[2]" order="ascending"/> <!-- x  -->
			
							<xsl:variable name="zCoord" select="arg[3]"/>
							<xsl:variable name="yCoord" select="arg[1]"/>
							<xsl:variable name="xCoord" select="arg[2]"/>
							<xsl:variable name="content" select="arg[4]"/>
			
							<xsl:variable name="yCell" select="(4 - $zCoord) * 120 + (4 - $yCoord) * 25"/>
							<xsl:variable name="xCell" select="($xCoord - 1) * 52 + ($yCoord - 1) * 25"/>
							
							
							<div>
								<xsl:attribute name="style">
									<xsl:value-of select="concat('position:absolute; left:', $xCell ,'px; top:', $yCell ,'px; height: 21px; width:39px; background-color: transparent')"/>
								</xsl:attribute>
								
								<img width="78px" height="42px">
									<xsl:attribute name="src">
										<xsl:text>../../stylesheets/tictactoe_3d/cell_</xsl:text>
										<xsl:choose>
											<xsl:when test="$content='X'">
												<xsl:text>blue</xsl:text>
											</xsl:when>
											<xsl:when test="$content='O'">
												<xsl:text>red</xsl:text>
											</xsl:when>
											<xsl:when test="$content='B'">
												<xsl:choose>
													<xsl:when test="
														../fact[prop-f='SELECTED']
														and
														(not (../fact[prop-f='SELECTED' and arg[1]='ROW']) or (../fact[prop-f='SELECTED' and arg[1]='ROW']/arg[2]=$yCoord))
														and
														(not (../fact[prop-f='SELECTED' and arg[1]='COLUMN']) or (../fact[prop-f='SELECTED' and arg[1]='COLUMN']/arg[2]=$xCoord))
														and
														(not (../fact[prop-f='SELECTED' and arg[1]='LEVEL']) or (../fact[prop-f='SELECTED' and arg[1]='LEVEL']/arg[2]=$zCoord))
														">
														<xsl:text>possible</xsl:text>
													</xsl:when>
													<xsl:otherwise>
														<xsl:text>blank</xsl:text>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:when>
										</xsl:choose>
										<xsl:text>.png</xsl:text>
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