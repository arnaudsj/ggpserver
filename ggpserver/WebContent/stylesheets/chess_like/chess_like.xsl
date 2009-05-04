<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	should work for most chess like games (chess, checkers, ...)
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">

		<xsl:variable name="CellFluentName">
			<xsl:choose>
				<xsl:when test="fact[prop-f='CELL' and count(./arg)=3]">CELL</xsl:when>
				<xsl:when test="fact[prop-f='LOCATION' and count(./arg)=3]">LOCATION</xsl:when>
				<xsl:when test="fact[count(./arg)=3]">
					<xsl:value-of select="fact[count(arg)=3]/prop-f"/>
				</xsl:when>
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- paint board -->
		
		<xsl:call-template name="chess_board">
			<xsl:with-param name="CellFluentName" select="$CellFluentName"/>
		</xsl:call-template>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="$CellFluentName"/>
		</xsl:call-template>
		
		<!-- TODO: add disclaimer, e.g.:
			images are from: http://commons.wikimedia.org/wiki/Category:Standard_chess_tiles
			and are freely redistributable under the <a href="http://www.gnu.org/copyleft/fdl.html">GNU Free Documentation License</a>.
		-->

	</xsl:template>
	
	
</xsl:stylesheet>