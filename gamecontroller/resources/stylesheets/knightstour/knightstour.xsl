<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Knightstour
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
					<xsl:with-param name="xPos">320px</xsl:with-param>
					<xsl:with-param name="yPos">110px</xsl:with-param>
				</xsl:call-template>				

				<xsl:call-template name="playerInfo">
					<xsl:with-param name="xPos">320px</xsl:with-param>
					<xsl:with-param name="yPos">160px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="history">
					<xsl:with-param name="xPos">320px</xsl:with-param>
					<xsl:with-param name="yPos">200px</xsl:with-param>
				</xsl:call-template>

				<style type="text/css" media="all">
					#main
					{
						position: absolute;
						left:     10px;
						top:      110px;
						width:    300px;
						height:   250px;
					}					
					#cellL
					{
						width:  46px;
						height: 46px;
						float:	left;
						border: 2px solid #FFC;
						background-color: #CCCCCC;
					}
					#cellD
					{
						width:  46px;
						height: 46px;
						float:	left;
						border: 2px solid #FFC;
						background-color: #AAAAAA;
					}
					
				</style>

				<!-- Draw Board -->
				<div id="main">
					<div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/>
					<div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/>
					<div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/>
					<div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/>
					<div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/>
				</div>

				<!-- Draw Pieces -->
				<xsl:for-each select="/match/state/fact[prop-f='CELL']">
					<xsl:variable name="x" select="50 * (./arg[1]-1) + 15"/>
					<xsl:variable name="y" select="50 * (5-./arg[2]) + 115"/>
					
					<div>
						<xsl:attribute name="style">
							<xsl:value-of select="concat('position:absolute; left:', $x ,'px; top:', $y ,'px; width:50px; height:50px;')"/>
						</xsl:attribute>		

						<xsl:if test="./arg[3]='KNIGHT'">
							<img src="../../stylesheets/knightstour/wn.gif"/>
						</xsl:if>
						<xsl:if test="./arg[3]='HOLE'">
							<img src="../../stylesheets/knightstour/bn.gif"/>
						</xsl:if>
					</div>
				</xsl:for-each>

				<!-- Write move info -->
				<div style="position:absolute; top:370px; left:10px;">
					<span class="heading">Move Count: </span><span class="content"><xsl:value-of select="/match/state/fact[prop-f='MOVECOUNT']/arg[1]"/></span>
				</div>
				
			</body>
		
		</html>
	</xsl:template>
</xsl:stylesheet>