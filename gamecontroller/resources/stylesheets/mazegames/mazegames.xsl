<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Bomberman, Mummymaze, Pacman, Ghostmaze, Wargame, etc.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	<xsl:import href="../generic/sitespecific.xsl"/>

	<xsl:template name="print_state">
		
		<style type="text/css" media="all">
			div.maze {
				position: relative;
			}
			div.maze div.bomb3 {
				width:8px;
				height:8px;
				background-color:#99FF66;
			}
			div.maze div.bomb2 {
				width:8px;
				height:8px;
				background-color:#99FF66;
			}
			div.maze div.bomb1 {
				width:8px;
				height:8px;
				background-color:#FFCC00;
			}
			div.maze div.bomb0 {
				width:8px;
				height:8px;
				background-color:#FF3300;
			}
			div.maze div.pellet {
				width:8px;
				height:8px;
				background-color:#99FF66;
			}
			div.maze div.horizontalWall {
				position:absolute;
				width:44px;
				height:4px;
				background-color: #666666;
			}
			div.maze div.verticalWall {
				position:absolute;
				width:4px;
				height:44px;
				background-color: #666666;
			}
		</style>
		
		<div class="maze">
			
			<!-- Draw board -->
			<xsl:call-template name="chess_board">
				<xsl:with-param name="Width">8</xsl:with-param>
				<xsl:with-param name="Height">8</xsl:with-param>
				<xsl:with-param name="checkered">alldark</xsl:with-param>
				<xsl:with-param name="CellFluentName">LOCATION</xsl:with-param>
				<xsl:with-param name="contentArgIdx">1</xsl:with-param>
				<xsl:with-param name="xArgIdx">2</xsl:with-param>
				<xsl:with-param name="yArgIdx">3</xsl:with-param>
				<xsl:with-param name="CellWidth">40</xsl:with-param>
				<xsl:with-param name="CellHeight">40</xsl:with-param>
				<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
				<xsl:with-param name="DefaultCell">no</xsl:with-param>
			</xsl:call-template>
			
			<!-- Draw horizontal walls -->
			<xsl:for-each select="fact[prop-f='BLOCKEDNORTH' or (prop-f='WALL' and arg[3]='NORTH')]">
				<div class="horizontalWall">
					<xsl:attribute name="style">
						left: <xsl:value-of select="-2 + (./arg[1]-1)*40"/>px;
						top: <xsl:value-of select="-2 + (8-./arg[2])*40"/>px;
					</xsl:attribute>
				</div>
			</xsl:for-each>

			<!-- Draw vertical walls -->
			<xsl:for-each select="fact[prop-f='BLOCKEDEAST' or (prop-f='WALL' and arg[3]='EAST')]">
				<div class="verticalWall">
					<xsl:attribute name="style">
						left: <xsl:value-of select="-2 + (./arg[1])*40"/>px;
						top: <xsl:value-of select="-2 + (8-./arg[2])*40"/>px;
					</xsl:attribute>
				</div>
			</xsl:for-each>
			
		</div>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent">LOCATION</xsl:with-param>
			<xsl:with-param name="excludeFluent2">BLOCKEDNORTH</xsl:with-param>
			<xsl:with-param name="excludeFluent3">BLOCKEDEAST</xsl:with-param>
			<xsl:with-param name="excludeFluent4">WALL</xsl:with-param>
		</xsl:call-template>
		
	</xsl:template>


	<xsl:template name="make_cell">
		<xsl:param name="col"/>
		<xsl:param name="row"/>
		<xsl:param name="defaultClass"/>
		<div>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="fact[prop-f='LOCATION' and (arg[1]='FIRE' or arg[1]='SLIME' or arg[1]='BLASTZONE') and arg[2]=$col and arg[3]=$row]">cellLight</xsl:when>
					<xsl:otherwise><xsl:value-of select="$defaultClass"/></xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</div>
	</xsl:template>
		
	<xsl:template name="make_cell_content">
		<xsl:param name="xArg"/>
		<xsl:param name="yArg"/>
		<xsl:param name="content"/>
		<xsl:param name="piece"/>
		<xsl:param name="background"/>
		<xsl:param name="alt"/>

		<xsl:choose>
			<xsl:when test="$content='BOMB3'">
				<div class="bomb3"/>
			</xsl:when>
			<xsl:when test="$content='BOMB2'">
				<div class="bomb2"/>
			</xsl:when>
			<xsl:when test="$content='BOMB1'">
				<div class="bomb1"/>
			</xsl:when>
			<xsl:when test="$content='BOMB0'">
				<div class="bomb0"/>
			</xsl:when>
			<xsl:when test="$content='PELLET'">
				<div class="pellet"/>
			</xsl:when>
			<xsl:when test="$content='FIRE'"/>
			<xsl:when test="$content='SLIME'"/>
			<xsl:when test="$content='BLASTZONE'"/>
			<xsl:otherwise>
				<xsl:variable name="imgName">
					<xsl:choose>
						<xsl:when test="$content='GHOST'">blinky</xsl:when>
						<xsl:when test="substring($content,1,7)='SOLDIER'">explorer</xsl:when>
						<xsl:when test="substring($content,1,9)='TERRORIST'">bomberman</xsl:when>
						<xsl:when test="substring($content,1,3)='GUN' or substring($content,1,7)='GRENADE' or substring($content,1,6)='MEDKIT'">box</xsl:when>
						<xsl:otherwise><xsl:value-of select="translate($content, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<img>
					<xsl:attribute name="src"><xsl:value-of select="concat($stylesheetURL, '/mazegames/', $imgName, '.png')"/></xsl:attribute>
					<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
					<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
				</img>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

</xsl:stylesheet>
