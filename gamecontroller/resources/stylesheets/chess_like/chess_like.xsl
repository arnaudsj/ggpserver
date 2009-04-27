<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	should work for most chess like games (chess, checkers, ...)
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="print_state">

		<!-- paint board -->
		
		<xsl:call-template name="chess_board">
			<xsl:with-param name="Width">8</xsl:with-param>
			<xsl:with-param name="Height">8</xsl:with-param>
		</xsl:call-template>
		
		<!-- TODO: add disclaimer, e.g.:
			images are from: http://commons.wikimedia.org/wiki/Category:Standard_chess_tiles
			and are freely redistributable under the <a href="http://www.gnu.org/copyleft/fdl.html">GNU Free Documentation License</a>.
		-->

	</xsl:template>
	
	
</xsl:stylesheet>