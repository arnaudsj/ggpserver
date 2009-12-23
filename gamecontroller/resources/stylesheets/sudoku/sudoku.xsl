<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>

	<xsl:template name="print_state">
		<xsl:call-template name="chess_board">
			<xsl:with-param name="CellFluentName" select="'CELL'"/>
			<xsl:with-param name="checkered" select="'no'"/>
			<xsl:with-param name="DefaultCellContent" select="'no'"/>
			<xsl:with-param name="BorderWidth" select="1"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="make_cell_content">
		<xsl:param name="content"/>
		<xsl:param name="piece"/>
		<xsl:param name="background"/>
		<xsl:param name="alt"/>

		<xsl:variable name="background2">
			<xsl:choose>
				<xsl:when test="$content='B'">dark</xsl:when>
				<xsl:otherwise><xsl:value-of select="$background"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="make_chess_img">
			<xsl:with-param name="piece" select="$piece"/>
			<xsl:with-param name="background" select="$background2"/>
			<xsl:with-param name="alt" select="$alt"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
