<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	similar to chess_like but uses knights as pieces
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="content"/>
		<xsl:param name="piece"/>
		<xsl:param name="background"/>
		<xsl:param name="alt"/>

		<xsl:variable name="piece2">
			<xsl:choose>
				<xsl:when test="$content='WHITE'">nl</xsl:when>
				<xsl:when test="$content='BLACK'">nd</xsl:when>
				<xsl:when test="$content='RED'">n2</xsl:when>
				<xsl:when test="$content='GREEN'">n3</xsl:when>
				<xsl:when test="$content='BLUE'">n4</xsl:when>
				<xsl:when test="$content='CYAN' or $content='TEAL'">n5</xsl:when>
				<xsl:when test="$content='YELLOW'">n6</xsl:when>
				<xsl:when test="$content='PINK'">n7</xsl:when>
				<xsl:when test="$content='BROWN' or $content='ORANGE'">n8</xsl:when>
				<xsl:when test="$content='MAGENTA'">n9</xsl:when>
				<xsl:when test="$content='ARROW' or $content='HOLE'">RR</xsl:when>
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