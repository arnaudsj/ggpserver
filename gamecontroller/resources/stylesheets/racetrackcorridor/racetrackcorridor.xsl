<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	for racetrackcorridor
	
	needs the following sees_xml rules:

	(<= (sees_xml random ?t) (sees_term ?t))
	(<= (sees_xml ?p ?t) (role ?p) (distinct ?p random) (sees_term ?t))
	
	; transform cell/4 into wlane/3 and blane/3 so it is identified as two boards
	(<= (sees_term (wlane ?y ?x ?c))
	  (true (cell wlane ?y ?x ?c))
	)
	(<= (sees_term (blane ?y ?x ?c))
	  (true (cell blane ?y ?x ?c))
	)
	(<= (sees_term ?f)
	  (true ?f)
	)
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	<xsl:import href="../generic/sitespecific.xsl"/>

	<xsl:template name="print_state">
		
		<style type="text/css" media="all">
			div.track {
				position: relative;
				float:left;
				width 154px;
			}
			div.track div.wall {
				position:absolute;
				width:96px;
				height:4px;
				background-color: #666666;
			}
		</style>
		<div style="width:318px; height:240px;">
			<xsl:call-template name="make_track">
				<xsl:with-param name="track" select="'WLANE'"/>
			</xsl:call-template>
			<div style="float:left; height:240px; width:2px; background-color: #666666; margin-left: 4px; margin-right: 4px;"></div>
			<xsl:call-template name="make_track">
				<xsl:with-param name="track" select="'BLANE'"/>
			</xsl:call-template>
		</div>
		<!-- show remaining fluents -->
		<p>
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent">CELL</xsl:with-param>
			<xsl:with-param name="excludeFluent2">WLANE</xsl:with-param>
			<xsl:with-param name="excludeFluent3">BLANE</xsl:with-param>
			<xsl:with-param name="excludeFluent4">WALL</xsl:with-param>
		</xsl:call-template>
		</p>
	</xsl:template>

	<xsl:template name="make_track">
		<xsl:param name="track"/>
	
		<div class="track">
			<!--			 Draw board -->
			<xsl:call-template name="chess_board">
				<xsl:with-param name="checkered">true</xsl:with-param>
				<xsl:with-param name="CellFluentName" select="$track"/>
				<xsl:with-param name="contentArgIdx">3</xsl:with-param>
				<xsl:with-param name="xArgIdx">2</xsl:with-param>
				<xsl:with-param name="yArgIdx">1</xsl:with-param>
				<xsl:with-param name="BoardName" select="$track"/>
			</xsl:call-template>
			
			<!--			Draw horizontal walls -->
			<xsl:variable name="wallname">WALL<xsl:value-of select="$track"/></xsl:variable>
						  
			<xsl:for-each select="fact[prop-f='WALL' and arg[1]=$track and arg[3]!='NONE']">
				<div class="wall">
					<xsl:variable name="xcoord">
						<xsl:choose>
							<xsl:when test="./arg[3]='LEFT'">1</xsl:when>
							<xsl:when test="./arg[3]='RIGHT'">2</xsl:when>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="ycoord" select="6-translate(./arg[2], 'ABCDE', '12345')"/>
					<xsl:attribute name="style">
						left: <xsl:value-of select="($xcoord - 1)*48"/>px;
						top: <xsl:value-of select="-2 + ($ycoord - 1)*48"/>px;
					</xsl:attribute>
				</div>
			</xsl:for-each>
		</div>
		
	</xsl:template>

</xsl:stylesheet>
