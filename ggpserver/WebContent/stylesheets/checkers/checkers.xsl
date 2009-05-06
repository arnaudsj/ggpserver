<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	similar to chess_like but with checkers pieces
	works for checkers*, ...
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="Width" select="'8'"/>
			<xsl:with-param name="Height" select="'8'"/>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="content"/>
		<xsl:param name="piece"/>
		<xsl:param name="background"/>

		<xsl:variable name="piece2">
			<xsl:choose>
				<xsl:when test="$content='WP'">j1</xsl:when>
				<xsl:when test="$content='BP'">j0</xsl:when>
				<xsl:when test="$content='WK'">D1</xsl:when>
				<xsl:when test="$content='BK'">D0</xsl:when>
				<xsl:otherwise><xsl:value-of select="$piece"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="make_chess_img">
			<xsl:with-param name="piece" select="$piece2"/>
			<xsl:with-param name="background" select="$background"/>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>