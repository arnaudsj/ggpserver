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
				<!--border: 1px solid #666666;-->
				float: left;
				text-align: center;
				vertical-align: middle;
			}
			div.board{
				width: <xsl:value-of select="$Width*40"/>px;
				height: <xsl:value-of select="$Height*40"/>px;
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
					<xsl:with-param name="row" select="arg[2]"/>
					<xsl:with-param name="col" select="arg[1]"/>
					<xsl:with-param name="content" select="arg[3]"/>
					<xsl:with-param name="Width" select="$Width"/>
					<xsl:with-param name="Height" select="$Height"/>
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
		<xsl:param name="Width"/>
		<xsl:param name="Height"/>
		
		<xsl:variable name="letters" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
		<xsl:variable name="x" select="number(translate($col, $letters, ''))"/>
		<xsl:variable name="y" select="number(translate($row, $letters, ''))"/>
		
		<div class="cell">
			<xsl:variable name="imageName">
				<xsl:choose>
					<xsl:when test="$content='WALL'">bricks</xsl:when>
					<xsl:when test="$content='POINT' and not(../fact[prop-f='POS' and arg[1]=$col and arg[2]=$row])">frog</xsl:when>
					<xsl:when test="$content='EXIT' and not(../fact[prop-f='POS' and arg[1]=$col and arg[2]=$row])">exit</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="xw" select="($x - 2 + $Width) mod $Width + 1"/>
						<xsl:variable name="xe" select="$x mod $Width + 1"/>
						<xsl:variable name="yn" select="($y - 2 + $Height) mod $Height + 1"/>
						<xsl:variable name="ys" select="$y mod $Height + 1"/>
						<xsl:variable name="comefrom">
							<xsl:choose>
								<xsl:when test="../fact[prop-f='CELL' and number(translate(arg[1], $letters, ''))=$xe and arg[2]=$row]/arg[3]='MOV_LEFT'">e</xsl:when>
								<xsl:when test="../fact[prop-f='CELL' and number(translate(arg[1], $letters, ''))=$xw and arg[2]=$row]/arg[3]='MOV_RIGHT'">w</xsl:when>
								<xsl:when test="../fact[prop-f='CELL' and arg[1]=$col and number(translate(arg[2], $letters, ''))=$yn]/arg[3]='MOV_DOWN'">n</xsl:when>
								<xsl:when test="../fact[prop-f='CELL' and arg[1]=$col and number(translate(arg[2], $letters, ''))=$ys]/arg[3]='MOV_UP'">s</xsl:when>
								<xsl:otherwise/>
							</xsl:choose>
						</xsl:variable>
						<xsl:variable name="goto">
							<xsl:choose>
								<xsl:when test="../fact[prop-f='POS' and arg[1]=$col and arg[2]=$row]">head</xsl:when>
								<xsl:when test="$content='MOV_UP'">n</xsl:when>
								<xsl:when test="$content='MOV_DOWN'">s</xsl:when>
								<xsl:when test="$content='MOV_LEFT'">w</xsl:when>
								<xsl:when test="$content='MOV_RIGHT'">e</xsl:when>
								<xsl:otherwise/>
							</xsl:choose>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="$goto=''"/>
							<xsl:when test="$goto='head' and $comefrom=''">snakehead_s</xsl:when>
							<xsl:when test="$goto='head'">snakehead_<xsl:value-of select="$comefrom"/></xsl:when>
							<xsl:when test="$comefrom='s' and $goto='s'">snake_ns</xsl:when>
							<xsl:when test="$goto='n'">snake_<xsl:value-of select="$goto"/><xsl:value-of select="$comefrom"/></xsl:when>
							<xsl:when test="$comefrom='n'">snake_<xsl:value-of select="$comefrom"/><xsl:value-of select="$goto"/></xsl:when>
							<xsl:when test="$goto='s'">snake_<xsl:value-of select="$goto"/><xsl:value-of select="$comefrom"/></xsl:when>
							<xsl:when test="$comefrom='s'">snake_<xsl:value-of select="$comefrom"/><xsl:value-of select="$goto"/></xsl:when>
							<xsl:when test="$goto='w'">snake_<xsl:value-of select="$goto"/><xsl:value-of select="$comefrom"/></xsl:when>
							<xsl:when test="$comefrom='w'">snake_<xsl:value-of select="$comefrom"/><xsl:value-of select="$goto"/></xsl:when>
							<xsl:otherwise>snake_<xsl:value-of select="$comefrom"/><xsl:value-of select="$goto"/></xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:if test="$imageName!=''">
				<img>
					<xsl:attribute name="src"><xsl:value-of select="concat($stylesheetURL, '/snake/', $imageName, '.png')"/></xsl:attribute>
				</img>
			</xsl:if>
		</div>
	</xsl:template>

</xsl:stylesheet>
