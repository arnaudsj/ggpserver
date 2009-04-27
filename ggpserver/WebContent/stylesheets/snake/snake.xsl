<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Snake
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>

	<xsl:template name="print_state">

		<xsl:variable name="ARow">
			<xsl:value-of select="fact[prop-f='CELL']/arg[2]"/>
		</xsl:variable>
		<xsl:variable name="Width" select="count(fact[prop-f='CELL' and arg[2]=$ARow])"/>
		<xsl:variable name="ACol">
			<xsl:value-of select="fact[prop-f='CELL']/arg[1]"/>
		</xsl:variable>
		<xsl:variable name="Height" select="count(fact[prop-f='CELL' and arg[1]=$ACol])"/>

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
			div.board{
				width: <xsl:value-of select="$Width*42"/>px;
				height: <xsl:value-of select="$Height*42"/>px;
				background: #ffcc99;
				border: 2px solid #000000;
				color: #aaaaaa;
			}
			div.count{
			}
		</style>

		<!-- Display the state of the game -->

		<div class="board">
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
		<p>
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
		</p>

	</xsl:template>

	<xsl:template name="makecell">
		<xsl:param name="row"/>
		<xsl:param name="col"/>
		<xsl:param name="content"/>
		<div class="cell">

			<!--<xsl:attribute name="id"><xsl:value-of select="$col"/><xsl:value-of select="$row"/></xsl:attribute>-->
			<xsl:variable name="imageName">
				<xsl:choose>
					<xsl:when test="../fact[prop-f='POS' and arg[1]=$col and arg[2]=$row]">snake.gif</xsl:when>
					<xsl:when test="$content='WALL'">bricks.gif</xsl:when>
					<xsl:when test="$content='MOV_UP'">uparrow.png</xsl:when>
					<xsl:when test="$content='MOV_DOWN'">downarrow.png</xsl:when>
					<xsl:when test="$content='MOV_LEFT'">leftarrow.png</xsl:when>
					<xsl:when test="$content='MOV_RIGHT'">rightarrow.png</xsl:when>
					<xsl:when test="$content='POINT'">frog.gif</xsl:when>
					<xsl:when test="$content='EXIT'">exit.png</xsl:when>
					<xsl:otherwise></xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:if test="$imageName!=''">
				<img>
					<xsl:attribute name="src"><xsl:value-of select="concat($stylesheetURL, '/snake/', $imageName)"/></xsl:attribute>
				</img>
			</xsl:if>
		</div>
	</xsl:template>

</xsl:stylesheet>
