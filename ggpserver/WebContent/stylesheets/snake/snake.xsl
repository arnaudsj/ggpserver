<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Snake
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
					<xsl:with-param name="xPos">420px</xsl:with-param>
					<xsl:with-param name="yPos">110px</xsl:with-param>
				</xsl:call-template>				

				<xsl:call-template name="playerInfo">
					<xsl:with-param name="xPos">420px</xsl:with-param>
					<xsl:with-param name="yPos">160px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="history">
					<xsl:with-param name="xPos">420px</xsl:with-param>
					<xsl:with-param name="yPos">230px</xsl:with-param>
				</xsl:call-template>

				<style type="text/css" media="all">
					div.cell{
						width: 40px;
						height: 40px;
						background: #ffffcc;
						border: 1px solid #666666;
						float: left;
						text-align: center;
						vertical-align: middle;
					}
					div.bcell{
						width: 40px;
						height: 40px;
						background: #cccccc;
						border: 1px solid #666666;
						float: left;
					}
					div.rcell{
						width: 40px;
						height: 40px;
						background: #ffcccc;
						border: 1px solid #666666;
						float: left;
					}
					#board{
						position: absolute;
						top: 100px;
						width: 378px;
						height: 294px;
						left: 10px;
						background: #ffcc99;
						border: 2px solid #000000;
						color: #aaaaaa;
					}
					#count{
						position: absolute;
						top: 410px;
						left: 10px;
					}
				</style>

				<!-- Display the state of the game -->
				<xsl:for-each select="match/state">
					<xsl:call-template name="state"/>
				</xsl:for-each>

			</body>
		
		</html>
	</xsl:template>

	<xsl:template name="state">
		<div id="board">
			<xsl:for-each select="fact[prop-f='CELL']">
				<xsl:sort select="arg[2]" order="ascending"/>
				<xsl:sort select="arg[1]"/>
				<xsl:call-template name="makecell">
					<xsl:with-param name="row" select="arg[2]"></xsl:with-param>
					<xsl:with-param name="col" select="arg[1]"></xsl:with-param>
					<xsl:with-param name="content" select="arg[3]"></xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
		</div>
		<div id="count">
			<xsl:for-each select="fact[prop-f='POINTS']">
				frogs eaten: <xsl:value-of select="arg[1]"/>
			</xsl:for-each>
			<xsl:variable name="posy" select="fact[prop-f='POS']/arg[1]"/>
			<xsl:variable name="posx" select="fact[prop-f='POS']/arg[2]"/>
			<xsl:for-each select="fact[prop-f='CELL' and ./arg[3]='POINT']">
				<xsl:if test="$posy=arg[1] and $posx=arg[2]">
					+ 1
				</xsl:if>
			
			</xsl:for-each>
			
		</div>
	</xsl:template>
	
	<xsl:template name="makecell">
		<xsl:param name="row"/>
		<xsl:param name="col"/>
		<xsl:param name="content"/>
		<div>
			<xsl:attribute name="class">cell</xsl:attribute>
			
			<xsl:attribute name="id"><xsl:value-of select="$col"/><xsl:value-of select="$row"/></xsl:attribute>
			<xsl:choose>
				<xsl:when test="../fact[prop-f='POS' and arg[1]=$col and arg[2]=$row]">
					<img src="../stylesheets/snake/snake.gif"/>
				</xsl:when>
				<xsl:when test="$content='WALL'">
					<img src="../stylesheets/snake/bricks.gif"/>
				</xsl:when>
				<xsl:when test="$content='MOV_UP'">
					<img src="../stylesheets/snake/uparrow.png"/>
				</xsl:when>
				<xsl:when test="$content='MOV_DOWN'">
					<img src="../stylesheets/snake/downarrow.png"/>
				</xsl:when>
				<xsl:when test="$content='MOV_LEFT'">
					<img src="../stylesheets/snake/leftarrow.png"/>
				</xsl:when>
				<xsl:when test="$content='MOV_RIGHT'">
					<img src="../stylesheets/snake/rightarrow.png"/>
				</xsl:when>
				<xsl:when test="$content='POINT'">
					<img src="../stylesheets/snake/frog.gif"/>
				</xsl:when>
				<xsl:when test="$content='EXIT'">
					<img src="../stylesheets/snake/exit.png"/>
				</xsl:when>
				<!--<xsl:otherwise>
					<xsl:value-of select="$col"/>_<xsl:value-of select="$row"/>
				</xsl:otherwise>-->
			</xsl:choose>
		</div>
	</xsl:template>

</xsl:stylesheet>
