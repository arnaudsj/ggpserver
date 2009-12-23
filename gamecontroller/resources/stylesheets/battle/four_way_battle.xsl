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
			<xsl:with-param name="Width" select="'6'"/>
			<xsl:with-param name="Height" select="'6'"/>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="content"/>
		<xsl:param name="piece"/>
		<xsl:param name="background"/>
		<xsl:param name="alt"/>

		<xsl:variable name="piece2">
			<xsl:choose>
				<xsl:when test="$content='NP'">j1</xsl:when>
				<xsl:when test="$content='SP'">j0</xsl:when>
				<xsl:when test="$content='NK'">kl</xsl:when>
				<xsl:when test="$content='SK'">kd</xsl:when>
				<xsl:when test="$content='EP'">j2</xsl:when>
				<xsl:when test="$content='WP'">j3</xsl:when>
				<xsl:when test="$content='EK'">k2</xsl:when>
				<xsl:when test="$content='WK'">k3</xsl:when>
				<xsl:otherwise><xsl:value-of select="$piece"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="make_chess_img">
			<xsl:with-param name="piece" select="$piece2"/>
			<xsl:with-param name="background" select="$background"/>
			<xsl:with-param name="alt" select="$alt"/>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
