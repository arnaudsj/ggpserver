<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Blocker
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>

	<xsl:template name="print_state">
		
		<!-- paint board(s) -->
		<xsl:call-template name="print_all_chess_boards">
			<xsl:with-param name="checkered" select="'no'"/>
		</xsl:call-template>

	</xsl:template>

	<xsl:template name="make_cell_content">
		<xsl:param name="content"/>
		<xsl:param name="background"/>
		<xsl:param name="alt"/>

		<xsl:variable name="piece">
			<xsl:choose>
				<xsl:when test="$content='BLK'">RR</xsl:when>
				<xsl:when test="$content='CROSSER'">RB</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="make_chess_img">
			<xsl:with-param name="piece" select="$piece"/>
			<xsl:with-param name="background" select="$background"/>
			<xsl:with-param name="alt" select="$alt"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>